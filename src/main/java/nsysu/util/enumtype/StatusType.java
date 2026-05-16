package nsysu.util.enumtype;

public enum StatusType implements asTarget {
    Active("active"),
    Frozen("frozen"),
    Closed("closed");

    private final String str;

    StatusType(String str) {
        this.str = str;
    }
    public String getStr() {
        return str;
    }
}
