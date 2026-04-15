package WebClient;

import jakarta.persistence.Column;
import jdk.jfr.Timestamp;
import lombok.Data;

import java.time.Instant;

@Data
public class MLQuery {

    private Long QueryId;
    private String Query;
    @Timestamp
    private Instant SendAt;
    @Column(name = "user_id")
    private int user_id;

}
