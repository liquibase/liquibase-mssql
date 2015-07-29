package liquibase.ext.mssql.sqlgenerator;

import liquibase.database.Database;
import liquibase.exception.ValidationErrors;

import liquibase.database.core.MSSQLDatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import liquibase.sqlgenerator.core.InsertOrUpdateGenerator;
import liquibase.sqlgenerator.core.InsertOrUpdateGeneratorMSSQL;
import liquibase.statement.core.InsertOrUpdateStatement;
import liquibase.statement.core.InsertStatement;

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
        List<Sql> sql = new ArrayList(Arrays.asList(sqlGeneratorChain.generateSql(statement, database)));

        sql.add(0, new UnparsedSql("SET IDENTITY_INSERT "+ database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName()) +" ON"));

        sql.add(new UnparsedSql("SET IDENTITY_INSERT "+ database.escapeTableName(statement.getCatalogName(), statement.getSchemaName(), statement.getTableName()) +" OFF"));

        return sql.toArray(new Sql[sql.size()]);
    }
}
