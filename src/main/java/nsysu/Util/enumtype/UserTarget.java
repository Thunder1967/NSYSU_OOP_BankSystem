package nsysu.Util.enumtype;

public enum UserTarget implements asTarget {
    UserName("username"),
    Password("password"),
    Role("role"),
    Status("status"),
    Accounts("accounts");

    private final String str;

    UserTarget(String str) {
        this.str = str;
    }
    public String getStr() {
        return str;
    }
}
