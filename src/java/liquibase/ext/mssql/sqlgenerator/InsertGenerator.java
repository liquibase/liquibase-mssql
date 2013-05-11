package liquibase.ext.mssql.sqlgenerator;

import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.InsertStatement;

import java.util.*;

public class InsertGenerator extends liquibase.sqlgenerator.core.InsertGenerator {
    public static final String IF_TABLE_HAS_IDENTITY_STATEMENT =
            "IF EXISTS(select TABLE_NAME\n" +
            "            from INFORMATION_SCHEMA.COLUMNS\n" +
            "           where TABLE_SCHEMA = '${schemaName}'\n" +
            "             and COLUMNPROPERTY(object_id(TABLE_NAME), COLUMN_NAME, 'IsIdentity') = 1\n" +
            "             and TABLE_NAME='${tableName}'\n" +
            "             and TABLE_SCHEMA='${schemaName})'\n" +
            "\t${then}\n";

    @Override
    public int getPriority() {
        return 15;
    }

    public boolean supports(InsertStatement statement, Database database) {
        return database instanceof MSSQLDatabase;
    }

    public ValidationErrors validate(InsertStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        return sqlGeneratorChain.validate(statement, database);
    }

    @Override
    public Sql[] generateSql(InsertStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        String tableName = database.escapeTableName(statement.getSchemaName(), statement.getTableName());
        String enableIdentityInsert = "SET IDENTITY_INSERT " + tableName + " ON";
        String disableIdentityInsert = "SET IDENTITY_INSERT " + tableName + " OFF";
        String safelyEnableIdentityInsert = ifTableHasIdentityColumn(enableIdentityInsert, statement);
        String safelyDisableIdentityInsert = ifTableHasIdentityColumn(disableIdentityInsert, statement);

        List<Sql> sql = new ArrayList(Arrays.asList(sqlGeneratorChain.generateSql(statement, database)));
        sql.add(0, new UnparsedSql(safelyEnableIdentityInsert));
        sql.add(new UnparsedSql(safelyDisableIdentityInsert));
        return sql.toArray(new Sql[sql.size()]);
    }

    private String ifTableHasIdentityColumn(String then, InsertStatement statement) {
        String tableName = statement.getTableName();
        String schemaName = statement.getSchemaName();
        if (schemaName == null)
            schemaName = "dbo";

        Map<String, String> tokens = new HashMap<String, String>();
        tokens.put("${tableName}", tableName);
        tokens.put("${schemaName}", schemaName);
        tokens.put("${then}", then);
        return performTokenReplacement(IF_TABLE_HAS_IDENTITY_STATEMENT, tokens);
    }

    private String performTokenReplacement(String input, Map<String, String> tokens) {
        String result = input;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}