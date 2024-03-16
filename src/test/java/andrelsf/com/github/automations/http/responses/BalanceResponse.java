package andrelsf.com.github.automations.http.responses;

import java.math.BigDecimal;

public record BalanceResponse(
    String fullName,
    Integer agency,
    Integer accountNumber,
    BigDecimal dailyTransferLimit,
    BigDecimal balance
) {}
