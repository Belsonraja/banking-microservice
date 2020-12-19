# banking-microservice
# Simple Banking Application based on micro-service architecture which communicate through events.

### <ins>Modules:<ins>
1. Accounts Microservice
2. Transaction Microservice
3. Eureka Discovery Server
4. Zuul Gateway Proxy

### <ins>Tech Stacks:<ins>
1. `Java 8`
2. `Springboot`
3. `Spring JPA`
4. `Sping Cloud Security with JWT Authentication`
5. `Kafka`
6. `H2 Database`
7. `Eureka Discovery server (Service registry)`
8. `Zuul Gateway Proxy`
9. `Feign Client`
10. `JUnit4`
11. `Docker`
12. `Jenkins`
13. `Openshift (Kubernetes)`
14. `Swagger`
15. `Jacacco for code coverage in local` 
16. `Sonarlint for static code analysis in local`
17. `Zipkin & Sleuth for distributed tracing`

### <ins>APIs:<ins>

#### <ins>Eureka Server:<ins>
http://localhost:8761/

#### <ins>Zipkin:<ins>
http://localhost:9411/zipkin/

##### To run Zipkin:
java -jar zipkin.jar

### <ins>accounts-service:<ins>
#### H2 Console:
`http://localhost:8762/accounts-service/h2-console`

#### Create Account: `POST`
`http://localhost:8762/accounts-service/accounts/createAccount`

`{
  "email": "oracle@email.com",
  "name": "oracle",
  "password": "oracle@123"
}`

#### Validate Account: `POST`
`http://localhost:8762/accounts-service/accounts/validateAccount`

`{
"username": "google",
"password": "google@123"
}`

#### Check Account Balance: `POST`
`http://localhost:8762/accounts-service/accounts/checkAccountBalance/`

`accountId`

### <ins>transaction-service:<ins>

#### H2 Console:
`http://localhost:8762/transaction-service/h2-console`

#### Get JWT authentication tocken: `POST`
`http://localhost:8762/transaction-service/authenticate`

`{
"username": "google",
"password": "google@123"
}`

To access below api's , `JWT Authenticcation token` should be generated from above API and the generated JWT token should be added in request header as Authorization with prefix Bearer 

#### Credit Amount to account: `POST`
`http://localhost:8762/transaction-service/transaction/creditAmount`

`{
	"accountId": 1,
	"amount": 100
}`

####  Debit Amount from account: `POST`
`http://localhost:8762/transaction-service/transaction/debitAmount`

`{
	"accountId": 1,
	"amount": 100
}`

#### Get Account balance for the accountId: `POST`
`http://localhost:8762/transaction-service/transaction/getBalance`

`accountId`


## <ins>Kafka commands:<ins>
Below commands should be run from the directory where the downloaded kafka setup folders are placed.
#### Start Zookeeper:
`.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties`

#### Start kafka-server:
`.\bin\windows\kafka-server-start.bat .\config\server.properties`

#### Create topic with name oracle_payments:
`.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic oracle_payments`

#### Consume topic in console for monitoring purpose:
`.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic oracle_payments --from-beginning`

## <ins>Sequence to start application:<ins>
1. Start Zipkin
2. Start ZooKeeper and Kafka
3. Start Eureka Server
4. Start Zuul gateway proxy
5. Start all other microservices
