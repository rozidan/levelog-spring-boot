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

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

/**
 * Created by Idan Rozenfeld
 */
@Configuration
public class LevelogAutoConfiguration {

    @Bean
    @ConditionalOnMissingClass
    public LevelogProvider levelogProvider() {
        return new LevelogProvider();
    }

    @ConditionalOnProperty(prefix = "levelog", name = "kafka.enabled", havingValue = "true")
    @ConditionalOnBean(annotation = EnableKafka.class)
    @Configuration
    @Import(LevelogKafka.class)
    public static class LevelogKafkaConfiguration {

        private String broker;
        private String appName;

        public LevelogKafkaConfiguration(@Value("${levelog.kafka.servers:localhost:9092}") String broker,
                                         @Value("${spring.application.name}") String appName) {
            this.broker = broker;
            this.appName = appName;
        }

        @ConditionalOnMissingBean(name = "levelogKafkaContainer")
        @Bean
        public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Message>> levelogKafkaContainer() {
            ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            factory.setConcurrency(1);
            return factory;
        }

        public ConsumerFactory<String, Message> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfigs());
        }

        public Map<String, Object> consumerConfigs() {
            Map<String, Object> propsMap = new HashMap<>();
            propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
            propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
            propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LevelogMessageJsonDeserializer.class);
            propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, appName + " #levelog");
            propsMap.put(ConsumerConfig.CLIENT_ID_CONFIG, appName);
            return propsMap;
        }
    }

    @ConditionalOnProperty(prefix = "levelog", name = "rest.enabled", havingValue = "true")
    @Configuration
    @Import(LevelogRest.class)
    public static class LevelogRestConfiguration {

    }
}
