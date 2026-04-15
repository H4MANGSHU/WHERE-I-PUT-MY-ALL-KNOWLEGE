package Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_table")
public class iNOTIFICATION {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int MsgId;
    @Column(name = "mails",updatable = true)
    private String email;
    @Column(name = "token_fcm")
    private String token;
    @Column(name = "user_name",nullable = false)
    private String username;
    @CreationTimestamp
    @Column(updatable = false)
    private Instant SendAt;
    @Column(name = "title",nullable = false)
    private String Title;
    @Column(name = "body",columnDefinition = "text")
    private String Body;
    @Column(name = "data_notify")
    @JdbcTypeCode(SqlTypes.JSON)
    public Map<String,String> data;


}
