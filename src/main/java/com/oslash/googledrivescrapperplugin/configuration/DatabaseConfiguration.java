package com.oslash.googledrivescrapperplugin.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

@Configuration
public class DatabaseConfiguration {

    @Configuration
    public class PersistenceHibernateConfig{

        @Bean
        public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
            return new PersistenceExceptionTranslationPostProcessor();
        }
    }
}
