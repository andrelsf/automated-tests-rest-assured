package andrelsf.com.github.automations.http.requests;

import java.math.BigDecimal;

public record PostTransferRequest(
    ToAccountRequest toAccount,
    BigDecimal amount
) {

}
