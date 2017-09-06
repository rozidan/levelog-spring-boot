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
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "levelog.kafka.topic=levelog.t",
        "levelog.kafka.enabled=true",
        "spring.application.name=appTest"})
public class LevelogKafkaTest {

    private static final String TOPIC = "levelog.t";

    @ClassRule
    public static final KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TOPIC);

    @Autowired
    private LevelogProvider levelogProvider;
    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;
    @Autowired(required = false)
    private LevelogKafka listener;

    @Test
    public void kafkaTest() throws InterruptedException {
        assertNotNull(listener);

        levelogProvider.changeLogLevel(LogLevel.INFO, LevelogKafkaTest.class.getName());
        assertTrue(log.isInfoEnabled());

        Message msg = new Message();
        msg.setLogLevel(LogLevel.ERROR);
        msg.setLoggerName(LevelogKafkaTest.class.getName());

        kafkaTemplate.send(TOPIC, msg);
        assertThat(listener.countDownLatch.await(10, TimeUnit.SECONDS)).isTrue();

        TimeUnit.SECONDS.sleep(1);

        assertFalse(log.isInfoEnabled());
        assertTrue(log.isErrorEnabled());
    }

    @Test
    public void kafkaErrorTest() throws InterruptedException {
        kafkaLoadedTest();

        levelogProvider.changeLogLevel(LogLevel.INFO, LevelogKafkaTest.class.getName());
        assertTrue(log.isInfoEnabled());

        kafkaTemplate.send(TOPIC, Message.builder()
                .logLevel(LogLevel.ERROR)
                .loggerName(null)
                .build());
        assertThat(listener.countDownLatch.await(10, TimeUnit.SECONDS)).isTrue();

        TimeUnit.SECONDS.sleep(1);

        assertTrue(log.isInfoEnabled());
    }

    @Test
    public void kafkaErrorEmptyTest() throws InterruptedException {
        kafkaLoadedTest();

        levelogProvider.changeLogLevel(LogLevel.INFO, LevelogKafkaTest.class.getName());
        assertTrue(log.isInfoEnabled());

        kafkaTemplate.send(TOPIC, Message.builder()
                .logLevel(LogLevel.ERROR)
                .loggerName("")
                .build());
        assertThat(listener.countDownLatch.await(10, TimeUnit.SECONDS)).isFalse();
        assertTrue(log.isInfoEnabled());
    }

    @Test
    public void kafkaLoadedTest() {
        assertNotNull(listener);
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableKafka
    public static class Application {

        public ProducerFactory<String, Message> producerFactory() {
            return new DefaultKafkaProducerFactory<>(producerConfigs());
        }

        public Map<String, Object> producerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return props;
        }

        @Bean
        public KafkaTemplate<String, Message> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }

        @Bean
        public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Message>> levelogKafkaContainer() {
            ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            return factory;
        }

        public ConsumerFactory<String, Message> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfigs());
        }

        public Map<String, Object> consumerConfigs() {
            Map<String, Object> propsMap = new HashMap<>();
            propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
            propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
            propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LevelogMessageJsonDeserializer.class);
            propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
            propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return propsMap;
        }

    }
}