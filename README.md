# multi-testing
Application utilising various testing tools and frameworks

This application currently tests using Cucumber and JUnit 5, alongside Spock in Groovy

## Project structure

The source files have been divided according to their feature, with each showcasing a particular problem. They are as follows:

* Simple, the first implemented, consisting of a pair of post and get endpoints, and a domain object of id and name fields. The id field is an always-generated integer.
* Large, being functionally identical to Simple, but with a domain object of 21 fields, and a string id field. The purpose was to observe how the testing types code under larger-scaled payloads.
* Letter, which consumes and stores messages from a Kafka queue
* Potato, which consumes messages from a kafka queue and passes them on to an external http client
* Labour + Fruit, which takes fruit request and puts it in a Kafka queue, with a generated id and created timestamp

A few different methods of testing have been used. Some purely overlap and are examples of contrasting techniques, whilst others show off useful features in their own right. The first group is around various integration testing tools. The second is a mismatch of tools that border the line between unit test and integration test and concern testing small parts of the application or even just database.

## Integration testing

The tools displayed are:

* Cucumber
* Junit 5 @SpringBootTests
* Spock (Groovy) @SpringBootTests

Now, there is nothing limiting Junit5 or Spock to integration tests, but the benefit of using groovy is its conciseness and string-related features which come into the fore in that context.

It is my opinion that cucumber is too cumbersome and suffers greatly in separating description from content and emphasising reusable steps. It does not handle the use of different data in different tests gracefully, with the solutions being to either duplicate steps, abstract different data through a key used in the feature files, or to add data directly from the feature file. The scaling very poorly with increasing data size. In addition, step definition files become difficult to follow and the writing of new features/scenarios requires the knowledge of much of what currently exists to prevent the duplication or inconsistency of steps.

Next, Junit 5 @SpringBootTests address a few of those issues, as tests are more self-contained, but suffers from the verbosity of Java and the need for either large data builders or to store the test data in separate files independent of the tests in which they are being used. Simply put, readability is the biggest drawback - it's damn difficult to see through the leaves.

This is where Spock comes in, a testing framework for the Groovy JVM language. It is more concise, utilising simpler syntax for mocking and asserting values, and allows for multiline and templated strings. This last point has the amazing ability of clearly representing the request and response payloads in code in the tests themselves, in the context they are being used. For the requests, a developer can copy and paste the payload into Postman (etc) and expect it to be valid. For responses, it is made clear what populates each field.

## Other tools

* @JooqTest, used either on the database itself or on the repository/DAO
* @WebMvcTest, used test the controller via the HTTP layer
* Spock (Groovy) for unit testing