package it.alnao.javaexamples.awssdk.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.alnao.javaexamples.awssdk.restModel.ApiResponse;
import it.alnao.javaexamples.awssdk.restModel.s3.CreateBucketRequest;
import it.alnao.javaexamples.awssdk.restModel.s3.DeleteBucketRequest;
import it.alnao.javaexamples.awssdk.restModel.s3.DeleteFileRequest;
import it.alnao.javaexamples.awssdk.restModel.s3.DownloadFileRequest;
import it.alnao.javaexamples.awssdk.restModel.s3.ListFilesRequest;
import it.alnao.javaexamples.awssdk.restModel.s3.ListFilesResponse;
import it.alnao.javaexamples.awssdk.service.S3Service;
import it.alnao.javaexamples.awssdk.utils.RegionUtils;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

    @Autowired
    private S3Service s3Service;

    @GetMapping("/listBuckets")
    public ResponseEntity<?> listBuckets() {
        try {
            List<String> buckets = s3Service.listBuckets();
            return ResponseEntity.ok(buckets);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Errore nel listing dei bucket: " + e.getMessage(), null));
        }
    }

    @PostMapping("/listFiles")
    public ResponseEntity<?> listFiles(@RequestBody ListFilesRequest request) {
        logger.info("S3Controller.listFiles");
        try {
            List<String> files = s3Service.listFiles(request.getBucketName(), request.getPrefix());
            return ResponseEntity.ok(new ListFilesResponse(files));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Errore nel listing del bucket: " + e.getMessage(), null));
        }
    }

    @PostMapping("/deleteFile")
    public ResponseEntity<?> deleteFile(@RequestBody DeleteFileRequest request) {
        try {
            String result = s3Service.deleteFile(request.getBucketName(), request.getKey());
            return ResponseEntity.ok(new ApiResponse(true, result, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Errore nella cancellazione: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/createBucket")
    public ResponseEntity<?> createBucket(@RequestBody CreateBucketRequest request) {
        logger.info("S3Controller.createBucket");
        try {
            String arn = s3Service.createBucket(
                request.getBucketName(),
                RegionUtils.getRegionOrDefault(request.getRegion()),
                request.isEnableVersioning(),
                request.isEnableEventBridge(),
                request.isEnableServerSideEncryption()
            );

            return ResponseEntity.ok().body(
                new ApiResponse(true, "Bucket creato con successo", arn)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "Errore nella creazione del bucket: " + e.getMessage(), null));
        }
    }

    @PostMapping("/deleteBucket")
    public ResponseEntity<?> deleteBucket(@RequestBody DeleteBucketRequest request) {
        try {
            String result = s3Service.deleteBucketIfEmpty(
                request.getBucketName()
                //,RegionUtils.getRegionOrDefault(request.getRegion())
            );
            boolean success = result.startsWith("Bucket eliminato");
            return ResponseEntity.ok(new ApiResponse(success, result, null));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Errore nella cancellazione del bucket: " + e.getMessage(), null));
        }
    }


    @PostMapping("/downloadFile")
    public void downloadFile(@RequestBody DownloadFileRequest request, HttpServletResponse response) {
        try (InputStream is = s3Service.downloadFile(request.getBucketName(), request.getKey());
            OutputStream os = response.getOutputStream()) {

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + extractFileName(request.getKey()) + "\"");

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
                response.getWriter().flush();
            } catch (Exception ignored) {}
        }
    }

    private String extractFileName(String key) {
        return key.contains("/") ? key.substring(key.lastIndexOf("/") + 1) : key;
    }

/*
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        File file = convertToFile(multipartFile);
        return s3Service.uploadFile(file);
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File convFile = File.createTempFile("temp", null);
        multipartFile.transferTo(convFile);
        return convFile;
    }
*/
}