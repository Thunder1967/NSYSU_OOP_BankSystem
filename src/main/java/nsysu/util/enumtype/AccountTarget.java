package nsysu.util.enumtype;

public enum AccountTarget implements asTarget {
    Balance("balance"),
    TimeOfLastView("lastview"),
    Type("type"),
    Status("status"),
    History("history");

    private final String str;

    AccountTarget(String str) {
        this.str = str;
    }
    public String getStr() {
        return str;
    }
}
