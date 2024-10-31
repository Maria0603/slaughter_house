package org.example.first;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.persistence.AnimalRepository;
import org.example.slaughter_house.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@GrpcService
public class FirstStationImpl extends FirstStationServiceGrpc.FirstStationServiceImplBase{
  private ArrayList<Animal> animals;
  private ArrayList<Part> parts;

  @Autowired AnimalRepository animalRepository;


  private SecondStationServiceGrpc.SecondStationServiceBlockingStub secondStationStub;

  public FirstStationImpl() {
    //TODO: fetch animals from database
    this.animals = new ArrayList<>();
    System.out.println("FirstStationImpl created");

    /*ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8082)
        .usePlaintext()
        .build();
    secondStationStub = SecondStationServiceGrpc.newBlockingStub(channel);*/

    // Retry mechanism to ensure second station is available
    boolean connected = false;
    while (!connected) {
      try {
        /*secondStationStub.withDeadlineAfter(1, TimeUnit.SECONDS)
            .cutAnimal(CutAnimalRequest.newBuilder().build());*/
        connected = true;
      } catch (io.grpc.StatusRuntimeException e) {
        System.out.println("Waiting for Second Station to be available...");
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      }
    }

  }


  @Override
  public void registerAnimal(RegisterAnimalRequest request, StreamObserver<RegisterAnimalResponse> responseObserver) {
    final Animal[] newAnimal = {request.getAnimal()};

    // Create a request for weighing the animal
    WeightAnimalRequest weightRequest = WeightAnimalRequest.newBuilder().setAnimal(
        newAnimal[0]).build();

    weightAnimal();


    // Create a temporary StreamObserver to handle the weight response
    StreamObserver<WeightAnimalResponse> weightResponseObserver = new StreamObserver<WeightAnimalResponse>() {
      @Override
      public void onNext(WeightAnimalResponse weightResponse) {
        float newAnimalWeight = weightResponse.getWeight();

        // Update the animal's weight
        newAnimal[0] = newAnimal[0].toBuilder().setWeightKilos((int) newAnimalWeight).build();

        // Add new animal to the database
        try {
         // persistence.addAnimal(newAnimal[0]);
        } catch (Exception e) {
          RegisterAnimalResponse response = RegisterAnimalResponse.newBuilder()
              .setSuccess(false)
              .setMessage("Failed to register animal: " + e.getMessage())
              .build();
          responseObserver.onNext(response);
          responseObserver.onCompleted();
          return;
        }

        // Call SecondStationService
        CutAnimalRequest cutRequest = CutAnimalRequest.newBuilder().setAnimal(
            newAnimal[0]).build();
        //secondStationStub.cutAnimal(cutRequest);

        // Send success response
        RegisterAnimalResponse response = RegisterAnimalResponse.newBuilder()
            .setSuccess(true)
            .setMessage("AnimalEntity was successfully registered and weighted")
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
      }

      @Override
      public void onError(Throwable t) {
        RegisterAnimalResponse response = RegisterAnimalResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Weighting failed: " + t.getMessage())
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
      }

      @Override
      public void onCompleted() {
        // This can be left empty, but required by the interface
      }
    };

    // Use the stub to call the weightAnimal method
    weightAnimal(weightRequest, weightResponseObserver);
  }



  @Override public void weightAnimal(WeightAnimalRequest request,
      StreamObserver<WeightAnimalResponse> responseObserver) {

    Animal animal = request.getAnimal();
    float weight = switch (animal.getAnimalType()) {
      case "chicken" -> getRandomWeight(1, 5);
      case "pork" -> getRandomWeight(250, 500);
      case "cow" -> getRandomWeight(500, 900);
      case "duck" -> getRandomWeight(1, 5);
      default -> 0; // no match
    };
    

    WeightAnimalResponse response;

    if(animal.isInitialized() && weight > 0){
      response = WeightAnimalResponse.newBuilder()
          .setSuccess(true)
          .setMessage("AnimalEntity was successfully weighted")
          .setWeight(weight)
          .build();
    }else {
      response = WeightAnimalResponse.newBuilder()
          .setSuccess(false)
          .setMessage("AnimalEntity was not weighted")
          .build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  private float getRandomWeight(int min, int max) {
    return min + (float) (Math.random() * (max - min));
  }
}





