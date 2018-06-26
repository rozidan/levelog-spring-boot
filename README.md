# Spring Boot Levelog
> A Spring Boot project that let you set the log level for your application at runtime.

[![Build Status](https://travis-ci.org/rozidan/levelog-spring-boot.svg?branch=master)](https://travis-ci.org/rozidan/levelog-spring-boot)
[![Coverage Status](https://coveralls.io/repos/github/rozidan/levelog-spring-boot/badge.svg?branch=master)](https://coveralls.io/github/rozidan/levelog-spring-boot?branch=master)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Features
Register the Levelog to your Spring Boot application and allow it to automatically add a
service that will help you change application log level at runtime through different APIs such as REST, KAFKA..

## Setup

In order to add logger to your project simply add this dependency to your classpath:

```xml
<dependency>
    <groupId>com.github.rozidan</groupId>
    <artifactId>levelog-spring-boot</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

```groovy
compile 'com.github.rozidan:levelog-spring-boot:1.0.0-SNAPSHOT'
```

and the sonatype public repository:


```groovy
repositories {
    mavenCentral()
    maven { url "https://oss.sonatype.org/content/groups/public" }
    ...
}
```

## Change the log level
##### KAFKA
* Enable KAFKA API: `levelog.kafka.enabled = true`.
* Set the bootstrap servers to be used: `levelog.kafka.servers`.
* Set the topic name: `levelog.kafka.topic` default is `levelog.t`.
* Set the container bean name: `levelog.kafka.container` default is `levelogKafkaContainer`.
* Message format to be use is `Message`, which has two properties:
    * Log level: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF.
    * Logger name: a string represent the logger name.
* Make sure to register a `KafkaListenerContainerFactory` with `LevelogMessageJsonDeserializer` as the value deserializer.



##### REST
* Enable REST API: `levelog.rest.enabled = true`.
* Set the request mapping to be used `levelog.rest.path` default is `/levelog`.
* Request parameter to be use is `Message`.

## License

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)
