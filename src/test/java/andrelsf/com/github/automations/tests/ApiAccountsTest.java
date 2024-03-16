package andrelsf.com.github.automations.tests;

import static andrelsf.com.github.automations.core.TestUtil.accountIdInvalid;
import static andrelsf.com.github.automations.core.TestUtil.aliceAccountId;
import static andrelsf.com.github.automations.core.TestUtil.bobAccountId;
import static andrelsf.com.github.automations.core.TestUtil.joseAccountId;
import static andrelsf.com.github.automations.core.TestUtil.transfer;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import andrelsf.com.github.automations.core.BaseTest;
import andrelsf.com.github.automations.http.requests.PostTransferRequest;
import andrelsf.com.github.automations.http.responses.BalanceResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiAccountsTest extends BaseTest {

  private static BalanceResponse bobAccountResponse;
  private static BalanceResponse aliceAccountResponse;
  private static final BigDecimal amountToTransfer = BigDecimal.valueOf(500.0F);

  @Test
  public void t01_shouldReturn_404_accountNotFoundByAccountId() {
    given()
        .pathParam("accountId", accountIdInvalid)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(404)
        .assertThat().body("code", is(404))
        .assertThat().body("message", is("Account not found by customerId=".concat(accountIdInvalid)));
  }

  @Test
  public void t02_shouldReturn_200_JoseNomeFacilAccountIdtValid() {
    given()
        .pathParam("accountId", joseAccountId)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("fullName", is("Jose Nome Facil"))
        .assertThat().body("accountNumber", is(5990027))
        .assertThat().body("agency", is(4321))
        .assertThat().body("balance", is(5000.0F))
        .assertThat().body("dailyTransferLimit", is(1000.0F));
  }

  @Test
  public void t03_shouldReturn_200_BobAccountIdtValid() {
    bobAccountResponse = given()
        .pathParam("accountId", bobAccountId)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("fullName", is("Bob"))
        .assertThat().body("accountNumber", is(1230019))
        .assertThat().body("agency", is(1234))
        .assertThat().body("balance", is(2000.0F))
        .assertThat().body("dailyTransferLimit", is(1000.0F))
      .extract()
        .response()
        .getBody()
        .as(BalanceResponse.class);
  }

  @Test
  public void t04_shouldReturn_200_AliceAccountIdtValid() {
    aliceAccountResponse = given()
        .pathParam("accountId", aliceAccountId)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("fullName", is("Alice"))
        .assertThat().body("accountNumber", is(856087))
        .assertThat().body("agency", is(4321))
        .assertThat().body("balance", is(3000.0F))
        .assertThat().body("dailyTransferLimit", is(1000.0F))
      .extract()
        .response()
        .getBody()
        .as(BalanceResponse.class);
  }

  @Test
  public void t05_shouldPerformATransfer_FromBobToAlice_and_returnProofOfTheTransaction() {
    final PostTransferRequest transferRequest = transfer(aliceAccountResponse, amountToTransfer);
    given()
        .pathParam("accountId", bobAccountId)
        .body(transferRequest)
      .when()
        .post("/{accountId}/transfers")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("amount", is(amountToTransfer.floatValue()))
        .assertThat().body("toAccount", notNullValue())
        .assertThat().body("toAccount.accountNumber", is(aliceAccountResponse.accountNumber()))
        .assertThat().body("toAccount.agency", is(aliceAccountResponse.agency()))
        .assertThat().body("fromAccount", notNullValue())
        .assertThat().body("fromAccount.agency", is(bobAccountResponse.agency()))
        .assertThat().body("fromAccount.accountNumber", is(bobAccountResponse.accountNumber()))
        .assertThat().body("transferDate", notNullValue());
  }

  @Test
  public void t06_shouldReturn_200_AliceAccountIdtValid_withUpdatedAccountBalance() {
    final BigDecimal initialAccountBalance = aliceAccountResponse.balance();
    final BigDecimal currentAccountBalance = initialAccountBalance.add(amountToTransfer);
    given()
        .pathParam("accountId", aliceAccountId)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("balance", is(currentAccountBalance.floatValue()));
  }

  @Test
  public void t07_shouldReturn_200_BobAccountIdtValid_withUpdatedAccountBalance() {
    final BigDecimal initialAccountBalance = bobAccountResponse.balance();
    final BigDecimal currentAccountBalance = initialAccountBalance.subtract(amountToTransfer);
    given()
        .pathParam("accountId", bobAccountId)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("balance", is(currentAccountBalance.floatValue()));
  }

  @Test
  public void t08_shouldPerformATransfer_FromAliceToBob_and_returnProofOfTheTransaction() {
    final PostTransferRequest transferRequest = transfer(bobAccountResponse, amountToTransfer);
    given()
        .pathParam("accountId", aliceAccountId)
        .body(transferRequest)
      .when()
        .post("/{accountId}/transfers")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("amount", is(amountToTransfer.floatValue()))
        .assertThat().body("toAccount", notNullValue())
        .assertThat().body("toAccount.accountNumber", is(bobAccountResponse.accountNumber()))
        .assertThat().body("toAccount.agency", is(bobAccountResponse.agency()))
        .assertThat().body("fromAccount", notNullValue())
        .assertThat().body("fromAccount.agency", is(aliceAccountResponse.agency()))
        .assertThat().body("fromAccount.accountNumber", is(aliceAccountResponse.accountNumber()))
        .assertThat().body("transferDate", notNullValue());
  }

  @Test
  public void t09_shouldPerformATransfer_FromBobToAlice_and_returnProofOfTheTransaction_part2() {
    final PostTransferRequest transferRequest = transfer(aliceAccountResponse, amountToTransfer);
    given()
        .pathParam("accountId", bobAccountId)
        .body(transferRequest)
      .when()
        .post("/{accountId}/transfers")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("amount", is(amountToTransfer.floatValue()));
  }

  @Test
  public void t10_shouldReturn_200_BobAccountIdtValid_withDailyTransferLimit_isZero() {
    given()
        .pathParam("accountId", bobAccountId)
      .when()
        .get("/{accountId}/balance")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("dailyTransferLimit", is(0.0F));
  }

  @Test
  public void t11_shouldPerformATransfer_FromBobToAlice_and_returnDailyTransferLimitReached_part3() {
    final PostTransferRequest transferRequest = transfer(aliceAccountResponse, amountToTransfer);
    given()
        .pathParam("accountId", bobAccountId)
        .body(transferRequest)
      .when()
        .post("/{accountId}/transfers")
      .then()
        .statusCode(422)
        .assertThat().body("code", is(422))
        .assertThat().body("message", is(
            "Customer unable to make transfer.\nCheck account balance and daily transfer limit."));
  }

  @Test
  public void t12_shouldPerformATransfer_FromAliceToBob_and_returnProofOfTheTransaction() {
    final PostTransferRequest transferRequest = transfer(bobAccountResponse, amountToTransfer);
    given()
        .pathParam("accountId", aliceAccountId)
        .body(transferRequest)
      .when()
        .post("/{accountId}/transfers")
      .then()
        .assertThat().statusCode(200)
        .assertThat().body("amount", is(amountToTransfer.floatValue()));
  }

  @Test
  public void t13_shouldPerformUpdateTransferLimit_toBobAccount_and_returnSuccess_204_noContent() {
    Map<String, Object> patchTransferLimitRequest = new HashMap<>();
    patchTransferLimitRequest.put("amount", BigDecimal.valueOf(1000.0F));
    given()
        .pathParam("accountId", bobAccountId)
        .body(patchTransferLimitRequest)
      .when()
        .patch("/{accountId}/transfer-limits")
      .then()
        .assertThat().statusCode(204);
  }

  @Test
  public void t13_shouldPerformUpdateTransferLimit_toAliceAccount_and_returnSuccess_204_noContent() {
    Map<String, Object> patchTransferLimitRequest = new HashMap<>();
    patchTransferLimitRequest.put("amount",  BigDecimal.valueOf(1000.0F));
    given()
        .pathParam("accountId", aliceAccountId)
        .body(patchTransferLimitRequest)
      .when()
        .patch("/{accountId}/transfer-limits")
      .then()
        .assertThat().statusCode(204);
  }
}
