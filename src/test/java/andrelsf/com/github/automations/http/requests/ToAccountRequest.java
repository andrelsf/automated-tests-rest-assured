package andrelsf.com.github.automations.http.requests;

public record ToAccountRequest(
    String fullName,
    String cpf,
    Integer agency,
    Integer accountNumber
) {

}
