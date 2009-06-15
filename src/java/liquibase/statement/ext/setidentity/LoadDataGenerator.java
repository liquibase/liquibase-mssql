package liquibase.statement.ext.setidentity;

import liquibase.database.Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGenerator;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.InsertStatement;

import java.util.Arrays;
import java.util.List;

public class LoadDataGenerator implements SqlGenerator<InsertStatement> {
    public int getPriority() {
        return 15;
    }

    public boolean supports(InsertStatement statement, Database database) {
        return database instanceof MSSQLDatabase;
    }

    public ValidationErrors validate(InsertStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        return sqlGeneratorChain.validate(statement, database);
    }

    public Sql[] generateSql(InsertStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        List<Sql> sql = Arrays.asList(sqlGeneratorChain.generateSql(statement, database));

        sql.add(0, new UnparsedSql("SET IDENTITY_INSERT "+ database.escapeTableName(statement.getSchemaName(), statement.getTableName()) +" ON"));

        sql.add(new UnparsedSql("SET IDENTITY_INSERT "+ database.escapeTableName(statement.getSchemaName(), statement.getTableName()) +" OFF"));

        return sql.toArray(new Sql[sql.size()]);
    }
}
