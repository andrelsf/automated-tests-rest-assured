package andrelsf.com.github.automations.core;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;

public class BaseTest implements ApiUtil {

  public static RequestSpecification requestSpecification;
  public static ResponseSpecification responseSpecification;

  @BeforeClass
  public static void setup() {
    RestAssured.baseURI = BASE_URI;
    RestAssured.basePath = BASE_PATH;
    RestAssured.port = PORT;

    requestSpecification = new RequestSpecBuilder()
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .build();

    responseSpecification = new ResponseSpecBuilder()
        .expectResponseTime(Matchers.lessThan(MAX_TIMEOUT))
        .build();

    RestAssured.requestSpecification = requestSpecification;
    RestAssured.responseSpecification = responseSpecification;

    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }
}
