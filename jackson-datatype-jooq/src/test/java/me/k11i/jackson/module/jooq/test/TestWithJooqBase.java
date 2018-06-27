package me.k11i.jackson.module.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.TestTableRecord;
import org.jooq.impl.DSL;

public class TestWithJooqBase {
    @FunctionalInterface
    protected interface ThrowableConsumer<T> {
        void accept(T o) throws Exception;
    }

    private static final String DB_URL = "jdbc:h2:file:./tmp/unit_test";
    private static final String DB_USER = "sa";

    protected void withDslContext(ThrowableConsumer<DSLContext> consumer) throws Exception {
        try (DSLContext dslContext = DSL.using(DB_URL, DB_USER, "")) {
            consumer.accept(dslContext);
        }
    }

    protected void withTestTableRecords(ThrowableConsumer<Result<TestTableRecord>> consumer) throws Exception {
        withDslContext(dslContext -> {
            Result<TestTableRecord> records = dslContext.selectFrom(Tables.TEST_TABLE)
                    .orderBy(Tables.TEST_TABLE.INT_COL)
                    .fetch();

            consumer.accept(records);
        });
    }
}
