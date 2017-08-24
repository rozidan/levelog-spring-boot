# Spring Boot Levelog Starter
> A Spring Boot starter that let you set the log level for your application at runtime.

[![Build Status](https://travis-ci.org/rozidan/levelog-spring-boot-starter.svg?branch=master)](https://travis-ci.org/rozidan/levelog-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/rozidan/levelog-spring-boot-starter/badge.svg?branch=master)](https://coveralls.io/github/rozidan/levelog-spring-boot-starter?branch=master)

## Features
Register the Levelog to your Spring Boot application and allow it to automatically add a
service that will help you change application log level at runtime through different APIs such as REST, KAFKA..

## Setup

In order to add logger to your project simply add this dependency to your classpath:

```xml
<dependency>
    <groupId>com.github.rozidan</groupId>
    <artifactId>levelog-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```groovy
compile 'com.github.rozidan:levelog-spring-boot-starter:1.0.0-SNAPSHOT'
```

## Change the log level

Choose the API you what levelog to use:

```properties
levelog.rest.enabled = false
levelog.kafka.enabled = false
levelog.rest.path = /levelog
levelog.kafka.topic = levelog.t
levelog.kafka.container = levelogKafkaContainer
```

##### KAFKA
* Default topic name is 'levelog.t'.
* Message format to be use is `Message`, which has two properties:
    * Log level: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF.
    * Logger name: a string represent the logger name.
* Make sure to register a `KafkaTemplate` and `KafkaListenerContainerFactory` with `Message` as value.
	* The `KafkaListenerContainerFactory` should be named "levelogKafkaContainer"
	* Use `LevelogMessageJsonDeserializer` as deserializer class.

##### REST
* Default request mapping is '/levelog'.
* Request parameter to be use is `Message`.

## License

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)
