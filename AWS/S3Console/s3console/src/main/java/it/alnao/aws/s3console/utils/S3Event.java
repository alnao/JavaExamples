package it.alnao.aws.s3console.utils;

// S3Event.java
public class S3Event {
    private final S3EventType type;
    private final Object data;

    public S3Event(S3EventType type) {
        this(type, null);
    }

    public S3Event(S3EventType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public S3EventType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
