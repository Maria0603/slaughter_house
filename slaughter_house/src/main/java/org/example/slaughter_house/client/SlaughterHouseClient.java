package org.example.slaughter_house.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.slaughter_house.grpc.*;

import java.util.Arrays;
import java.util.List;

public class SlaughterHouseClient {
    public static void main(String[] args) {
        ManagedChannel firstStationChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext()
            .build();

        FirstStationServiceGrpc.FirstStationServiceBlockingStub firstStationStub =
            FirstStationServiceGrpc.newBlockingStub(firstStationChannel);

        List<Animal> animals = Arrays.asList(
            Animal.newBuilder().setAnimalID(11111).setAnimalType("Cow").setWeightKilos(550.5f).build(),
            Animal.newBuilder().setAnimalID(2222).setAnimalType("Pig").setWeightKilos(350.5f).build()
        );

        for (Animal animal : animals) {
            // Register animal at the first station
            RegisterAnimalRequest registerAnimalRequest = RegisterAnimalRequest.newBuilder()
                .setAnimal(animal)
                .build();
            RegisterAnimalResponse registerResponse = firstStationStub.registerAnimal(registerAnimalRequest);
            System.out.println("First Station Response: " + registerResponse.getMessage());
        }

        firstStationChannel.shutdown();
    }
}