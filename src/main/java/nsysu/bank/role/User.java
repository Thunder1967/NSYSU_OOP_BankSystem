package nsysu.bank.role;

public class User extends Person{
    protected User(String accountId, String name, String password, String role, String status) {
        super(accountId, name, password, role, status);
    }
}
