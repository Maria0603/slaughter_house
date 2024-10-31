package org.example.second;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.persistence.AnimalRepository;
import org.example.slaughter_house.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@Service
class SecondStationImpl
    extends SecondStationServiceGrpc.SecondStationServiceImplBase {

  @Autowired AnimalRepository animalRepository;

  //private final ThirdStationServiceGrpc.ThirdStationServiceBlockingStub thirdStationStub;


   public SecondStationImpl() {

  /*  ArrayList<Part> parts = new ArrayList<>();
    ArrayList<Product> products = new ArrayList<>();*/

    /*// Call SecondStationService
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8083)
        .usePlaintext().build();
    thirdStationStub = ThirdStationServiceGrpc.newBlockingStub(channel);*/
  }


  @Override public void cutAnimal(CutAnimalRequest request,
      StreamObserver<CutAnimalResponse> responseObserver) {
    Animal animal = request.getAnimal();
    CutAnimalResponse response;
    List<Animal> nimasl = stationOneStub.getAllAnimals()
    if (animal.isInitialized()) {
      List<Part> generatedParts;
      String animalType = animal.getAnimalType();

      // Cut based on animal type
      switch (animalType.toLowerCase()) {
        case "chicken" -> generatedParts = cutChicken(animal);
        case "cow" -> generatedParts = cutCow(animal);
        case "pig" -> generatedParts = cutPig(animal);
        default -> {
          response = CutAnimalResponse.newBuilder().setSuccess(false).build();
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          return;
        }
      }

      // Add parts to database (animal_parts table)
      for (Part part : generatedParts) {
        try {
         // persistence.addPart(part);
        }
        catch (Exception e) {
          response = CutAnimalResponse.newBuilder().setSuccess(false).build();
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          return;
        }
      }

      // Call putProductOnTray to assign tray IDs to parts
    /*  PackProductRequest packRequest = PackProductRequest.newBuilder()
          .addAllParts(generatedParts)
          .build();
*/
      /*putProductOnTray(packRequest, new StreamObserver<PackProductResponse>() {
        @Override
        public void onNext(PackProductResponse packProductResponse) {
          // Handle response if needed
        }

        @Override
        public void onError(Throwable t) {
          // Handle error if needed
        }

        @Override
        public void onCompleted() {
          // Handle completion if needed
        }
      });*/


      response = CutAnimalResponse.newBuilder().setSuccess(true)
          .addAllParts(generatedParts).build();
    }
    else {
      response = CutAnimalResponse.newBuilder().setSuccess(false).build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();

    /*// Call ThirdStationService
    PackProductRequest packRequest = PackProductRequest.newBuilder()
        .addAllParts(parts).build();
    thirdStationStub.packProduct(packRequest);*/
  }


  @Override public void putProductOnTray(PackProductRequest request, StreamObserver<PackProductResponse> responseObserver) {
    List<Tray> partsToPack = request.getTraysList();

    PackProductResponse response;

    if (!partsToPack.isEmpty()) {
        /*float totalWeight = (float) partsToPack.stream()
            .mapToDouble(Part::getPartWeight).sum();*/

        for (Tray tray : partsToPack) {
            try {
                // Assign tray to part
             /*   String partType = part.getPartType();
                String animalType = persistence.getAnimal(part.getAnimalId()).getAnimalType();

                long trayId = persistence.createTray(animalType, partType).getTrayId();
                boolean success = persistence.setTrayToPart(part.getPartId(), trayId);*/

                /*if (!success) {
                    response = PackProductResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Failed to assign tray to part: " + part.getPartId())
                        .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }*/
            } catch (Exception e) {
                response = PackProductResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error assigning tray to part: " + e.getMessage())
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
        }

        response = PackProductResponse.newBuilder()
            .setSuccess(true)
            .setMessage("Product was successfully packed")
            .build();
    } else {
        response = PackProductResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Product could not be packed. Missing parts or product type.")
            .build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
}


  private List<Part> cutChicken(Animal animal){
    List<Part> generatedParts = new ArrayList<>();

    generatedParts.add(Part.newBuilder().setPartType("wing")
        .setPartWeight(animal.getWeightKilos() * 0.1f)
        .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(Part.newBuilder().setPartType("wing")
        .setPartWeight(animal.getWeightKilos() * 0.1f)
        .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("leg")
            .setPartWeight(animal.getWeightKilos() * 0.15f)
            .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("leg")
            .setPartWeight(animal.getWeightKilos() * 0.15f)
            .setAnimalId((animal.getAnimalID())).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("breast")
            .setPartWeight(animal.getWeightKilos() * 0.25f)
            .setAnimalId((animal.getAnimalID())).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("breast")
            .setPartWeight(animal.getWeightKilos() * 0.25f)
            .setAnimalId((animal.getAnimalID())).build());

    return generatedParts;
  }

  private List<Part> cutCow(Animal animal){
    List<Part> generatedParts = new ArrayList<>();

    generatedParts.add(
        Part.newBuilder()
            .setPartType("rib")
            .setPartWeight(animal.getWeightKilos() * 0.3f)
            .setAnimalId((animal.getAnimalID())).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("chuck")
            .setPartWeight(animal.getWeightKilos() * 0.2f)
            .setAnimalId((animal.getAnimalID())).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("loin")
            .setPartWeight(animal.getWeightKilos() * 0.25f)
            .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("brisket")
            .setPartWeight(animal.getWeightKilos() * 0.25f)
            .setAnimalId(animal.getAnimalID()).build());

    return generatedParts;
  }

  private List<Part> cutPig(Animal animal){
    List<Part> generatedParts = new ArrayList<>();

    generatedParts.add(
        Part.newBuilder()
            .setPartType("boston butt")
            .setPartWeight(animal.getWeightKilos() * 0.15f)
            .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("shoulder")
            .setPartWeight(animal.getWeightKilos() * 0.15f)
            .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("loin")
            .setPartWeight(animal.getWeightKilos() * 0.3f)
            .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("belly")
            .setPartWeight(animal.getWeightKilos() * 0.2f)
            .setAnimalId(animal.getAnimalID()).build());
    generatedParts.add(
        Part.newBuilder()
            .setPartType("ham")
            .setPartWeight(animal.getWeightKilos() * 0.2f)
            .setAnimalId(animal.getAnimalID()).build());

    return generatedParts;
  }


}
