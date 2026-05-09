package nsysu.util.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import nsysu.util.enumtype.*;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.SameUserNameException;
import nsysu.util.exception.TargetNotFindException;
import nsysu.resources.ApplicationProperties;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public final class MongoDBUtil {
    private static final MongoCollection<Document> accountsCollection;
    private static final MongoCollection<Document> usersCollection;
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
        if (Objects.requireNonNull(type) == CollectionType.ACCOUNTS) {
            return accountsCollection;
        }
        return usersCollection;
    }

    public static <T> T getData(CollectionType type, String id, asTarget target, Class<T> CC) throws IdNotFindException, TargetNotFindException {
        Document doc = getCollection(type).find(Filters.eq("id", id)).first();
        if (doc == null) {
            throw new IdNotFindException();
        }
        else if (!doc.containsKey(target.getStr())) {
            throw new TargetNotFindException();
        }
        else{
            return doc.get(target, CC);
        }
    }

    public static <T> void setData(CollectionType type, String id, asTarget target, T value) throws IdNotFindException{
        UpdateResult result = getCollection(type).updateOne(Filters.eq("id", id), Updates.set(target.getStr(), value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    public static <T extends Number> void updateData(CollectionType type, String id, asTarget target, T value) throws IdNotFindException{
        UpdateResult result = getCollection(type).updateOne(Filters.eq("id", id), Updates.inc(target.getStr(), value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    public static <T> void addToList(CollectionType type, String id, asTarget target, T value) throws IdNotFindException{
        MongoCollection<Document> col = getCollection(type);

        UpdateResult result = col.updateOne(Filters.eq("id", id), Updates.push(target.getStr(), value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    private static String genUniqueId(MongoCollection<Document> collection,String name){
        String newId;
        do {
            newId = name + (random.nextInt(90000) + 10000);
        } while (collection.countDocuments(Filters.eq("id", newId)) > 0);
        return newId;
    }

    public static String addNewUser(String username, String password, RoleType role) throws SameUserNameException {
        if(usersCollection.countDocuments(Filters.eq(UserTarget.UserName.getStr(), username)) > 0){
            throw new SameUserNameException();
        }
        String newId = genUniqueId(usersCollection,"U");
        Document newUser = new Document("id", newId)
                .append(UserTarget.UserName.getStr(), username)
                .append(UserTarget.Password.getStr(), password)
                .append(UserTarget.Role.getStr(), role.getStr())
                .append(UserTarget.Status.getStr(), StatusType.Active)
                .append(UserTarget.Accounts.getStr(), new ArrayList<String>());
        usersCollection.insertOne(newUser);
        return newId;
    }

    public static boolean deleteData(CollectionType type,String id) {
        DeleteResult result = getCollection(type).deleteOne(Filters.eq("id", id));
        return result.getDeletedCount() > 0;
    }

    public static String addNewAccount(String userId, AccountType type) {
        String newId = genUniqueId(accountsCollection,"");
        Document newAccount = new Document("id", newId)
                .append(AccountTarget.Balance.getStr(), 0D)
                .append(AccountTarget.TimeOfLastView.getStr(), new Date())
                .append(AccountTarget.Type.getStr(), type.getStr())
                .append(UserTarget.Status.getStr(), StatusType.Active)
                .append(AccountTarget.History.getStr(), new ArrayList<Document>());
        accountsCollection.insertOne(newAccount);
        addToList(CollectionType.USERS, userId, UserTarget.Accounts, newId);
        return newId;
    }

    public static void addNewHistory(String toId,int amount, String fromId,String description) {
        Document newHistory = new Document("date", new Date())
                .append("amount", amount)
                .append("fromId", fromId)
                .append("description", description);
        MongoDBUtil.addToList(CollectionType.ACCOUNTS, toId, AccountTarget.History, newHistory);
    }
}
