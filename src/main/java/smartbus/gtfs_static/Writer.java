package smartbus.gtfs_static;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;

import java.util.Collection;

public class Writer {
    private static final String DATABASE_NAME = "gtfs";

    private ArangoDB conn;

    public Writer(String host, int port, String username, String password) {
        conn = new ArangoDB.Builder().host(host, port).user(username).password(password).build();
        Collection<String> dbs = conn.getDatabases();
        if (!dbs.contains(DATABASE_NAME)) {
            conn.createDatabase(DATABASE_NAME);
        }
    }

    public void addCollection(String name, boolean shouldDrop) {
        Collection<CollectionEntity> cols = conn.db(DATABASE_NAME).getCollections();
        if (shouldDrop && cols.stream().anyMatch(col -> col.getName().equals(name))) {
            conn.db(DATABASE_NAME).collection(name).drop();
        }
//        if (!cols.stream().anyMatch(col -> col.getName().equals(name))) {
        conn.db(DATABASE_NAME).createCollection(name);
//        }
    }

    public void addDocument(String collection, BaseDocument doc) {
        conn.db(DATABASE_NAME).collection(collection).insertDocument(doc);
    }
}
