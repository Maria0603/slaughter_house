// FirstStationApplication.java
package org.example.slaughter_house.first_station;

import org.example.slaughter_house.persistence.DatabaseHelper;
import org.example.slaughter_house.persistence.IPersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FirstStationApplication {
    public static void main(String[] args) {

        SpringApplication.run(FirstStationApplication.class, "--spring.config.name=application-firststation");




    }
}