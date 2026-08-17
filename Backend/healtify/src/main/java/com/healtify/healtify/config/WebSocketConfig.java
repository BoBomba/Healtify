package com.healtify.healtify.config;

import com.healtify.healtify.security.WebSocketAuthChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP nad WebSocketem - kanal z chatem na zywo.
 *
 * Broker jest in-memory. Wystarcza gdy backend chodzi w jednej instancji;
 * przy kilku instancjach trzeba by podpiac zewnetrzny (np. RabbitMQ/ActiveMQ),
 * bo kolejki uzytkownikow nie sa wspoldzielone pomiedzy instancjami.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor authChannelInterceptor;

    public WebSocketConfig(WebSocketAuthChannelInterceptor authChannelInterceptor) {
        this.authChannelInterceptor = authChannelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Ta sama lista origins co w CorsConfig - handshake WebSocketa ma wlasna kontrole
        // pochodzenia i nie korzysta z konfiguracji CORS dla MVC.
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /queue - kolejki prywatne (Spring rozwija /user/queue/... na konkretna sesje).
        registry.enableSimpleBroker("/queue");
        // Wiadomosci od klienta do @MessageMapping.
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Autoryzacja tokenem przy CONNECT - bez tego kazde gniazdo byloby anonimowe
        // i nie dalo by sie ustalic, czyja jest kolejka.
        registration.interceptors(authChannelInterceptor);
    }
}
