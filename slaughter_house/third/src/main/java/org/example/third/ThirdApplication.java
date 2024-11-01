package org.example.third;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.persistence")
public class ThirdApplication {

  public static void main(String[] args) {
    SpringApplication.run(ThirdApplication.class, args);
  }

}
