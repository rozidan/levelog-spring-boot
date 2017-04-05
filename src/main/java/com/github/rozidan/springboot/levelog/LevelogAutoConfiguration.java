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

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Created by Idan Rozenfeld
 */
@Configuration
public class LevelogAutoConfiguration {

   @Bean
   @ConditionalOnMissingBean
   public LevelogProvider levelogProvider() {
      return new LevelogProvider();
   }

   @ConditionalOnProperty(prefix = "levelog", name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
   @ConditionalOnBean(annotation = EnableKafka.class)
   @Configuration
   @Import(LevelogKafka.class)
   public static class LevelogKafkaConfiguration {
   }

   @ConditionalOnProperty(prefix = "levelog", name = "rest.enabled", havingValue = "true", matchIfMissing = false)
   @Configuration
   @Import(LevelogRest.class)
   public static class LevelogRestConfiguration {

   }
}
