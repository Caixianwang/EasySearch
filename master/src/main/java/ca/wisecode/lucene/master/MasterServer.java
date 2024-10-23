package ca.wisecode.lucene.master;

import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MasterServer {
    public static void main(final String[] args) {
        SpringApplication.run(MasterServer.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final Server server) {
        log.info(" ****** GRPC master started ****** ");
        return args -> {
//            server.awaitTermination();
        };
    }
}