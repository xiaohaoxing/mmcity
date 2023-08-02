package smartbus.gtfs_static;

import java.sql.Types;
import java.util.List;

public class DbTable {
    public String name;

    public List<TableColumn> columns;

    public List<String> primaryKey;

    public DbTable(String name, List<TableColumn> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<TableColumn> columns) {
        this.columns = columns;
    }

    public List<String> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(List<String> primaryKey) {
        this.primaryKey = primaryKey;
    }
}

class TableColumn {
    private String name;
    /* refer to java.sql.Types */
    private int type;

    private boolean isDecimal;

    private boolean isPrimary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public void setDecimal(boolean decimal) {
        isDecimal = decimal;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isString() {
        return type == Types.VARCHAR;
    }

    public boolean isInt() {
        return type == Types.INTEGER;
    }

    public boolean isDouble() {
        return type == Types.REAL;
    }

    @Override
    public String toString() {
        return "TableColumn{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", isDecimal=" + isDecimal +
                '}';
    }
}
