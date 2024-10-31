package org.example.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AnimalRepository extends JpaRepository<Animal, Long> {

  @Query("SELECT a FROM Animal a WHERE a.animalId = :animalId")
  public Animal findByAnimalId(Long animalId);
}
