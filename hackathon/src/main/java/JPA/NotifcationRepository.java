package JPA;

import Entity.iNOTIFICATION;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotifcationRepository extends JpaRepository<Integer, iNOTIFICATION> {

    @Query(value = "SELECT DISTINCT e.emails from notification_table AS e where e.emails=:Emails ORDER BY ASC LIMIT 1",nativeQuery = true)
    Optional<iNOTIFICATION>FindEmailByAsc(@Param("Emails") String EmailS);
}
