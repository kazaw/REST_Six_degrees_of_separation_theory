package kacper.lab9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication
public class Lab9Application {


    public static void main(String[] args) {
        SpringApplication.run(Lab9Application.class, args);

        //SpringApplication.run(Lab9Application.class, args);

    }

    @Bean(name = "webExecutor")
    public Executor asyncWebExecutor() {
        ThreadPoolTaskExecutor webExecutor = new ThreadPoolTaskExecutor();
        webExecutor.setCorePoolSize(5);
        webExecutor.setMaxPoolSize(5);
        webExecutor.setQueueCapacity(600);
        webExecutor.setThreadNamePrefix("webService-");
        webExecutor.initialize();
        return webExecutor;
    }

    @Bean(name = "linkLookupExecutor")
    public Executor asyncLinkLookupExecutor() {
        ThreadPoolTaskExecutor linkLookupExecutor = new ThreadPoolTaskExecutor();
        linkLookupExecutor.setCorePoolSize(50);
        linkLookupExecutor.setMaxPoolSize(50);
        linkLookupExecutor.setQueueCapacity(60000); //TODO: zapytać o to
        linkLookupExecutor.setThreadNamePrefix("Lookup-");
        linkLookupExecutor.initialize();
        return linkLookupExecutor;
    }
}
