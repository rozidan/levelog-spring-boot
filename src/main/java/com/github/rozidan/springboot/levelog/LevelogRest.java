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

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Idan Rozenfeld
 */
@RestController
public class LevelogRest {

   private final LevelogProvider levelogProvider;

   @Autowired
   public LevelogRest(LevelogProvider levelogProvider) {
      this.levelogProvider = levelogProvider;
   }

   @PostMapping("${levelog.rest.mapping:/levelog}")
   public ResponseEntity<String> changeLogLevel(@RequestBody Message message) {
      try {
         levelogProvider.changeLogLevel(message);
      } catch (ConstraintViolationException ex) {
         StringBuilder errStr = new StringBuilder();
         ex.getConstraintViolations().stream().forEach((v) -> errStr.append(v.getMessage()).append("\n"));
         return ResponseEntity.badRequest().body(errStr.toString());
      }

      return ResponseEntity.ok("");

   }

}
