package liquibase.ext.mssql.change;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.core.LoadDataChange;
import liquibase.database.Database;
import liquibase.ext.mssql.MssqlUtil;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.statement.*;
import liquibase.statement.core.InsertSetStatement;
import liquibase.statement.core.RawSqlStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DatabaseChange(name = "loadData", description = "", priority = ChangeMetaData.PRIORITY_DATABASE)
public class LoadDataChangeMssql extends LoadDataChange {

    @Override
    public SqlStatement[] generateStatements(Database database) {
        List<SqlStatement> returnList = new ArrayList<>(Arrays.asList(super.generateStatements(database)));

        String catalogName;
        String schemaName;
        String tableName;
        final SqlStatement untypedStatement = returnList.get(0);
        if (untypedStatement instanceof ExecutablePreparedStatementBase) {
            final ExecutablePreparedStatementBase statement = (ExecutablePreparedStatementBase) untypedStatement;
            catalogName = statement.getCatalogName();
            schemaName = statement.getSchemaName();
            tableName = statement.getTableName();
        } else if (untypedStatement instanceof InsertSetStatement) {
            final InsertSetStatement statement = (InsertSetStatement) untypedStatement;
            catalogName = statement.getCatalogName();
            schemaName = statement.getSchemaName();
            tableName = statement.getTableName();
        } else {
            throw new RuntimeException("Unexpected statement type: "+untypedStatement.getClass().getName());
        }

        returnList.add(0, new RawSqlStatement(MssqlUtil.generateIdentityInsertSql("ON", catalogName, schemaName, tableName, database)));
        returnList.add(new RawSqlStatement(MssqlUtil.generateIdentityInsertSql("OFF", catalogName, schemaName, tableName, database)));

        return returnList.toArray(new SqlStatement[0]);
    }
}
