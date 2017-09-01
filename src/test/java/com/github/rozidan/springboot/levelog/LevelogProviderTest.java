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
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringRunner.class)
public class LevelogProviderTest {

    @Autowired
    private LevelogProvider levelogProvider;

    @Test
    public void shouldChangeLevelInfoToError() {
        assertTrue(log.isInfoEnabled());
        levelogProvider.changeLogLevel(LogLevel.ERROR, LevelogProviderTest.class.getName());
        assertFalse(log.isInfoEnabled());
        assertTrue(log.isErrorEnabled());
    }

    @Test(expected = ConstraintViolationException.class)
    public void messageShouldNotPassValidation() {
        levelogProvider.changeLogLevel(Message.builder().logLevel(LogLevel.ERROR).loggerName(null).build());
    }

    @Test(expected = ConstraintViolationException.class)
    public void messageShouldNotPassValidation2() {
        levelogProvider.changeLogLevel(Message.builder().logLevel(null).loggerName(LevelogProviderTest.class.getName()).build());
    }


    @Configuration
    @Import(LevelogProvider.class)
    public static class Application {

    }

}