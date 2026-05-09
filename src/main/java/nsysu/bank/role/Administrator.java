package nsysu.bank.role;

import nsysu.util.enumtype.RoleType;
import nsysu.util.exception.IdNotFindException;

public class Administrator extends Person{
    public Administrator(String userId) throws IdNotFindException {
        super(userId, RoleType.Administrator.getStr());
    }
}
