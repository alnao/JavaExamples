package exc2Csv;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.OldFileFormatException;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3ObjectEntity;


//https://guides.micronaut.io/latest/micronaut-aws-lambda-eventbridge-event-maven-java.html

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, String> {
    public static String SEPARATOR=";";
    public static String NEW_LINE="\n";
    private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

    @Override
    public String handleRequest(Object e, Context context) {
        if ((e==null)||(context==null)){
            return "event or context null";
        }
        context.getLogger().log("Received event: " + e.getClass() );
        context.getLogger().log("Received event: " + e);
        java.util.LinkedHashMap m1=(java.util.LinkedHashMap) e;
        java.util.LinkedHashMap m3=(java.util.LinkedHashMap) m1.get("detail");
        java.util.LinkedHashMap m4=(java.util.LinkedHashMap) m3.get("bucket");
        java.util.LinkedHashMap m5=(java.util.LinkedHashMap) m3.get("object");
        String bucket = (String) m4.get("name");
        String key = (String) m5.get("key");
        String destFile = key;
        StringBuffer data = new StringBuffer();
        try {
            S3Object response = s3Client.getObject(bucket, key);
            InputStream in = response.getObjectContent();
            //com.amazonaws.util.IOUtils.drainInputStream(in);
            Workbook workbook = null;
            // Get the workbook object for Excel file based on file format
            if (key.endsWith(".xlsx")) {
                context.getLogger().log("workbook xlsx");
                try{
                    workbook = new XSSFWorkbook(in);
                }catch(org.apache.poi.POIXMLException ex){
                    context.getLogger().log("workbook xls POIXMLException");
                    response = s3Client.getObject(bucket, key);
                    in = response.getObjectContent();
                    workbook = new HSSFWorkbook(in);
                }
            } else if (key.endsWith(".xls")) {
                context.getLogger().log("workbook xls");
                workbook = new HSSFWorkbook(in);
            } else {
                return ("File not supported! " + key);
            }
            // Get first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);
            // Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
               Row row = rowIterator.next();
               // For each row, iterate through each columns
               Iterator<Cell> cellIterator = row.cellIterator();
               while (cellIterator.hasNext()) {
                   Cell cell = cellIterator.next();
                   switch (cell.getCellType()) {
                   case Cell.CELL_TYPE_BOOLEAN:
                       data.append(cell.getBooleanCellValue() + SEPARATOR);
                       break;
                   case Cell.CELL_TYPE_NUMERIC:
                       String v=""+cell.getNumericCellValue();
                       if (v.endsWith(".0")) { v=v.substring(0,v.length()-2); }
                       data.append(v + SEPARATOR);
                       break;
                   case Cell.CELL_TYPE_STRING:
                       data.append(cell.getStringCellValue() + SEPARATOR);
                       break;
                   case Cell.CELL_TYPE_BLANK:
                       data.append("" + SEPARATOR);
                       break;
                   default:
                       data.append(cell + SEPARATOR);
                   }
               }
               // appending new line after each row
               data.append(NEW_LINE);
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(data.toString().getBytes());
            destFile=key.replace(".xlsx",".csv").replace(".xls",".csv");
            s3Client.putObject(bucket, destFile, inputStream, new ObjectMetadata());
            context.getLogger().log("destFile ");
            if (workbook!=null){
                workbook.close();
            }
            return destFile;

        } catch (Exception ex) {
            ex.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
            return "Error : " + ex.getMessage();
        }
    }
    //metodo per evitare ClassNotFound UnsupportedFileFormatException
    public void errore(String errore) throws Exception,UnsupportedFileFormatException {
    	if (errore!=null) {
    		throw new OldFileFormatException(errore) {};
    	}
    }
}
