package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
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
        boolean omitsNull = false;
        boolean omitsEmpty = false;

        SerializationConfig config = provider.getConfig();
        if (config != null) {
            namingStrategy = config.getPropertyNamingStrategy();

            JsonInclude.Value defaultPropertyInclusion = config.getDefaultPropertyInclusion();
            if (defaultPropertyInclusion != null) {

                JsonInclude.Include valueInclusion = defaultPropertyInclusion.getValueInclusion();
                if (valueInclusion != null) {
                    switch (valueInclusion) {
                        case NON_DEFAULT:
                        case NON_EMPTY:
                            omitsEmpty = true;
                            // fall through

                        case NON_NULL:
                        case NON_ABSENT:
                            omitsNull = true;
                            break;

                        default:
                            break;
                    }
                }
            }
        }
        if (namingStrategy == null) {
            namingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE;
        }

        gen.writeStartObject();

        for (int i = 0; i < fields.length; i++) {
            Field<?> field = fields[i];
            Object value = record.get(i);

            if (value != null) {
                if (omitsEmpty) {
                    JsonSerializer<Object> valueSerializer = provider.findValueSerializer(field.getType());
                    if (valueSerializer != null && valueSerializer.isEmpty(provider, value)) {
                        continue;
                    }
                }

            } else if (omitsNull) {
                continue;
            }

            String fieldName = namingStrategy.nameForField(config, null, Utils.toCamelCase(field.getName()));

            gen.writeObjectField(fieldName, value);
        }

        gen.writeEndObject();
    }
}
