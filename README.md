# multi-testing
Application utilising various testing tools and frameworks

This application currently tests using Cucumber and JUnit 5, alongside Spock in Groovy

## Project structure

The source files have been divided according to their feature, with each showcasing a particular problem. They are as follows:

* Simple, the first implemented, consisting of a pair of post and get endpoints, and a domain object of id and name fields. The id field is an always-generated integer.
* Large, being functionally identical to Simple, but with a domain object of 21 fields, and a string id field. The purpose was to observe how the testing types code under larger-scaled payloads.
* Letter, which consumes messages from a Kafka queue