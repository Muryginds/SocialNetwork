package ru.skillbox.zerone.backend.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class VKWebClientConfig {
  private final VKWebClientProperties properties;

  @Bean
  public WebClient getVKWebClient() {
    var httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
        .doOnConnected(conn -> conn
            .addHandlerFirst(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
            .addHandlerFirst(new WriteTimeoutHandler(10, TimeUnit.SECONDS)))
        .responseTimeout(Duration.ofSeconds(2));

    return WebClient.builder()
        .baseUrl(properties.getRootUrl())
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", properties.getAccessToken()))
        .build();
  }
}
