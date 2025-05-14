package org.example;

import org.example.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableAutoConfiguration
public class DrillingHelperBotApplication {

    @Autowired
    private BotConfig botConfig;

    public static void main(String[] args) {
        try {
            ApplicationContext context = SpringApplication.run(DrillingHelperBotApplication.class, args);
            BotConfig botConfig = context.getBean(BotConfig.class);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(botConfig.getBotName(), botConfig.getToken()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
