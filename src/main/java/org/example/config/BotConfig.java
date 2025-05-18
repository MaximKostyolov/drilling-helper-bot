package org.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {

    @Value("${bot.name:DrillingHelperBot}")
    private String botName;
    @Value("${bot.token:7572504508:AAGyiB5N5nE0mDgRCSPLi53Ezin5ApYvMmQ}")
    private String token;

}