package nsysu.bank;

import org.bson.Document;

import java.util.Date;

public class HistoryRecord implements Comparable<HistoryRecord>{
    private final Date date;
    private final int amount;
    private final String fromId;
    private final String description;

    public HistoryRecord(Date date, int amount, String fromId, String description) {
        this.date = date;
        this.amount = amount;
        this.fromId = fromId;
        this.description = description;
    }
    public HistoryRecord(Document doc) {
        this.date = doc.getDate("date");
        this.amount = doc.getInteger("amount");
        this.fromId = doc.getString("fromId");
        this.description = doc.getString("description");
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public String getFromId() {
        return fromId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(HistoryRecord other) {
        return other.date.compareTo(this.date);
    }

    @Override
    public String toString() {
        return String.format("[%s] %d NTD from (%s) - %s", date, amount, fromId, description);
    }
}
