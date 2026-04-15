package THREADCONFIG;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ThreadConfig {

    @Bean("HighThread")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(11);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("high-thread--");
        executor.initialize();
   return executor;

    }
    @Bean("LowThread")
    public Executor LowExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(3);
        executor.setThreadNamePrefix("low-thread--");
        executor.initialize();
        return executor;
    }


}
