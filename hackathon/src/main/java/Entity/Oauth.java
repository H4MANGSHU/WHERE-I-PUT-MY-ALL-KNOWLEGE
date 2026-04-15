package Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Auth")
@Entity
public class Oauth implements Serializable, OAuth2User {

    @Serial
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "provider_name")
    private String provider;
    @Column(name = "provider_id")
    private String providerId;
    @Email
    private String email;
    private String name;
    @Lob
    @Column(name = "ref_token",length = 2099)
    private String RefreshToken;
    @Column(name = "Access_token",columnDefinition = "TEXT")
    private String AcessToken;
    @CreationTimestamp
    private LocalDateTime CreatedAt;
    //WE WILL ADD URL AT LAST

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
