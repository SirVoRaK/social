package br.com.rodolfo.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocialApplication {

    public static void main(String[] args) {
//        Map<String, String> env = System.getenv();
//        System.out.println("ENVIRONMENT VARIABLES");
//        env.forEach((key, value) -> System.out.println(key + "=" + value));
        SpringApplication.run(SocialApplication.class, args);
    }

}
