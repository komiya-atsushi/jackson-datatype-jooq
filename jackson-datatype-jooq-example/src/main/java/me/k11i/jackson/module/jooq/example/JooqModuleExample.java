package me.k11i.jackson.module.jooq.example;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.k11i.jackson.module.jooq.JooqModule;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.PlanetRecord;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JooqModuleExample {
    public static void main(String[] args) throws SQLException, JsonProcessingException {
        if (args.length < 1) {
            System.out.println("You should specify JDBC URL to connect H2 DB");
            System.exit(1);
        }

        String user = "sa";
        String password = "";
        String url = args[0];

        try (Connection conn = DriverManager.getConnection(url, user, password);
             DSLContext context = DSL.using(conn, SQLDialect.H2)) {

            serializeTableRecords(context);

            System.out.println("==========");

            serializeRecords(context);
        }
    }

    private static void serializeTableRecords(DSLContext context) throws JsonProcessingException {
        Result<PlanetRecord> planets = context.selectFrom(Tables.PLANET)
                .fetch();

        String result = new ObjectMapper()
                .registerModule(new JooqModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(planets);
        System.out.println(result);

        System.out.println("----------");

        result = new ObjectMapper()
                .registerModule(new JooqModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(planets);
        System.out.println(result);
    }

    private static void serializeRecords(DSLContext context) throws JsonProcessingException {
        Result<Record2<String, Double>> planets = context.select(Tables.PLANET.NAME, Tables.PLANET.MEAN_RADIUS_KM)
                .from(Tables.PLANET)
                .where(Tables.PLANET.DISCOVERY_DATE.isNull())
                .fetch();

        String result = new ObjectMapper()
                .registerModule(new JooqModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(planets);
        System.out.println(result);
    }
}
