package JPA;

import Entity.Oauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthRepository extends JpaRepository<Oauth,Long> {

    @Query(value = "SELECT * from Auth AS O WHERE provider_id = :providerId AND provider_name = :provider",nativeQuery = true)
    Optional<Oauth>FindByProviderIdAndProviderName(@Param("providerId") String providerId, @Param("provider") String Provider);
}
