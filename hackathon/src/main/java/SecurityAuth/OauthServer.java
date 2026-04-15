package SecurityAuth;
import Entity.Oauth;
import JPA.OauthRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@NullMarked
public class OauthServer extends OidcUserService {
    private final OauthRepository oauthRepository;

    public OauthServer(OauthRepository oauthRepository) {
        this.oauthRepository = oauthRepository;
        System.out.println("=== OauthServer BEAN CREATED ===");
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        OidcUser oidcUser = super.loadUser(userRequest);
      String provider = userRequest.getClientRegistration().getRegistrationId();
      String providerId = oidcUser.getAttribute("sub");
      String Email = oidcUser.getAttribute("email");
      String name = oidcUser.getAttribute("name");
      String AcesToken =userRequest.getAccessToken().getTokenValue();
      String token = (String) userRequest.getAdditionalParameters().get("refresh_token");


      Oauth oauth1 = oauthRepository.FindByProviderIdAndProviderName(provider,providerId)
              .orElse(new Oauth());

      oauth1.setProviderId(providerId);
      oauth1.setProvider(provider);
      oauth1.setEmail(Email);
      oauth1.setName(name);
      oauth1.setAcessToken(AcesToken);
      oauth1.setRefreshToken(token);

       if (oauth1.getId()==null){
           oauth1.setCreatedAt(LocalDateTime.now()); //FOR NEW USER
       }
       oauthRepository.save(oauth1);
       return oidcUser;


    }

}
