package nsysu.Util.enumtype;

public enum RoleType{
    User("user"),
    Administrator("administrator");

    private final String str;

    RoleType(String str) {
        this.str = str;
    }
    public String getStr() {
        return str;
    }
}
