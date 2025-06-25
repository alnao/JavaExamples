package it.alnao.javaexamples.awssdk.restModel;

public class ApiResponse {
    private boolean success;
    private String message;
    private String arn;

    public ApiResponse(boolean success, String message, String arn) {
        this.success = success;
        this.message = message;
        this.arn = arn;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getArn() { return arn; }
}