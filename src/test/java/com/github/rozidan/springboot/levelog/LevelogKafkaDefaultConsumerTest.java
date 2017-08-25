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
        "levelog.kafka.enabled=true"})
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
