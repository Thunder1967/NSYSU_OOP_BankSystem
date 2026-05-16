package nsysu.util.enumtype;

import java.util.Arrays;

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
    public static boolean checkMatch(String current, StatusType... targets) {
        if (current == null || targets == null) return false;

        return Arrays.stream(targets)
                .anyMatch(target -> current.equals(target.getStr()));
    }
}
