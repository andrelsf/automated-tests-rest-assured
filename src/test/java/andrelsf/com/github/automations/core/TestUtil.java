package andrelsf.com.github.automations.core;

import andrelsf.com.github.automations.http.requests.PostTransferRequest;
import andrelsf.com.github.automations.http.requests.ToAccountRequest;
import andrelsf.com.github.automations.http.responses.BalanceResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestUtil {

  public final static String bobAccountId;
  public final static String aliceAccountId;
  public final static String accountIdInvalid;

  public final static String joseAccountId;

  static {
    bobAccountId = "0ab4165471c445918697d3d620b0db2b";
    joseAccountId = "0ab4165471c445918697d3d620b0db2c";
    aliceAccountId = "0ab4165471c445918697d3d620b0db2a";
    accountIdInvalid = UUID.randomUUID().toString();
  }

  public static PostTransferRequest transfer(
      final BalanceResponse toAccount,
      final BigDecimal amountToTransfer) {
    return new PostTransferRequest(
        new ToAccountRequest(
            toAccount.fullName(),
            "22233344455",
            toAccount.agency(),
            toAccount.accountNumber()
        ),
        amountToTransfer
    );
  }
}
