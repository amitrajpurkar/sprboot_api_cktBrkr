# atjax
this sample application takes inspiration from a successful implementation seen at a place

## what project/ app is this?
* this is an example of java application, having simple/ sample rest APIs 
* the application shows desirable quality control features typically anticipated in an production ready app supporting critical business features
* target audience -- a java developer having good experience working with Java tech stack and using this as a reference after gap of several years working in different areas of SDLC

## what will you need to have this app up and running
here are the bare minimum tools you need on your machine to get this running on your machine
* java JDK version 14.0.2 
* gradle version 6.6
* git version 2.33.1.windows.1
* SpringBoot version 2.3.3.RELEASE
* Mongo DB version

### for ideal backend services
Following non-functional features will make a big difference in producing a High Quality product that can last test of time.
below usecases/ reference implementations, can be seen
* logEvents for capturing meaningful data for machine learning
* logRecord that records severside details and contains individual logEvent
* logForwarder that writes or delegates log-writing to platforms like Splunk or ELK so that meaningful dashboards, controls/ alerts can be done
* Aspects for intercepting at controller, service layer entry-exit points
* use aspects for capturing logs, measuring response times, using circuit breakers for SLA timeouts
* circuitBreaker implementation for wait-periods or timeouts
* configProperties making possible grouping of application properties as java objects
* swagger documentation
* unit test cases for documenting different scenarios that needs to be handled by service-layer and controllers
* arch-unit test cases to ensuring the application code structure is following desired patterns
* jacoco test coverage to keep tap of testability and coverage of your code



