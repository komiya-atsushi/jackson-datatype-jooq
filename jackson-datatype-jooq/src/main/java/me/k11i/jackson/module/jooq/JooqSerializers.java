package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.jooq.Record;

class JooqSerializers extends Serializers.Base {
    private final JooqRecordSerializer jooqRecordSerializer;

    JooqSerializers() {
        jooqRecordSerializer = new JooqRecordSerializer();
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> rawClass = type.getRawClass();
        if (Record.class.isAssignableFrom(rawClass)) {
            return jooqRecordSerializer;
        }
        return null;
    }
}
