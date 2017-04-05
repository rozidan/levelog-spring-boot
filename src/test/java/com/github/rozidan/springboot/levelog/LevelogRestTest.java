/**
 * Copyright (C) 2017 Idan Rozenfeld the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rozidan.springboot.levelog;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "levelog.rest.path=/levelog", "levelog.rest.enabled=true" })
@SpringBootTest(classes = LevelogRestTest.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class LevelogRestTest {

   private static final String REQUEST_MAPPING = "/levelog";

   @Autowired(required = false)
   private LevelogRest levelogRest;
   @Autowired
   private LevelogProvider levelogProvider;
   @Autowired
   private TestRestTemplate restTemplate;

   @Test
   public void restTest() {
      assertNotNull(levelogRest);

      levelogProvider.changeLogLevel(LogLevel.INFO, LevelogRestTest.class.getName());
      assertTrue(log.isInfoEnabled());

      ResponseEntity<String> exchange = restTemplate.exchange(REQUEST_MAPPING, HttpMethod.POST,
            new HttpEntity<>(Message.builder()
                  .logLevel(LogLevel.ERROR)
                  .loggerName(LevelogRestTest.class.getName())
                  .build()),
            String.class);

      assertEquals(HttpStatus.OK, exchange.getStatusCode());
      assertFalse(log.isInfoEnabled());
      assertTrue(log.isErrorEnabled());
   }

   @Test
   public void restValidationTest() {
      levelogProvider.changeLogLevel(LogLevel.INFO, LevelogRestTest.class.getName());
      assertTrue(log.isInfoEnabled());

      ResponseEntity<String> exchange = restTemplate.exchange(REQUEST_MAPPING, HttpMethod.POST,
            new HttpEntity<>(Message.builder()
                  .logLevel(LogLevel.ERROR)
                  .loggerName(null)
                  .build()),
            String.class);

      assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
      assertTrue(log.isInfoEnabled());
   }

   @Test
   public void restValidationEmptyTest() {
      levelogProvider.changeLogLevel(LogLevel.INFO, LevelogRestTest.class.getName());
      assertTrue(log.isInfoEnabled());

      ResponseEntity<String> exchange = restTemplate.exchange(REQUEST_MAPPING, HttpMethod.POST,
            new HttpEntity<>(Message.builder()
                  .logLevel(LogLevel.ERROR)
                  .loggerName("")
                  .build()),
            String.class);

      assertEquals(HttpStatus.BAD_REQUEST, exchange.getStatusCode());
      assertTrue(log.isInfoEnabled());
   }

   @Configuration
   @EnableAutoConfiguration
   public static class Application {

   }
}