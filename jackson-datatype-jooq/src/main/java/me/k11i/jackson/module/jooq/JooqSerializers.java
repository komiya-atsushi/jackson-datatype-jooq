package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.jooq.Record;
import org.jooq.TableRecord;

import java.util.Set;

import static me.k11i.jackson.module.jooq.JooqSerializationFeature.USE_DEFAULT_BEAN_SERIALIZER_FOR_TABLE_RECORD;

class JooqSerializers extends Serializers.Base {
    private final JooqRecordSerializer jooqRecordSerializer;
    private final boolean usesDefaultBeanSerializerForTableRecord;

    JooqSerializers(Set<JooqSerializationFeature> enabledFeatures) {
        jooqRecordSerializer = new JooqRecordSerializer();
        usesDefaultBeanSerializerForTableRecord = enabledFeatures.contains(USE_DEFAULT_BEAN_SERIALIZER_FOR_TABLE_RECORD);
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> rawClass = type.getRawClass();
        if (Record.class.isAssignableFrom(rawClass)) {
            if (!usesDefaultBeanSerializerForTableRecord || !TableRecord.class.isAssignableFrom(rawClass)) {
                return jooqRecordSerializer;
            }
        }
        return null;
    }
}
