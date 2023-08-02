package smartbus.gtfs_static;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Parser {

    public static BaseDocument parseTableToDocument(DbTable tableSchema, ResultSet rowData) throws SQLException {
        BaseDocument doc = new BaseDocument();
        // set key
        if (tableSchema.getPrimaryKey().size() > 0) {
            String key = (String) tableSchema.getPrimaryKey().stream().map(k -> {
                try {
                    return rowData.getObject(k);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).reduce((a, b) -> "%s-%s".formatted(a, b)).get();
            key = key.replace(' ', '_');
            doc.setKey(key);
        }
        // set columns
        for (TableColumn column : tableSchema.columns) {
            Object value = rowData.getObject(column.getName());
            doc.addAttribute(column.getName(), value);
        }
        return doc;
    }
}
