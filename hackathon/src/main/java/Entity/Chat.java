package Entity;


import E2EE.HashConvert;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@Table(name = "chat_v1")
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ChatId;
    @Column(name = "user_name")
    private String username;
    @CreationTimestamp
    private Instant sendAt;
    @Lob
    private String Message;


}
