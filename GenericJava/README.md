# Generic Java Examples

<a href="https://www.alnao.it/javaee/"> 
        <img src="https://img.shields.io/badge/alnao-.it-blue?logo=amazoncloudwatch&logoColor=A6C9E2" height="25px">
        <img src="https://img.shields.io/badge/Java-ED8B00?style=plastic&logo=java&logoColor=white" height="25px"/>
        <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=plastic&logo=SpringBoot&logoColor=white" height="25px" />
</a>

## Projects:

- 01maven created with 
        
      mvn archetype:generate -DgroupId=it.alnao -DartifactId=01maven

   to compile

      mvn clean dependency:copy-dependencies package
- 02webModule created with 
        
      mvn archetype:generate -DgroupId=it.alnao -DartifactId=02webModule -DarchetypeArtifactId=maven-archetype-webapp 
- 03j2ee 


## Genenric commands for Java with Apache Maven:
 
To create project
 
```
mvn archetype:generate -DgroupId=it.alnao -DartifactId=progettoJava
```

To compile
 
```
mvn clean package
```

To run in local

```
java -jar target/*.jar

```

# Licence
See <a href="https://github.com/alnao/JavaExamples/blob/master/LICENSE">LICENSE</a> file for details
