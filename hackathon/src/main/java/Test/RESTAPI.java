package Test;

import Controller.Login;
import DTO.SosRequest;
import Entity.iNOTIFICATION;
import FireBase.NotificationService;
import Redis.StatusRedis;
import Security.AuthService;
import SecurityAuth.OauthServer;
import WebClient.MLQuery;
import WebClient.WebClientService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class RESTAPI {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final StatusRedis statusRedis;
    private final NotificationService notificationService;
    private final OauthServer oauthServer;
    private final Login login;
    private final ChatClient client;
    private final WebClientService service;

    @PostMapping("/v1")
    public ResponseEntity<?> isValid(@RequestParam @Valid String username,
                                     @RequestParam @Valid String pass) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, pass));

        if (authentication.isAuthenticated()) {
            String token = authService.generateToken(username);
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/test/{id}")
    @Timed(value = "SendNotify", percentiles = {0.5, 0.95, 0.99})
    public CompletableFuture<?> SendNotification(@RequestBody @Valid iNOTIFICATION iNOTIFICATION) {
        return notificationService.SendMessage(iNOTIFICATION);

    }

    @GetMapping("/status")
    public boolean Status(@PathVariable String userid) {
        return statusRedis.isOnline(userid);
    }
    @PostMapping("/chat/{UserId}")
    public ResponseEntity<?>SetOnline(@PathVariable String UserId ){
       statusRedis.setOffline(UserId);
       return ResponseEntity.ok(HttpStatus.OK);

    }
    @PostMapping("/chat/WhoAmi")
    public ResponseEntity<?>iSLoggedIn(@RequestParam String EmailId,@RequestParam String ProviderId){
        return ResponseEntity.ok(login.IsLoggedIn(EmailId,ProviderId));
    }

    @PostMapping("/Sos")
    @RateLimiter(name = "ExceedMsgLimit", fallbackMethod = "LimitFallback")
    public Mono<SosRequest> SosMsg(@PathVariable ("number")String num, @RequestBody String MSG) {
        return login.SosMsg(num,MSG);

    }
    public ResponseEntity<?>LimitFallback(){
        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).build();
     }
     @PostMapping("/Query")
    public Mono<MLQuery>Query(@RequestBody MLQuery mlQuery) {
        return service.PostRequest(mlQuery);

    }

    }



