package Response;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
@NullMarked
public class ResponseDTO {
    @Id
    private Long AuthId;
    @Min(value = 1)
    private String providerName;
    private String providerId;
    @CreationTimestamp
    private LocalDateTime CreatedAt;

    public ResponseDTO(String providerId, String provider,  LocalDateTime createdAt ){}

}
