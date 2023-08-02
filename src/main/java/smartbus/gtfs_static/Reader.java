package smartbus.gtfs_static;

import java.sql.*;
import java.util.*;

public class Reader {
    private static final List<String> STOP_TABLES = Arrays.asList("real_time_data_temp", "current", "status", "sys_config","shapes");

    private final Connection conn;

    public Reader(String url) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection(url);
    }

    public List<DbTable> getTables() throws Exception {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tableMetas = metaData.getTables(null, "%", "%", new String[]{"TABLE"});
        List<String> importTables = new ArrayList<>();
        while (tableMetas.next()) {
            if (!STOP_TABLES.contains(tableMetas.getString("TABLE_NAME"))) {
                importTables.add(tableMetas.getString("TABLE_NAME"));
            }
        }
        List<DbTable> tables = new ArrayList<>();
        for (String importTable : importTables) {
            tables.add(readTableSchema(importTable, metaData));
        }
        return tables;
    }

    private DbTable readTableSchema(String tableName, DatabaseMetaData metaData) throws Exception {
        ResultSet cols = metaData.getColumns(null, "%", tableName, "%");
        List<TableColumn> columns = new ArrayList<>();
        while (cols.next()) {
            TableColumn column = new TableColumn();
            column.setName(cols.getString("COLUMN_NAME"));
            column.setType(cols.getInt("DATA_TYPE"));
            column.setDecimal(cols.getInt("DECIMAL_DIGITS") != 0);
            columns.add(column);
        }
        // read primary keys
        ResultSet pks = metaData.getPrimaryKeys(null, null, tableName);
        int size = 0;
        while (pks.next()) {
            size++;
        }
        pks.beforeFirst();
        String[] primaryKeyArray = new String[size];
        while (pks.next()) {
            String col = pks.getString("COLUMN_NAME");
            short seq = pks.getShort("KEY_SEQ");
            primaryKeyArray[seq - 1] = col;
        }
        DbTable table = new DbTable(tableName, columns);
        table.setPrimaryKey(List.of(primaryKeyArray));
        return table;
    }

    public int getTableSize(String tableName) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("select count(1) from " + tableName + ";");
//        stmt.setString(1, tableName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        throw new Exception("The table contains no data:" + tableName);
    }

    public ResultSet getTableData(String tableName) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("select * from " + tableName + ";");
        stmt.setFetchSize(10000);
//        stmt.setString(1, tableName);
        return stmt.executeQuery();
    }
}
