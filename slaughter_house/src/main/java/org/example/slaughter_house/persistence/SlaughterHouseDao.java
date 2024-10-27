package org.example.slaughter_house.persistence;

import org.example.slaughter_house.grpc.Animal;
import org.example.slaughter_house.grpc.Part;
import org.example.slaughter_house.grpc.Product;
import org.example.slaughter_house.grpc.Tray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class SlaughterHouseDao implements IPersistence {
  private final DatabaseHelper helper;


  @Autowired
  public SlaughterHouseDao(DatabaseHelper helper) {
    this.helper = helper;
  }

  @Override public ArrayList<Animal> getAnimalsFromProduct(long productId)
      throws IllegalArgumentException
  {
    String sql =
        "SELECT animalRegNo, animalType, animalWeight\n" + "FROM animal\n"
            + "         JOIN animal_part on animal.animalRegNo = animal_part.originAnimalRegNo\n"
            + "         JOIN tray on animal_part.trayId = tray.trayId\n"
            + "         JOIN mixed_product_tray on tray.trayId = mixed_product_tray.trayId\n"
            + "         JOIN mixed_product on mixed_product_tray.productId = mixed_product.productId\n"
            + "WHERE mixed_product.productId = ?\n" + "UNION\n"
            + "SELECT animalRegNo, animalType, animalWeight\n" + "FROM animal\n"
            + "         JOIN animal_part on animal.animalRegNo = animal_part.originAnimalRegNo\n"
            + "         JOIN tray on animal_part.trayId = tray.trayId\n"
            + "         JOIN same_part_product_tray on tray.trayId = same_part_product_tray.trayId\n"
            + "         JOIN same_part_product on same_part_product_tray.productId = same_part_product.productId\n"
            + "WHERE same_part_product.productId = ?;";

    try (Connection connection = helper.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
    {
      ps.setLong(1, productId);
      ps.setLong(2, productId);

      try (ResultSet rs = ps.executeQuery())
      {
        ArrayList<Animal> animals = new ArrayList<>();

        while (rs.next())
        {
          long regNo = rs.getLong("animalRegNo");
          String type = rs.getString("animalType");
          float weight = rs.getFloat("animalWeight");
          int weightKilos = (int) weight;

          Animal animal = Animal.newBuilder().setAnimalID(regNo).setAnimalType(type).setWeightKilos(weightKilos).build();
          animals.add(animal);
        }
        return animals;
      }

    }
    catch (SQLException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  @Override public ArrayList<Product> getProductsFromAnimal(long animalRegNo)
      throws IllegalArgumentException
  {
    String sql =
        "SELECT mixed_product.productId, mixed_product.productName, mixed_product.weight, mixed_product.packageDate, mixed_product.expirationDate\n"
            + "FROM mixed_product\n"
            + "         JOIN mixed_product_tray on mixed_product.productId = mixed_product_tray.productId\n"
            + "         JOIN tray on mixed_product_tray.trayId = tray.trayId\n"
            + "         JOIN animal_part on tray.trayId = animal_part.trayId\n"
            + "         JOIN animal on animal_part.originAnimalRegNo = animal.animalRegNo\n"
            + "WHERE animalRegNo = ?\n" + "UNION\n"
            + "SELECT same_part_product.productId, same_part_product.partType, same_part_product.weight, same_part_product.packageDate, same_part_product.expirationDate\n"
            + "FROM same_part_product\n"
            + "         JOIN same_part_product_tray on same_part_product.productId = same_part_product_tray.productId\n"
            + "         JOIN tray on same_part_product_tray.trayId = tray.trayId\n"
            + "         JOIN animal_part on tray.trayId = animal_part.trayId\n"
            + "         JOIN animal on animal_part.originAnimalRegNo = animal.animalRegNo\n"
            + "WHERE animalRegNo = ?;";
    try (Connection connection = helper.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
    {
      ps.setLong(1, animalRegNo);
      ps.setLong(2, animalRegNo);

      try (ResultSet rs = ps.executeQuery())
      {
        ArrayList<Product> products = new ArrayList<>();

        while (rs.next())
        {

          long id = rs.getLong("productId");
          String name = rs.getString("productName");
          float weight = rs.getFloat("weightKilos");
          int weightKilos = (int) weight;


          Product product = Product.newBuilder().setProductId(id).setProductType(name).setProductWeight(weightKilos).build();
          products.add(product);
        }
        return products;
      }
      catch (SQLException e)
      {
        throw new IllegalArgumentException(e);
      }
    }
    catch (SQLException e)
    {
      throw new IllegalArgumentException(e);
    }

  }

  @Override public Animal addAnimal(Animal animal) throws IllegalArgumentException {
    String sql = "INSERT INTO animal (animalType, animalWeight) VALUES (?, ?) RETURNING animalRegNo, animalType, animalWeight";

    try (Connection connection = helper.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, animal.getAnimalType());
      ps.setFloat(2, animal.getWeightKilos());

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          long animalID = rs.getLong("animalID");
          String animalType = rs.getString("animalType");
          float weightKilos = rs.getFloat("animalWeight");

          return Animal.newBuilder()
              .setAnimalID(animalID)
              .setAnimalType(animalType)
              .setWeightKilos(weightKilos)
              .build();
        } else {
          throw new IllegalArgumentException("Failed to retrieve generated ID");
        }
      }
    } catch (SQLException e) {
      throw new IllegalArgumentException("Error adding animal to the database", e);
    }
  }

  @Override public Part addPart(Part part) throws IllegalArgumentException {

      String sql = "INSERT INTO part (partType, partWeight) VALUES (?, ?) RETURNING partID, partType, partWeight";

      try (Connection connection = helper.getConnection();
          PreparedStatement ps = connection.prepareStatement(sql)) {

        // Set the parameters for partType and partWeight
        ps.setString(1, part.getPartType());
        ps.setFloat(2, part.getPartWeight());

        // Execute the query and process the result
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            long partID = rs.getLong("partID");
            String partType = rs.getString("partType");
            float weightKilos = rs.getFloat("partWeight");

            return Part.newBuilder()
                .setPartId(partID)
                .setPartType(partType)
                .setPartWeight(weightKilos)
                .build();
          } else {
            throw new IllegalArgumentException("Failed to retrieve generated ID");
          }
        }
      } catch (SQLException e) {
        throw new IllegalArgumentException("Error adding part to the database", e);
      }
    }


  @Override public boolean setTrayToPart(long partId, long trayId)
      throws IllegalArgumentException {

      // SQL to get part type and tray type based on IDs
      String partSql = "SELECT partType FROM part WHERE partID = ?";
      String traySql = "SELECT trayType FROM tray WHERE trayID = ?";
      String updatePartSql = "UPDATE part SET trayID = ? WHERE partID = ?";

      try (Connection connection = helper.getConnection();
          PreparedStatement partPs = connection.prepareStatement(partSql);
          PreparedStatement trayPs = connection.prepareStatement(traySql)) {

        // Retrieve the partType for the given partID
        partPs.setLong(1, partId);
        ResultSet partRs = partPs.executeQuery();
        if (!partRs.next()) {
          throw new IllegalArgumentException("Part not found with ID: " + partId);
        }
        String partType = partRs.getString("partType");

        // Retrieve the trayType for the given trayID
        trayPs.setLong(1, trayId);
        ResultSet trayRs = trayPs.executeQuery();

        String trayType;
        if (!trayRs.next()) {
          // If no tray exists, create a new one using addTray
          Tray newTray = createTray("defaultAnimalType", partType); // Replace "defaultAnimalType" as needed
          trayId = newTray.getTrayId();
          trayType = newTray.getTrayType();
        } else {
          trayType = trayRs.getString("trayType");
        }

        // Check if the types match
        if (!partType.equals(trayType)) {
          return false; // Types don't match, so return false
        }

        // If types match, update the part with the trayID
        try (PreparedStatement updatePs = connection.prepareStatement(updatePartSql)) {
          updatePs.setLong(1, trayId);
          updatePs.setLong(2, partId);
          int rowsUpdated = updatePs.executeUpdate();

          return rowsUpdated > 0; // Return true if the update was successful
        }

      } catch (SQLException e) {
        throw new IllegalArgumentException("Error setting tray to part in the database", e);
      }
    }


  @Override public Tray createTray(String animalType, String partType)
      throws IllegalArgumentException {

    // SQL to check for existing tray and insert new tray
    String checkTraySql = "SELECT trayID FROM tray WHERE partType = ?";
    String insertTraySql = "INSERT INTO tray (animalType, partType) VALUES (?, ?) RETURNING trayID, animalType, partType";

    try (Connection connection = helper.getConnection();
        PreparedStatement checkPs = connection.prepareStatement(checkTraySql)) {

      // Check if a tray with the given partType already exists
      checkPs.setString(1, partType);
      ResultSet checkRs = checkPs.executeQuery();
      if (checkRs.next()) {
        long existingTrayID = checkRs.getLong("trayID");
        throw new IllegalArgumentException("Tray already exists for partType: " + partType + " with trayID: " + existingTrayID);
      }

      // If no tray exists, insert a new tray
      try (PreparedStatement insertPs = connection.prepareStatement(insertTraySql)) {
        insertPs.setString(1, animalType);
        insertPs.setString(2, partType);

        ResultSet insertRs = insertPs.executeQuery();
        if (insertRs.next()) {
          long trayID = insertRs.getLong("trayID");
          String insertedAnimalType = insertRs.getString("animalType");
          String insertedPartType = insertRs.getString("partType");

          // Build and return the new Tray instance
          return Tray.newBuilder()
              .setTrayId(trayID)
              .setAnimalType(insertedAnimalType)
              .setTrayType(partType)
              .build();
        } else {
          throw new IllegalArgumentException("Failed to create new tray for partType: " + partType);
        }
      }

    } catch (SQLException e) {
      throw new IllegalArgumentException("Error adding tray to the database", e);
    }
  }

  @Override public Animal getAnimal(long animalId)
      throws IllegalArgumentException {
    return Animal.newBuilder().setAnimalID(11111).setAnimalType("chicken").setWeightKilos(2).build();
  }
}
