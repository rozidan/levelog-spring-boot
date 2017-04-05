# Spring Boot Levelog Starter

> A Spring Boot starter that let you set the log level for your application at runtime.

## Features
Register the Levelog to your Spring Boot application and allow it to automatically add a
service that will help you change application log level at runtime through different APIs such as REST, KAFKA..

## Setup

In order to add logger to your project simply add this dependency to your classpath:

```xml
<dependency>
    <groupId>com.github.rozidan</groupId>
    <artifactId>spring-boot-starter-levelog</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```groovy
compile 'com.github.rozidan:spring-boot-starter-levelog:1.0.0-SNAPSHOT'
```

## Change the log level

Choose the API you what levelog to use it service:

```properties
levelog.apis = REST, KAFKA
levelog.rest.path = /levelog
levelog.kafka.topic = levelog.t
levelog.kafka.container = levelogKafkaContainer
```

##### KAFKA
* Default topic name is 'levelog.t'.
* Message format to be use is `LevelogMessage`, which has two properties:
    * Log level: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF.
    * Logger name: a string represent the logger name.
* Make sure to register a `KafkaTemplate` and `KafkaListenerContainerFactory` with `LevelogMessage` as value.
	* The `KafkaListenerContainerFactory` should be named "levelogKafkaContainer"
	* Use `LevelogMessageJsonDeserializer` as deserializer class.

##### REST
* Default request mapping is '/levelog'.
* Request parameter to be use is `LevelogMessage`.

## License

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)
