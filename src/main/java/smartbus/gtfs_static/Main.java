package smartbus.gtfs_static;

import com.arangodb.entity.BaseDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Long startTime = System.currentTimeMillis();
        Reader dbReader = new Reader("jdbc:mysql://localhost:3306/transitnet?user=transitnet&password=transitnet");
        List<DbTable> tables = dbReader.getTables();
        log.info("MySQL Connected, {} tables loaded", tables.size());
        Writer dbWriter = new Writer("HOST", 8529, "root", "123123");
        log.info("ArangoDB Connected.");
        for (DbTable table : tables) {
            int size = dbReader.getTableSize(table.name);
            log.info("Reading table {}, size is {}", table.name, size);
            ResultSet datas = dbReader.getTableData(table.name);
            dbWriter.addCollection(table.name);
            int count = 0;
            double lastNotice = 0.0;
            while (datas.next()) {
                BaseDocument doc = Parser.parseTableToDocument(table, datas);
                dbWriter.addDocument(table.name, doc);
                count++;
                double percentage = count * 100.0 / size;
                if (percentage - lastNotice > 2) {
                    log.info("Table {}, process {}%", table.name, String.format("%.1f", percentage));
                }
                lastNotice = percentage;
            }
            log.warn("Table {} loaded!", table.name);
        }
        Long endTime = System.currentTimeMillis();
        log.info("All table loaded, cost {}s", (endTime - startTime) / 1000);
        System.exit(0);
    }
}
