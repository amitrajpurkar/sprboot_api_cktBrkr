step 1:
* analyze the codebase
* this application is java, springboot application, having rest API endpoints
* the project is designed to give example of circuit breaker, aspectj, mongodb, swagger, jacoco, archunit, logback
* Refer to Readme.md for more details
* It was written in 2020 using Java 14.0.2  and Spring Boot 2.3.3.RELEASE, compiled/ build using gradle 6.6
* it was intended to be a sample application to show desirable quality control features typically anticipated in an production ready app supporting critical business features
After analyzing this application as if you are a Senior Developer and Java Architect, write a detailed report on this application in First_Analysis.md file in root directory

step 2:
* describe a detailed step by step plan to upgrade this application to latest java and springboot versions. Write this plan in Upgrade_Plan.md file in root directory
* include in the plan upgrade to gradle version 9. This machine has java 21 installed; Append this to Upgrade_Plan.md file in root directory

step 3:
* describe a separate and detailed step by step plan to upgrade circuitbreaker to use resiliency4j. Write this plan in Upgrade_CircuitBreaker_Plan.md file in root directory

step 4:
* describe a detailed step by step plan to change the mongodb database to use latest H2 in-memory database. Write this plan in Upgrade_H2_Plan.md file in root directory

step 5:
* using the repository classes and model, create controller endpoints for Product entity. create endpoints for list existing products, create new product, update existing product, delete existing product

step 6: 
* create a frontend UI in reactjs as SPA to consume product endpoints

step 7:
* add mkdocs to the application to document the application

step 8:
* this machine has coderabbit installed. Use it to run a thorough code analysis and code review of the application and generate a report

