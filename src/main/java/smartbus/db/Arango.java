package smartbus.db;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.ArangoConfiguration;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@EnableArangoRepositories(basePackages = {"com.arangodb.spring.demo"})
public class Arango implements Database, ArangoConfiguration {
    @Override
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder().host("localhost", 8529).user("root").password("123123");
    }

    @Override
    public String database() {
        return "testdb";
    }

    @Override
    public void connect(String url) {

    }

    public static void test(String[] args) {
        String dbName = "demo";
        String colName = "gtfs";
        String docKey = "nyc";
        Arango test = new Arango();
        ArangoDB db = test.arango().build();
        // create database
        Collection<String> dbs = db.getDatabases();
        if (!dbs.contains(dbName)) {
            db.createDatabase(dbName);
        }
        // create collection
        Collection<CollectionEntity> cols = db.db(dbName).getCollections();
        if (cols.stream().anyMatch(col -> col.getName().equals(colName))) {
            db.db(dbName).collection(colName).drop();
        }
        db.db(dbName).createCollection(colName);
        BaseDocument doc = new BaseDocument();
        doc.setKey("nyc");
        doc.addAttribute("agency_id", "MTA NYCT");
        doc.addAttribute("agency_name", "MTA New York City Transit");
        doc.addAttribute("agency_url", "http://www.mta.info");
        doc.addAttribute("agency_timezone", "America/New_York");
        doc.addAttribute("agency_lang", "en");
        doc.addAttribute("agency_phone", "718-330-1234");
        doc.addAttribute("integer", 1234);
        // create document
        db.db(dbName).collection(colName).insertDocument(doc);
        // read document
        BaseDocument readDoc = db.db(dbName).collection(colName).getDocument(docKey, BaseDocument.class);
        System.out.println(readDoc.getAttribute("agency_name"));
        System.out.println(readDoc.getAttribute("integer"));

        // read json (jackson) document
        ObjectNode jsonDoc = db.db(dbName).collection(colName).getDocument(docKey, ObjectNode.class);
        System.out.println(jsonDoc.get("agency_name").textValue());
        System.out.println(jsonDoc.get("integer").intValue());

        // update document
        readDoc.addAttribute("foo", "bar");
        db.db(dbName).collection(colName).updateDocument(docKey, readDoc);

        // read again
        BaseDocument readNewDoc = db.db(dbName).collection(colName).getDocument(docKey, BaseDocument.class);
        System.out.println(readNewDoc.getAttribute("foo"));

        // create several mock docs
        for (int i = 0; i < 10; i++) {
            BaseDocument batchDoc = new BaseDocument();
            batchDoc.setKey("batch-" + i);
            batchDoc.addAttribute("order", i);
            db.db(dbName).collection(colName).insertDocument(batchDoc);
        }
        // query with aql
        String query = "FOR t IN " + colName + " FILTER t.order % 2 == 1 RETURN t";
//        Map<String, Object> bindVars = Collections.singletonMap("", "");
        ArangoCursor<BaseDocument> cursor = db.db(dbName).query(query, BaseDocument.class);
        cursor.forEachRemaining(filterDoc -> System.out.println(filterDoc.getKey()));

        System.out.println("done");
        System.exit(0);
        return;
    }
}
