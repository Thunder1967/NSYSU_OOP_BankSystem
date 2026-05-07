package nsysu.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import nsysu.resources.ApplicationProperties;
import org.bson.Document;

import java.util.Optional;
import java.util.Random;

public final class MongoDBUtil {
    private static MongoCollection<Document> collection;

    static {
        String uri = ApplicationProperties.mongodbURL;
        MongoClient mongoClient = MongoClients.create(uri);
        collection = mongoClient.getDatabase(ApplicationProperties.Database).getCollection(ApplicationProperties.Collection);
    }
    private MongoDBUtil() {}

    public static <T> T getData(String id, String target, Class<T> CC) {
        Document doc = collection.find(Filters.eq("id", id)).first();
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

    public static <T> void setData(String id, String target, T value) {
        UpdateResult result = collection.updateOne(Filters.eq("id", id), Updates.set(target, value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    public static String addNewAccount(String name, String password) {
        String newId;
        do {
            newId = "NSYSU" + (new Random().nextInt(90000) + 10000);
        } while (collection.countDocuments(Filters.eq("id", newId)) > 0);
        Document newUser = new Document("id", newId)
                .append("name", name)
                .append("password", password)
                .append("balance", 0)
                .append("role", "user");
        collection.insertOne(newUser);
        return newId;
    }
}
