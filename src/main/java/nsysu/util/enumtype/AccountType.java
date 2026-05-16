package nsysu.util.enumtype;

public enum AccountType{
    SavingsAccount("saving"),
    TimeDeposit("time"),
    CheckingAccount("checking"),
    USDAccount("usd");

    private final String str;
    AccountType(String str){
        this.str = str;
    }
    public String getStr() {
        return str;
    }
}
