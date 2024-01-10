package it.alnao.FTP;

import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Properties;
import java.util.Vector;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
//see https://www.javatpoint.com/java-sftp
/*
  <dependency>
      <groupId>com.jcraft</groupId>
      <artifactId>jsch</artifactId>
      <version>0.1.55</version>
  </dependency>

 */
public  class SftpClientJSch {
    private String server;
    private int port;
    private String user;
    private String password;
    Session jschSession = null;
    Channel channel = null;
    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 5000;

    // constructor
    public SftpClientJSch(String server,int port, String user, String password) {
    	this.server=server;
    	this.port=port;
    	this.user=user;
    	this.password=password;
    }
    public void connect(File key) throws JSchException {
		JSch jsch = new JSch();
		//jsch.setKnownHosts(key.getAbsolutePath());
		jschSession = jsch.getSession(user, server, port);
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");//see https://stackoverflow.com/questions/2003419/com-jcraft-jsch-jschexception-unknownhostkey
		jschSession.setConfig(config);
		if (key!=null) {// authenticate using private key
			jsch.addIdentity( key.getAbsolutePath()); //"/home/javatpoint/.ssh/id_rsa"
		}else {// 	authenticate using password
			jschSession.setPassword(password);
		}
		// 10 seconds session timeout
		jschSession.connect(SESSION_TIMEOUT);
		channel = jschSession.openChannel("sftp");
		// 5 seconds timeout
		channel.connect(CHANNEL_TIMEOUT);
    }
    public void disconnect() {
    	channel.disconnect();
    	jschSession.disconnect();
    }
    public List<String> ls(String path) throws SftpException{
    	ChannelSftp channelSftp = (ChannelSftp)channel;
    	channelSftp.cd(path);
        Vector<LsEntry> filelist = channelSftp.ls(path);
        ArrayList<String> l=new ArrayList<String>();
        for(int i=0; i<filelist.size();i++){
            l.add(filelist.get(i).getFilename());
        }
        return l;
    }
    public boolean putFile(File local, String remoteFile) throws SftpException {
    	ChannelSftp channelSftp = (ChannelSftp)channel;
    	channelSftp.put(local.getAbsolutePath(), remoteFile);
    	//sftp.put(new FileInputStream(file), "remoteFile.txt"); 
    	return true;
    }
}