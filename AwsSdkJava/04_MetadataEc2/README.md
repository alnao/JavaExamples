# App 04_MetadataEc2

See [AWS Documentation](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-ec2-IMDS.html) 


This project contains a maven application with [AWS Java SDK 2.x](https://github.com/aws/aws-sdk-java-v2) dependencies.


## To create project
On Visual studio code
``` > Create Java procject ```
and see  [AWS Documentation](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-ec2-IMDS.html) 

To create Jar added "jar-with-dependencies" on pom and run:


```mvn clean compile assembly:single```


## To run on EC2
To run on EC2 Linux instance


```
sudo yum install -y amazon-efs-utils git maven
git clone https://github.com/alnao/JavaExamples.git
cd JavaExamples/AwsSdkJava/04_MetadataEc2/
mvn clean compile assembly:single
java -jar ./target/*.jar
```


# AlNao.it
Nessun contenuto in questo repository è stato creato con IA o automaticamente, tutto il codice è stato scritto con molta pazienza da Alberto Nao. Se il codice è stato preso da altri siti/progetti è sempre indicata la fonte. Per maggior informazioni visitare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*