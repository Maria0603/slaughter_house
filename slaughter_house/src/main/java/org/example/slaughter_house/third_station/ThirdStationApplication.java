// ThirdStationApplication.java
package org.example.slaughter_house.third_station;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThirdStationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThirdStationApplication.class, "--spring.config.name=application-thirdstation");
    }
}