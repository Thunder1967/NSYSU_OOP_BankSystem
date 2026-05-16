package nsysu.test;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import nsysu.resources.ApplicationProperties;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;

/**
 * Hello world!
 *
 */
public class TestConnect
{
    public static void main(String[] args) {
        // Test
        String uri = ApplicationProperties.mongodbURL;

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("admin");

            try {
                BsonDocument command = new BsonDocument("ping", new BsonInt64(1));
                Document commandResult = database.runCommand(command);

                System.out.println("connect successful");
                System.out.println("server: " + commandResult.toJson());

            } catch (MongoException me) {
                System.err.println("connect fail");
                System.err.println("error: " + me.getMessage());
            }
        }
    }
}
