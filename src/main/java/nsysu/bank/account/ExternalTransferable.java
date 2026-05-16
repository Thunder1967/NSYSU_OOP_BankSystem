package nsysu.bank.account;

public interface ExternalTransferable {
    public boolean externalTransfer(String toId, double amount, String description);
}
