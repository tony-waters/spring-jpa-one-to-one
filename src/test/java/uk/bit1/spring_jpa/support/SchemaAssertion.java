package uk.bit1.spring_jpa.support;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public final class SchemaAssertion {

    private SchemaAssertion() {
    }

    public static boolean columnExists(JdbcTemplate jdbc, String tableName, String columnName) {
        Integer count = jdbc.queryForObject("""
            select count(*)
            from information_schema.columns
            where upper(table_name) = upper(?)
              and upper(column_name) = upper(?)
            """, Integer.class, tableName, columnName);
        return count != null && count > 0;
    }

    public static boolean uniqueConstraintExistsForColumn(JdbcTemplate jdbc, String tableName, String columnName) {
        List<String> constraints = jdbc.queryForList("""
            select tc.constraint_name
            from information_schema.table_constraints tc
            join information_schema.constraint_column_usage ccu
              on tc.constraint_name = ccu.constraint_name
             and tc.table_name = ccu.table_name
            where upper(tc.table_name) = upper(?)
              and upper(ccu.column_name) = upper(?)
              and tc.constraint_type = 'UNIQUE'
            """, String.class, tableName, columnName);
        return !constraints.isEmpty();
    }
}