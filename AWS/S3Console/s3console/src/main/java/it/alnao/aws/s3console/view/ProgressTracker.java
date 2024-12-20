package it.alnao.aws.s3console.view;

// ProgressTracker.java
@FunctionalInterface
public interface ProgressTracker {
    void onProgress(double progress);
}