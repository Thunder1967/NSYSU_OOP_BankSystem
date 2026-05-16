package nsysu.bank.account;

public interface ExternalTransferable {
    public abstract boolean externalTransfer(String toId, double amount, String description);
}
