package org.example.slaughter_house.persistence;

import org.example.slaughter_house.grpc.Animal;
import org.example.slaughter_house.grpc.Part;
import org.example.slaughter_house.grpc.Product;
import org.example.slaughter_house.grpc.Tray;

import java.util.ArrayList;

public interface IPersistence {
  ArrayList<Animal> getAnimalsFromProduct(long productId) throws IllegalArgumentException;
  ArrayList<Product> getProductsFromAnimal(long animalId) throws IllegalArgumentException;
  Animal addAnimal(Animal animal) throws IllegalArgumentException;
  Part addPart(Part part) throws IllegalArgumentException;
  boolean setTrayToPart(long partId, long trayId) throws IllegalArgumentException;
  Tray createTray(String animalType, String partType) throws IllegalArgumentException;
  Animal getAnimal(long animalId) throws IllegalArgumentException;
}
