package liquibase.ext.mssql;

import liquibase.database.Database;
import liquibase.statement.core.InsertStatement;

import java.util.HashMap;
import java.util.Map;

public class MssqlUtil {

    public static final String IF_TABLE_HAS_IDENTITY_STATEMENT =
            "IF EXISTS(select TABLE_NAME\n" +
                    "            from INFORMATION_SCHEMA.COLUMNS\n" +
                    "           where TABLE_NAME = '${tableName}'\n" +
                    "             and TABLE_SCHEMA = '${schemaName}'\n" +
                    "             and COLUMNPROPERTY(object_id(TABLE_SCHEMA + '.' + TABLE_NAME), COLUMN_NAME, 'IsIdentity') = 1)\n" +
                    "\t${then}\n";


    public static String generateIdentityInsertSql( String onOrOff, String catalogName, String schemaName, String tableName, Database database) {
        return ifTableHasIdentityColumn(schemaName, tableName, "SET IDENTITY_INSERT " + database.escapeTableName(catalogName, schemaName, tableName) + " " +onOrOff, database);
    }

    private static String ifTableHasIdentityColumn(String schemaName, String tableName, String then, Database database) {
        if (schemaName == null) {
            schemaName = database.getDefaultSchemaName();
        }
        if (schemaName == null) {
            schemaName = "dbo";
        }

        Map<String, String> tokens = new HashMap<>();
        tokens.put("${tableName}", tableName);
        tokens.put("${schemaName}", schemaName);
        tokens.put("${then}", then);
        return performTokenReplacement(IF_TABLE_HAS_IDENTITY_STATEMENT, tokens);
    }

    private static String performTokenReplacement(String input, Map<String, String> tokens) {
        String result = input;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
