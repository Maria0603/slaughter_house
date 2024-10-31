package org.example.persistence;

import jakarta.persistence.*;

@Entity
public class Animal {
  @Id
  @GeneratedValue
  private Long animalId;
  private String animalType;
  private Double weightKilos;
}
