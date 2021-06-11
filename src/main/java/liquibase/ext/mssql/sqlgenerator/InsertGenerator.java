package liquibase.ext.mssql.sqlgenerator;

import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.ext.mssql.MssqlUtil;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.InsertStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertGenerator extends liquibase.sqlgenerator.core.InsertGenerator {

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
        List<Sql> sql = new ArrayList<>(Arrays.asList(sqlGeneratorChain.generateSql(statement, database)));
        sql.add(0, new UnparsedSql(MssqlUtil.generateIdentityInsertSql("ON", statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), database)));
        sql.add(new UnparsedSql(MssqlUtil.generateIdentityInsertSql("OFF", statement.getCatalogName(), statement.getSchemaName(), statement.getTableName(), database)));
        return sql.toArray(new Sql[sql.size()]);
    }


}
