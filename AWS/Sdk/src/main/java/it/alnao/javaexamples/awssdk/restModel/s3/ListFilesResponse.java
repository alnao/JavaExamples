package it.alnao.javaexamples.awssdk.restModel.s3;

import java.util.List;

public class ListFilesResponse {
    private List<String> files;

    public ListFilesResponse(List<String> files) {
        this.files = files;
    }

    public List<String> getFiles() { return files; }
    public void setFiles(List<String> files) { this.files = files; }
}