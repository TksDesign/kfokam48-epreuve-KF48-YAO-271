package com.kfokam48.presencerelecture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Random;

/**
 * Beans injectables pour rendre le code testable sans dependre d'une
 * horloge/alea reels : Clock (RG1, expiration) et Random (RG4, tirage
 * au sort du relecteur).
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public Random random() {
        return new Random();
    }
}
