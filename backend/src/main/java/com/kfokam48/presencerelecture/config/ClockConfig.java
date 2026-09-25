package com.kfokam48.presencerelecture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Clock injectable, pour pouvoir controler le temps dans les tests
 * (RG1 : expiration du code 15 minutes apres l'ouverture).
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
