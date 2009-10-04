package liquibase.ext.mssql.sqlgenerator;

import org.junit.Test;
import static org.junit.Assert.*;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.InsertStatement;
import liquibase.database.core.MSSQLDatabase;
import liquibase.sql.Sql;

public class InsertGeneratorTest {
    @Test
    public void integrates() {
        InsertStatement statement = new InsertStatement(null, "TABLE_NAME");
        statement.addColumnValue("id", 1);
        statement.addColumnValue("name", "asdf");

        Sql[] sql = SqlGeneratorFactory.getInstance().generateSql(statement, new MSSQLDatabase());
        assertEquals(3, sql.length);
    }

}
