package nsysu.bank.role;

public abstract class Person {
    private String accountId;
    protected String name;
    protected String password;
    protected String role;
    protected String status;

    protected Person(String accountId, String name, String password, String role, String status) {
        this.accountId = accountId;
        this.name = name;
        this.password = password;
        this.role = role;
        this.status = status;
    }
}
