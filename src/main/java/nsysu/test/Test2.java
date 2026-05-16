package nsysu.test;

import nsysu.util.enumtype.RoleType;
import nsysu.util.sqlaccess.UserData;

public class Test2 {
    public static void main(String[] args) {
        System.out.println(UserData.addNewUser("Peter","1234", RoleType.User));
    }
}
