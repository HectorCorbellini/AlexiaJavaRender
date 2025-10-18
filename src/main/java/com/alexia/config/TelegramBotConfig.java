package com.alexia.config;

import com.alexia.repository.BotCommandRepository;
import com.alexia.repository.TelegramMessageRepository;
import com.alexia.service.BusinessService;
import com.alexia.service.GrokService;
import com.alexia.service.TelegramService;
import com.alexia.telegram.AlexiaTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del bot de Telegram.
 * Crea la instancia del bot pero NO lo registra automáticamente.
 * El registro se realiza manualmente a través de BotManagerService.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final TelegramService telegramService;
    private final BotCommandRepository botCommandRepository;
    private final TelegramMessageRepository telegramMessageRepository;
    private final GrokService grokService;
    private final BusinessService businessService;

    /**
     * Crea la instancia del bot de Telegram.
     * El bot NO se inicia automáticamente - debe ser iniciado manualmente desde el dashboard.
     * 
     * @return instancia configurada de AlexiaTelegramBot
     */
    @Bean
    public AlexiaTelegramBot alexiaTelegramBot() {
        log.info("Creando instancia del bot de Telegram - username=@{}", botUsername);
        log.info("ℹ️  El bot NO se iniciará automáticamente. Usa el dashboard para iniciarlo.");
        
        return new AlexiaTelegramBot(botToken, botUsername, telegramService, 
                botCommandRepository, telegramMessageRepository, grokService, businessService);
    }
}
