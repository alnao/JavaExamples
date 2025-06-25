# JavaSouthAfricanMobileNumbers
Simple project to manage South African Mobile Numbers: 
- database monbo
- microservices with Java Spring Boot
- frontend web in Angular

## Database mongo 
- Server mongo must be running on 27017 port with a Collection called "collectionSouthAfricanMobileNumbers"
	- User "SouthAfricanMobileNumbers" password "Interlogica"
- To create che collection connect to server
	```
	mongo -u <USER> --authenticationDatabase admin -p --port 27017
	```
	and run 
	```
	> use collectionSouthAfricanMobileNumbers;
	> db.createUser({user:"SouthAfricanMobileNumbers"
			,pwd:"Interlogica"
			,roles:[{ role:"readWrite",db:"collectionSouthAfricanMobileNumbers" }] 
		}); 

	>	db.grantRolesToUser("SouthAfricanMobileNumbers",[{ role: "readWrite", db: "collectionSouthAfricanMobileNumbers" }])
	mongo -u SouthAfricanMobileNumbers --authenticationDatabase collectionSouthAfricanMobileNumbers -p --port 27017
	>	db.SAMN.insertOne({id:'1'});
	>	db.SAMN.find();
	>	db.SAMN.drop();
	```

## API Java Spring Boot
- Microservices ready on public GitHub repository:
	```
	git clone https://github.com/alnao/JavaExamples.git
	```
	(into CorsiVari/SouthAfricanMobileNumbers folder)
- Developerd with "Eclipse" and "Spring Tool Suite 4" but running is possibile with VisualStudioCode or IntelliJ.
- To compile and run:
	```
	mvn install
	mvn package
	java -jar ./target/SouthAfricanMobileNumbers-0.0.1-SNAPSHOT.jar
	```
	or run in Tool Suite 4 or Visual Studio Code with specific plugins.
- To run unit test in Tool Suite 4 use the class:
	```
	SouthAfricanMobileNumbersApplicationTests
	```
- To test with Postman:
	```
	http://localhost:5071/api/southAfricanMobileNumbersController/uploadFile
	```
	with "Content-Type":"application/json"
	request example
	```
	[
		{"id": "6044f51d9bf8d839c89ec1e7","idNumber":"1","phoneNumber":"27720374211","type":"","loadDate":""}
	,
		{"id": "6044f51d9bf8d839c89ec1e8","idNumber":"2","phoneNumber":"37720374211","type":"","loadDate":""}
	,
		{"id": "6044f51d9bf8d839c89ec1e9","idNumber":"3","phoneNumber":"720374211","type":"","loadDate":""}
	]
	```

## Angular Web
- Web project developed with Angualar is disponible on public GitHub repository:
	```
	git clone https://github.com/alnao/AngularReactNodeExamples.git
	```
- developed with Visual studio code and npm
- Coomand to run  
	```
	ng serve
	```

# AlNao.it
Nessun contenuto in questo repository è stato creato con IA oppure è chiaramente indicato dove sono state usate IA generative, tutto il codice è stato scritto con molta pazienza da Alberto Nao. Se il codice è stato preso da altri siti/progetti è sempre indicata la fonte. Per maggior informazioni visitare il sito [alnao.it](https://www.alnao.it/).

## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*