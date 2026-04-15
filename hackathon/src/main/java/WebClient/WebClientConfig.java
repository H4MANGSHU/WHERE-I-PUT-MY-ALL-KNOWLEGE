package WebClient;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;


@Configuration
public class WebClientConfig {

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("ML-pool")
                .maxConnections(50)                   //maX connection
                .maxIdleTime(Duration.ofSeconds(20))   // Time to keep idle connection alive
                .maxLifeTime(Duration.ofMinutes(5))    // Total age of a connection
                .pendingAcquireTimeout(Duration.ofSeconds(60)) // How long to wait for a free connection
                .evictInBackground(Duration.ofSeconds(30))
                .build();
    }
    @Bean
    public HttpClient httpClient(ConnectionProvider connectionProvider){
        return HttpClient.create(connectionProvider)
                .compress(true)
                .responseTimeout(Duration.ofSeconds(7));
    }
    @Bean
    public WebClient webClient(ConnectionProvider connectionProvider ,HttpClient httpClient){
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
