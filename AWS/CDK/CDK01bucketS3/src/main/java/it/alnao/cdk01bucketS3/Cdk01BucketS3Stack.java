package it.alnao.cdk01bucketS3;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;
import software.amazon.awscdk.CfnOutput;

public class Cdk01BucketS3Stack extends Stack {
    public Cdk01BucketS3Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public Cdk01BucketS3Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create S3 bucket
        Bucket bucket = new Bucket(this, "S3Bucket", BucketProps.builder()
                .bucketName(this.getStackName() + "-bucket")
                .versioned(true)
                .build());

        // Output
        CfnOutput.Builder.create(this, "BucketName")
                .description("Nome del bucket S3 creato")
                .value(bucket.getBucketName())
                .build();
    }
}


