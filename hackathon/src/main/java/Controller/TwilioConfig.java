package Controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class TwilioConfig {

    @Bean(name = "fastSms")
   public WebClient FastSmsWebClient(){
      return WebClient.builder()
              .baseUrl("https://www.fast2sms.com/dev/bulkV2")
              .build() ;
    }
}
