// SecondStationApplication.java
package org.example.slaughter_house.second_station;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecondStationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondStationApplication.class, "--spring.config.name=application-secondstation");
    }
}