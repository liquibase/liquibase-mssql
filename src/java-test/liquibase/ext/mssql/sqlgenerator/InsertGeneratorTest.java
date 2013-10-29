package liquibase.ext.mssql.sqlgenerator;

import liquibase.database.core.MSSQLDatabase;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.core.InsertStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class InsertGeneratorTest {
    @Test
    public void integrates() {
        InsertStatement statement = new InsertStatement(null, null, "TABLE_NAME");
        statement.addColumnValue("id", 1);
        statement.addColumnValue("name", "asdf");

        Sql[] sql = SqlGeneratorFactory.getInstance().generateSql(statement, new MSSQLDatabase());
        assertEquals(3, sql.length);

        for (Sql currentSql : sql) {
            assertSqlHasNoDanglingTokens(currentSql.toSql());
        }
    }

    private void assertSqlHasNoDanglingTokens(String s) {
        //System.out.println(s);
        assertFalse(s.contains("${"));
        assertFalse(s.contains("}"));
    }
}
