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
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Service;

import javax.validation.*;
import java.util.Set;

/**
 * Created by Idan Rozenfeld
 */
@Slf4j
@Service
public class LevelogProvider {

    /**
     * Validate the message and change log level according to message data.
     *
     * @param message The message
     */
    public void changeLogLevel(Message message) {
        validateMessage(message);
        changeLogLevel(message.getLogLevel(), message.getLoggerName());
        log.info("Log level of '" + message.getLoggerName() + "' is now " + message.getLogLevel());
    }

    void changeLogLevel(LogLevel logLevel, String loggerName) {
        LoggingSystem.get(this.getClass().getClassLoader()).setLogLevel(loggerName, logLevel);
    }

    /**
     * Validate the levelog message.
     *
     * @param message The message
     * @throws ConstraintViolationException If there are validation violations
     */
    public void validateMessage(Message message) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Message>> violations = validator.validate(message);

        if (message.getLoggerName().equals("root")) {
            throw new IllegalArgumentException("Cannot change log level of 'root'.");
        }

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Levelog message is not valid", violations);
        }
    }
}
