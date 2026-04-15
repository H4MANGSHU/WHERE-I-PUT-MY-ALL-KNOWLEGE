package TimeStamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Stamps {
    @CreatedBy
    private UUID CreatedBy;
    @LastModifiedBy
    private UUID UpdateBy;
}
