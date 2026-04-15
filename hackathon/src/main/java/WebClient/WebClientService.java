package WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientService {

    private final WebClient webClient;
    public Mono<MLQuery>PostRequest(MLQuery mlQuery){
        return webClient.post()
                .uri("")
                .bodyValue(mlQuery)
                .retrieve().
                bodyToMono(MLQuery.class)
                .retry(3)
                .doOnError(throwable-> {
                    log.info("QUERIES & QUERY ID {}",mlQuery.getQueryId() //will use db if needed
                            ,mlQuery.getQuery(),
                            throwable.getMessage());
        });

    }
}
