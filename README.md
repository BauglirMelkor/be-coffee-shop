# Getting Started
Java version 17
Docker version 20.10.17, build 100c701
docker-compose version 1.29.2, build 5becea4c


To run the application write the commands below
cd coffeeshop
mvn clean install
mvn docker:build
docker run -d -p 8080:8080 bestseller/be-coffee-store:latest

In order to check the API you may visit the address below
http://localhost:8080/swagger-ui/index.html#/

Also you may import post man collection below to test the application
be-coffee-shop.postman_collection.json

I added eureka client so the service will register itself to eureka server for service discovery if the
there is a server and it runs on default port in the environment. I didn't include gateway, feign and config 
server to keep the project simple. 

Test coverage is %90. I mostly focused on testing the discount functionality and reporting.

I wrote some comments in the code regarding the decisions I took. 
For example instead of passing the whole basket between user and the application, I store the basket in a variable 
and return it to user. This approach will cause problems in an environment with multiple user and threads but I 
wanted to keep things simple.



