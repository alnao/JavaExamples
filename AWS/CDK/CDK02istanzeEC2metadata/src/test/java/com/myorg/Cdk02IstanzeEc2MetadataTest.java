// package com.myorg;

// import software.amazon.awscdk.App;
// import software.amazon.awscdk.assertions.Template;
// import java.io.IOException;

// import java.util.HashMap;

// import org.junit.jupiter.api.Test;

// example test. To run these tests, uncomment this file, along with the
// example resource in java/src/main/java/com/myorg/Cdk02IstanzeEc2MetadataStack.java
// public class Cdk02IstanzeEc2MetadataTest {

//     @Test
//     public void testStack() throws IOException {
//         App app = new App();
//         Cdk02IstanzeEc2MetadataStack stack = new Cdk02IstanzeEc2MetadataStack(app, "test");

//         Template template = Template.fromStack(stack);

//         template.hasResourceProperties("AWS::SQS::Queue", new HashMap<String, Number>() {{
//           put("VisibilityTimeout", 300);
//         }});
//     }
// }
