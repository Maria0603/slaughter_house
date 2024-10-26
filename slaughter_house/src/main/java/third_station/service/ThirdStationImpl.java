package third_station.service;

import net.devh.boot.grpc.server.service.GrpcService;
import org.example.slaughter_house.grpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class ThirdStationImpl extends ThirdStationServiceGrpc.ThirdStationServiceImplBase {

  private final ArrayList<Product> products;

  @Autowired
  public ThirdStationImpl() {
    this.products = new ArrayList<>();
  }

  @Override
  public void packProduct(PackProductRequest request, StreamObserver<PackProductResponse> responseObserver) {


    List<Part> parts = request.getPartsList();
    String productType = request.getProductType();

    PackProductResponse response;


    if (!parts.isEmpty() && productType != null && !productType.isEmpty()) {

      float totalWeight = (float) parts.stream().mapToDouble(Part::getPartWeight).sum();


      Product newProduct = Product.newBuilder()
          .setProductId("prod-" + (products.size() + 1))
          .setProductType(productType)
          .addAllParts(parts)
          .setProductWeight(totalWeight)
          .build();


      products.add(newProduct);


      response = PackProductResponse.newBuilder()
          .setSuccess(true)
          .setMessage("Product was successfully packed")
          .setProduct(newProduct)
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
}
