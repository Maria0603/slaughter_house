// SecondStationApplication.java
package org.example.slaughter_house.second_station;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.example.slaughter_house")
public class SecondStationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondStationApplication.class, "--spring.config.name=application-secondstation");
    }
}