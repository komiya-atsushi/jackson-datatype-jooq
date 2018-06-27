package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.jooq.Field;
import org.jooq.Record;

import java.io.IOException;

/**
 * Serializer for {@link Record} object.
 */
class JooqRecordSerializer extends StdSerializer<Record> {
    JooqRecordSerializer() {
        super(Record.class);
    }

    @Override
    public void serialize(Record record, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Field<?>[] fields = record.fields();

        PropertyNamingStrategy namingStrategy = null;

        SerializationConfig config = provider.getConfig();
        if (config != null) {
            namingStrategy = config.getPropertyNamingStrategy();
        }

        gen.writeStartObject();

        for (int i = 0; i < fields.length; i++) {
            String fieldName = Utils.toCamelCase(fields[i].getName());
            if (namingStrategy != null) {
                try {
                    fieldName = namingStrategy.nameForField(config, null, fieldName);
                } catch (Exception ignore) {
                }
            }

            Object value = record.get(i);
            gen.writeObjectField(fieldName, value);
        }

        gen.writeEndObject();
    }
}
