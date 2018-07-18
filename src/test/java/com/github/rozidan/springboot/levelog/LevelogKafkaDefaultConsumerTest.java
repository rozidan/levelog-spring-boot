/**
 * Copyright (C) 2018 Idan Rozenfeld the original author or authors
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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "levelog.kafka.servers:localhost:19092",
        "levelog.kafka.enabled=true",
        "spring.application.name=appTest"})
public class LevelogKafkaDefaultConsumerTest {

    @Autowired(required = false)
    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Message>> levelogKafkaContainer;

    @Test
    public void setCustomServersWithTheDefaultConsumer() {
        assertThat(levelogKafkaContainer).isInstanceOf(ConcurrentKafkaListenerContainerFactory.class);
        ConcurrentKafkaListenerContainerFactory<String, Message> container = (ConcurrentKafkaListenerContainerFactory<String, Message>) levelogKafkaContainer;

        assertThat(container.getConsumerFactory()).isInstanceOf(DefaultKafkaConsumerFactory.class);
        DefaultKafkaConsumerFactory<String, Message> Consumer = (DefaultKafkaConsumerFactory<String, Message>) container.getConsumerFactory();

        assertThat(Consumer.getConfigurationProperties().get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("localhost:19092");

    }

    @Configuration
    @EnableAutoConfiguration
    @EnableKafka
    public static class Application {

    }
}
