package org.example.third;

import net.devh.boot.grpc.server.service.GrpcService;
import org.example.persistence.AnimalRepository;
import org.example.slaughter_house.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
public class ThirdStationImpl extends ThirdStationServiceGrpc.ThirdStationServiceImplBase {

  private final ArrayList<Product> products;
  private final ArrayList<Tray> trays;

  @Autowired AnimalRepository animalRepository;

  // Define required parts for each animal type
  private static final Set<String> CHICKEN_REQUIRED_PARTS = Set.of("wing", "leg", "breast");
  private static final Set<String> COW_REQUIRED_PARTS = Set.of("rib", "chuck", "loin", "brisket");
  private static final Set<String> PIG_REQUIRED_PARTS = Set.of("shoulder", "loin", "belly", "ham");

  public ThirdStationImpl() {
    this.products = new ArrayList<>();
    this.trays = new ArrayList<>();
  }

  @Override
  public void packProduct(PackProductRequest request, StreamObserver<PackProductResponse> responseObserver) {
    List<Part> parts = request.getPartsList();
    String productType = request.();
    PackProductResponse response;

    if (!parts.isEmpty() && productType != null && !productType.isEmpty()) {
      // Divide parts into bulk for packProduct and leftovers for packProductMix
      List<Part> bulkParts = new ArrayList<>();
      List<Part> leftoverParts = new ArrayList<>();

      for (Tray tray : trays) {
        List<Part> trayParts = tray.getPartsList();
        int trayPartCount = trayParts.size();
        int bulkCount = (trayPartCount / 10) * 10;  // Calculate the multiple of 10 for bulk
        int leftoverCount = trayPartCount % 10;      // Remainder goes to leftover

        // Add bulk parts to packProduct and leftovers to packProductMix
        bulkParts.addAll(trayParts.subList(0, bulkCount));
        leftoverParts.addAll(trayParts.subList(bulkCount, bulkCount + leftoverCount));
      }

      // Validate and pack bulk parts
      if (isValidProduct(bulkParts, productType)) {
        packBulkProduct(bulkParts, productType, responseObserver);
      } else {
        response = PackProductResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Product could not be packed. Parts must be from the same animal type and part type.")
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        return;
      }

      // Call packProductMix to handle leftovers
      packProductMix(leftoverParts, productType, responseObserver);
    } else {
      response = PackProductResponse.newBuilder()
          .setSuccess(false)
          .setMessage("Product could not be packed. Missing parts or product type.")
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

  private void packBulkProduct(List<Part> parts, String productType, StreamObserver<PackProductResponse> responseObserver) {
    float totalWeight = (float) parts.stream().mapToDouble(Part::getPartWeight).sum();

    // Create the new bulk Product
    Product newProduct = Product.newBuilder()
        .setProductId("prod-" + (products.size() + 1))
        .setProductType(productType)
        .addAllParts(parts)
        .setProductWeight(totalWeight)
        .build();

    products.add(newProduct);

    PackProductResponse response = PackProductResponse.newBuilder()
        .setSuccess(true)
        .setMessage("Product was successfully packed as a regular product")
        .setProduct(newProduct)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void packProductMix(List<Part> leftoverParts, String productType, StreamObserver<PackProductResponse> responseObserver) {
    PackProductResponse response;

    if (!leftoverParts.isEmpty() && productType != null && !productType.isEmpty()) {
      // Determine the required parts based on the product type
      Set<String> requiredParts;
      switch (productType.toLowerCase()) {
        case "chicken":
          requiredParts = CHICKEN_REQUIRED_PARTS;
          break;
        case "cow":
          requiredParts = COW_REQUIRED_PARTS;
          break;
        case "pig":
          requiredParts = PIG_REQUIRED_PARTS;
          break;
        default:
          response = PackProductResponse.newBuilder()
              .setSuccess(false)
              .setMessage("Unknown product type for half-animal mix.")
              .build();
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          return;
      }

      // Ensure leftover parts cover all required parts
      Set<String> partTypesInRequest = leftoverParts.stream()
          .map(Part::getPartType)
          .collect(Collectors.toSet());
      boolean validMix = partTypesInRequest.containsAll(requiredParts);

      if (validMix) {
        float totalWeight = (float) leftoverParts.stream().mapToDouble(Part::getPartWeight).sum();

        // Create the new mixed Product
        Product newProduct = Product.newBuilder()
            .setProductId("mix-prod-" + (products.size() + 1))
            .setProductType(productType)
            .addAllParts(leftoverParts)
            .setProductWeight(totalWeight)
            .build();

        products.add(newProduct);

        response = PackProductResponse.newBuilder()
            .setSuccess(true)
            .setMessage("Product was successfully packed as a half-animal mix")
            .setProduct(newProduct)
            .build();
      } else {
        response = PackProductResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Product could not be packed. Missing required parts for a half-animal mix.")
            .build();
      }
    } else {
      response = PackProductResponse.newBuilder()
          .setSuccess(false)
          .setMessage("Product could not be packed. Missing parts or product type.")
          .build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private boolean isValidProduct(List<Part> parts, String productType) {
    String commonAnimalType = parts.get(0).getAnimalId();
    String commonPartType = parts.get(0).getPartType();
    return parts.stream().allMatch(part ->
        part.getAnimalId() == (commonAnimalType) && part.getPartType().equals(commonPartType)); }
  }
