package org.example.slaughter_house.first_station.service;

import net.devh.boot.grpc.server.service.GrpcService;
import org.example.slaughter_house.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@GrpcService
public class FirstStationImpl extends FirstStationServiceGrpc.FirstStationServiceImplBase{
  private ArrayList<Animal> animals;
  private ArrayList<Part> parts;

  public FirstStationImpl() {
    this.animals = new ArrayList<>();
    System.out.println("FirstStationImpl created");

    
  }



  @Override public void registerAnimal(RegisterAnimalRequest request,
      StreamObserver<RegisterAnimalResponse> responseObserver) {

    Animal newAnimal = request.getAnimal();
    if (newAnimal.isInitialized()){
      animals.add(newAnimal);
      RegisterAnimalResponse response = RegisterAnimalResponse.newBuilder()
          .setSuccess(true)
          .setMessage("Animal was successfully registered")
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }else {
      RegisterAnimalResponse response = RegisterAnimalResponse.newBuilder()
          .setSuccess(false)
          .setMessage("Animal was not registered")
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

  @Override public void weightAnimal(WeightAnimalRequest request,
      StreamObserver<WeightAnimalResponse> responseObserver) {

   Animal animal = request.getAnimal();
   float weight = animal.getWeightKilos();

   WeightAnimalResponse response;

   if(animal.isInitialized()){
     response = WeightAnimalResponse.newBuilder()
         .setSuccess(true)
         .setMessage("Animal was successfully weighted")
         .setWeight(weight)
         .build();
   }else {
      response = WeightAnimalResponse.newBuilder()
          .setSuccess(false)
          .setMessage("Animal was not weighted")
          .build();
   }

   responseObserver.onNext(response);
   responseObserver.onCompleted();

  }
}





