package second_station.service;

import net.devh.boot.grpc.server.service.GrpcService;
import org.example.slaughter_house.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GrpcService class SecondStationImpl
    extends SecondStationServiceGrpc.SecondStationServiceImplBase {

  private final ArrayList<Part> parts;
  private final ArrayList<Product> products;

  @Autowired public SecondStationImpl(ArrayList<Part> parts,
      ArrayList<Product> products) {
    this.parts = parts;
    this.products = products;
  }

  @Override public void cutAnimal(CutAnimalRequest request,
      StreamObserver<CutAnimalResponse> responseObserver) {
    Animal animal = request.getAnimal();
    CutAnimalResponse response;

    if (animal.isInitialized()) {

      List<Part> generatedParts = new ArrayList<>();
      generatedParts.add(
          Part.newBuilder().setPartId(UUID.randomUUID().toString())
              .setPartType("leg").setPartWeight(animal.getWeightKilos() * 0.2f)
              .setAnimalId(String.valueOf(animal.getAnimalID())).build());
      generatedParts.add(
          Part.newBuilder().setPartId(UUID.randomUUID().toString())
              .setPartType("loin").setPartWeight(animal.getWeightKilos() * 0.3f)
              .setAnimalId(String.valueOf(animal.getAnimalID())).build());
      generatedParts.add(
          Part.newBuilder().setPartId(UUID.randomUUID().toString())
              .setPartType("rib").setPartWeight(animal.getWeightKilos() * 0.5f)
              .setAnimalId(String.valueOf(animal.getAnimalID())).build());

      parts.addAll(generatedParts);

      response = CutAnimalResponse.newBuilder().setSuccess(true)
          .addAllParts(generatedParts).build();
    }
    else {

      response = CutAnimalResponse.newBuilder().setSuccess(false).build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override public void packProduct(PackProductRequest request,
      StreamObserver<PackProductResponse> responseObserver) {
    List<Part> partsToPack = request.getPartsList();
    String productType = request.getProductType();

    PackProductResponse response;

    if (!partsToPack.isEmpty() && productType != null
        && !productType.isEmpty()) {

      float totalWeight = (float) partsToPack.stream()
          .mapToDouble(Part::getPartWeight).sum();

      Product newProduct = Product.newBuilder()
          .setProductId("prod-" + (products.size() + 1))
          .setProductType(productType).addAllParts(partsToPack)
          .setProductWeight(totalWeight).build();

      products.add(newProduct);

      response = PackProductResponse.newBuilder().setSuccess(true)
          .setMessage("Product was successfully packed").setProduct(newProduct)
          .build();
    }
    else {

      response = PackProductResponse.newBuilder().setSuccess(false).setMessage(
              "Product could not be packed. Missing parts or product type.")
          .build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
