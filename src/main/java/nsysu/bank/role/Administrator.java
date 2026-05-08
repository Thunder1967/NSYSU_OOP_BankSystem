package nsysu.bank.role;

public class Administrator extends Person{
    protected Administrator(String accountId, String name, String password, String role, String status) {
        super(accountId, name, password, role, status);
    }
}
