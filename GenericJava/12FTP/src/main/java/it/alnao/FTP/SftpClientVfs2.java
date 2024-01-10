package it.alnao.FTP;

import java.io.File;
import java.net.URI;
//import java.net.URISyntaxException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
//import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
//import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
//import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

/* see https://www.north-47.com/sftp-file-transfers-with-java/
 * 
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-vfs2 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-vfs2</artifactId>
    <version>2.9.0</version>
</dependency>
* 
 * see https://gist.github.com/davengeo/e13da8c604a3a062b3b7
 * */

public class SftpClientVfs2{
    private String server;
    private int port;
    private String user;
    private String password;
    private String privateKey;

    // constructor
    public SftpClientVfs2(String server,int port, String user, String password) {
    	this.server=server;
    	this.port=port;
    	this.user=user;
    	this.password=password;
    	this.privateKey=null;
    }
    public void setKey(String keyPath) {
    	this.privateKey=keyPath;
    }
    private URI getConnection(String path) throws Exception {
    	if (password!=null) {
    		return new URI("sftp", user + ":" + password, server, port,  path, null, null);
    	}else {
    		return new URI("sftp", user, server, port,  path, null, null);
    	}
    	//return "sftp://" + user + ":" + password+"@" + server + ":" + port + "";
        //FileObject connection = fileSystemManager.resolveFile(sftpUri.toString(), opts);
    }	   
    public List<String> ls(String path) throws Exception {
        StandardFileSystemManager fsManager = new StandardFileSystemManager();
        fsManager.init();//Initializes the file manager
        FileSystemOptions opts = new FileSystemOptions();
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
//        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
        String sftpUri = getConnection(path).toString() ;
        //IdentityInfo identityInfo = new IdentityInfo(new File(privateKey));
        //SftpFileSystemConfigBuilder.getInstance().setIdentityInfo(opts, identityInfo);
        File[] identities = { new File(privateKey) };
        SftpFileSystemConfigBuilder.getInstance().setIdentities(opts, identities);
        FileObject localFileObject=fsManager .resolveFile (sftpUri, opts );
        FileObject[] children = localFileObject.getChildren();
        ArrayList<String> l=new ArrayList<String>();
        for ( int i = 0; i < children.length; i++ ){
            l.add ( children[ i ].getName().getBaseName() );
        }        
        fsManager.close();
        return l;
    }
}
