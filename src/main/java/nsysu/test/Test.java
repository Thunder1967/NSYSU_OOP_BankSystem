package nsysu.test;

import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.RoleType;
import nsysu.util.sqlaccess.AccountData;
import nsysu.util.sqlaccess.UserData;

import java.util.Date;

public class Test {
    public static void main(String[] args) {
//        System.out.println(UserData.addNewUser("Peter","1234", RoleType.User));
//        System.out.println(UserData.addAccount("U18473", AccountType.SavingsAccount));
        AccountData.incBalance("59049",1000);
    }
}
