package Redis;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusRedis {
    private static final String PREFIX_ONLINE_USERS = "online:";
    private static final String PREFIX_OFFLINE_USERS = "offline:";
    private ChannelTopic status_topic;
    private final RedisTemplate<String, String> redisTemplate;
    private  UserStatus userStatus;



    public void setOnline(String userId) {
        try {
            var OnlineKey = PREFIX_OFFLINE_USERS + userId;
            redisTemplate.opsForValue().set(PREFIX_ONLINE_USERS + userId, "online", 30, TimeUnit.SECONDS);
            redisTemplate.delete(OnlineKey);
            redisTemplate.convertAndSend(status_topic.getTopic(), userId + ":online");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        public void setOffline(String userId) {
            try {
                var OfflineKey = PREFIX_ONLINE_USERS + userId;
                redisTemplate.opsForValue().set(PREFIX_OFFLINE_USERS + userId, "offline", 30, TimeUnit.SECONDS);
                redisTemplate.delete(OfflineKey);
                redisTemplate.convertAndSend(status_topic.getTopic(), userId + ":offline");

            } catch (Exception e) {
                log.info("user is offline {}" ,
                        e.getStackTrace());


            }
        }
        public boolean isOnline(String userId) {
        if (redisTemplate.hasKey(PREFIX_ONLINE_USERS + userId)) {
            return true;
        }
        return false;
    }


    public boolean isOffline(String userId) {
        if (redisTemplate.hasKey(PREFIX_OFFLINE_USERS + userId)) {
            return true;
        }
        return false;
    }
    public String GetStatus(String userId) throws Exception {
     return (String) redisTemplate.opsForValue().get("status:"+userId);
        }

    }

