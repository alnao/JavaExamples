package it.alnao.FTP;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App{
	static String server="XXX.XXX.XXX.XXX";
	static int port=22;
	static String user="username";
	static String passw=null;
	static String pem="C:\\privata.pem";
	static String path="/folder";
    
    public static void main( String[] args ) throws Exception{
    	sftpVfs2Example();
    }

    public static void sftpVfs2Example() throws Exception {
    	System.out.println( "sFTP Vfs2 Client " );
    	SftpClientVfs2 sftpClient=new SftpClientVfs2( server , port , user , passw );
    	sftpClient.setKey( pem );
    	List<String> l=sftpClient.ls( path );
        for (int i=0;i<l.size();i++) {
        	System.out.println( "-" + l.get(i).toString() );
        }
    }

    public static void sftpJschExample() throws Exception {
    	System.out.println( "sFTP Jsch Client " );
    	SftpClientJSch sftpClient=new SftpClientJSch(server, port, user, passw);
    	sftpClient.connect( new File( pem ) );
        List<String> l=sftpClient.ls( path );
        for (int i=0;i<l.size();i++) {
        	System.out.println( "-" + l.get(i).toString() );
        }
        sftpClient.disconnect();
    } 
    
    public static void ftpExample() throws IOException{
        System.out.println( "FTP Client " );
        FtpClient cFftp=new FtpClient(server,port,user,passw);
        cFftp.open();
        List<Object> l=cFftp.listFiles("");
        for (int i=0;i<l.size();i++) {
        	System.out.println( "-" + l.get(i).toString() );
        }
        cFftp.close();
    }
    
}
