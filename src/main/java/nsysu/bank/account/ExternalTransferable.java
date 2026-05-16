package nsysu.bank.account;

public interface ExternalTransferable {
    boolean externalTransfer(String toId, double amount, String description);
}
