package it.alnao.aws.s3console.view;
import javax.swing.*;
// S3TransferProgress.java
public class S3TransferProgress implements ProgressTracker {
    private final long totalBytes;
    private long transferredBytes;
    private final ProgressTracker callback;
    
    public S3TransferProgress(long totalBytes, ProgressTracker callback) {
        this.totalBytes = totalBytes;
        this.callback = callback;
    }
    
    public void updateProgress(long bytes) {
        this.transferredBytes += bytes;
        double progress = (double) transferredBytes / totalBytes;
        callback.onProgress(progress);
    }

	@Override
	public void onProgress(double progress) {
		// TODO Auto-generated method stub
		
	}
}
