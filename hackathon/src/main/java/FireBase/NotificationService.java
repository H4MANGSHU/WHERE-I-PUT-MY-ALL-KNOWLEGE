package FireBase;

import Entity.iNOTIFICATION;
import THREADCONFIG.ThreadConfig;
import com.google.api.client.util.Value;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private  FirebaseMessaging firebaseMessaging;
    @Qualifier("HighThread")
    private  ThreadConfig threadConfig;



    public CompletableFuture<String> SendMessage(iNOTIFICATION iNOTIFICATION){

        Notification notification = Notification.builder()
                .setBody(iNOTIFICATION.getBody())
                .setTitle(iNOTIFICATION.getTitle())
                                      .build();

        Message message = Message.builder()
                .setToken(iNOTIFICATION.getToken())
                .setNotification(notification)
                .putAllData(iNOTIFICATION.getData())
                .build();
            CompletableFuture.supplyAsync(()->{
                try {
                   return firebaseMessaging.send(message);
                } catch (FirebaseMessagingException e) {
                    throw new RuntimeException(e);
                }
            }, (Executor) threadConfig).orTimeout(20, TimeUnit.SECONDS);

        return null;
    }



    }


