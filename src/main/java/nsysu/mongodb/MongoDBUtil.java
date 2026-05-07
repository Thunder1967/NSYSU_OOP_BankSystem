package nsysu.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import nsysu.resources.ApplicationProperties;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public final class MongoDBUtil {
    private static MongoCollection<Document> accountsCollection;
    private static MongoCollection<Document> usersCollection;
    private static final Random random = new Random();
    public enum CollectionType {
        USERS, ACCOUNTS
    }

    static {
        String uri = ApplicationProperties.mongodbURL;
        MongoClient mongoClient = MongoClients.create(uri);
        accountsCollection = mongoClient.getDatabase(ApplicationProperties.Database).getCollection(ApplicationProperties.Accounts);
        usersCollection = mongoClient.getDatabase(ApplicationProperties.Database).getCollection(ApplicationProperties.Users);
    }

    private MongoDBUtil() {}

    private static MongoCollection<Document> getCollection(CollectionType type){
        switch (type){
            case ACCOUNTS:
                return accountsCollection;
            case USERS:
                return usersCollection;
            default:
                return null;
        }
    }
    public static <T> T getData(CollectionType type,String id, String target, Class<T> CC) {
        Document doc = getCollection(type).find(Filters.eq("id", id)).first();
        if (doc == null) {
            throw new IdNotFindException();
        }
        else if(!doc.containsKey(target)){
            throw new TargetNotFindException();
        }
        else{
            return doc.get(target, CC);
        }
    }

    public static <T> void setData(CollectionType type,String id, String target, T value) {
        UpdateResult result = getCollection(type).updateOne(Filters.eq("id", id), Updates.set(target, value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    public static <T> void addToList(CollectionType type, String id, String target, T value) {
        MongoCollection<Document> col = getCollection(type);

        UpdateResult result = col.updateOne(Filters.eq("id", id), Updates.push(target, value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    private static String genUniqueId(MongoCollection<Document> collection,String name){
        String newId;
        do {
            newId = name + (random.nextInt(9000) + 1000);
        } while (collection.countDocuments(Filters.eq("id", newId)) > 0);
        return newId;
    }

    public static String addNewUser(String name, String password, String role) {
        String newId = genUniqueId(usersCollection,"U");
        Document newUser = new Document("id", newId)
                .append("username", name)
                .append("password", password)
                .append("role", role)
                .append("accounts", new ArrayList<String>());
        usersCollection.insertOne(newUser);
        return newId;
    }

    public static String addNewAccount(String userId) {
        String newId = genUniqueId(accountsCollection,"A");
        Document newAccount = new Document("id", newId)
                .append("balance", 0)
                .append("history", new ArrayList<Document>());
        accountsCollection.insertOne(newAccount);
        addToList(CollectionType.USERS, userId, "accounts", newId);
        return newId;
    }

    public static void addNewHistory(String toId,int amount, String fromId,String description) {
        Document newHistory = new Document("date", new Date())
                .append("amount", amount)
                .append("fromId", fromId)
                .append("description", description);
        MongoDBUtil.addToList(CollectionType.ACCOUNTS, toId, "history", newHistory);
    }
}
