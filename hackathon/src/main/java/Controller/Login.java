package Controller;


import DTO.SosRequest;
import Entity.Oauth;
import JPA.OauthRepository;
import JPA.UserRepository;
import Security.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public  class Login {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final OauthRepository oauthRepository;

    @Qualifier("fastSms")
   private final WebClient webClient;
    @Value("${fast2sms.RESTAPI.key}")
    private String key;

    public Oauth IsLoggedIn(String EmailId, String ProviderId) {
        return oauthRepository.FindByProviderIdAndProviderName(EmailId, ProviderId)
                .orElseThrow(() -> new UsernameNotFoundException("NOT Logged In"));

    }
 public Mono<SosRequest>SosMsg(String msg,String number){
        return webClient.post().
                uri(uriBuilder -> uriBuilder
                        .path("/bulkV2")
                        .queryParam("RESTAPI-key",key)
                        .queryParam("msg",msg)
                        .queryParam("number",number)
                        .build())
                .retrieve()
                .bodyToMono(SosRequest.class)
                .timeout(Duration.ofMillis(40))
                .retry(5);
 }

}





    //SIGNUP



