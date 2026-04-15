package WebSocket;

import Entity.Chat;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashSet;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private static  Chat chat1;
    @MessageMapping("/chat.send")
    @Timed(value = "send",percentiles = {0.5, 0.95, 0.99})
    public void SendMsg(@Payload Chat chat){
        log.info("get received{}",chat.getChatId());
        simpMessagingTemplate.convertAndSend("/topic/room",chat.getChatId());
    }
    @MessageMapping("/chat.typing")
    @SendTo("/topic/room")
    @Timed(value = "rec",percentiles = {0.5, 0.95, 0.99})
    public void ReceiveMsg(@Payload Chat chat){
        System.out.println("Received: "+chat.getChatId());
        simpMessagingTemplate.convertAndSend("/topic/room",chat.getChatId()+"is ded");

    }
}
