package nsysu.bank.account;

public interface CanTransferOut {
    public boolean transferOut(String toId,double amount,String description);
}
