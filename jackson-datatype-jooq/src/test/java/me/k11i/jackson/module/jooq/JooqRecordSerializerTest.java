package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.k11i.jackson.module.jooq.test.TestWithJooqBase;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.data.Percentage;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;
import org.jooq.Field;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.generated.tables.TestTable.TEST_TABLE;

class JooqRecordSerializerTest extends TestWithJooqBase {
    static class JsonAttributeDictionary {
        static final JsonAttributeDictionary LOWER_CAMEL_CASE = new JsonAttributeDictionary()
                .name(TEST_TABLE.INT_COL, "intCol")
                .name(TEST_TABLE.TINYINT_COL, "tinyintCol")
                .name(TEST_TABLE.BIGINT_COL, "bigintCol")
                .name(TEST_TABLE.DECIMAL_COL, "decimalCol")
                .name(TEST_TABLE.DOUBLE_COL, "doubleCol")
                .name(TEST_TABLE.STRING_COL, "stringCol")
                .name(TEST_TABLE.BOOL_COL, "boolCol")
                .name(TEST_TABLE.TIME_COL, "timeCol")
                .name(TEST_TABLE.DATE_COL, "dateCol")
                .name(TEST_TABLE.TIMESTAMP_TZ_COL, "timestampTzCol")
                .name(TEST_TABLE.ARRAY_COL, "arrayCol")
                .name(TEST_TABLE.NULLABLE_COL, "nullableCol");

        static final JsonAttributeDictionary UPPER_CAMEL_CASE = new JsonAttributeDictionary()
                .name(TEST_TABLE.INT_COL, "IntCol")
                .name(TEST_TABLE.TINYINT_COL, "TinyintCol")
                .name(TEST_TABLE.BIGINT_COL, "BigintCol")
                .name(TEST_TABLE.DECIMAL_COL, "DecimalCol")
                .name(TEST_TABLE.DOUBLE_COL, "DoubleCol")
                .name(TEST_TABLE.STRING_COL, "StringCol")
                .name(TEST_TABLE.BOOL_COL, "BoolCol")
                .name(TEST_TABLE.TIME_COL, "TimeCol")
                .name(TEST_TABLE.DATE_COL, "DateCol")
                .name(TEST_TABLE.TIMESTAMP_TZ_COL, "TimestampTzCol")
                .name(TEST_TABLE.ARRAY_COL, "ArrayCol")
                .name(TEST_TABLE.NULLABLE_COL, "NullableCol");

        static final JsonAttributeDictionary SNAKE_CASE = new JsonAttributeDictionary()
                .name(TEST_TABLE.INT_COL, "int_col")
                .name(TEST_TABLE.TINYINT_COL, "tinyint_col")
                .name(TEST_TABLE.BIGINT_COL, "bigint_col")
                .name(TEST_TABLE.DECIMAL_COL, "decimal_col")
                .name(TEST_TABLE.DOUBLE_COL, "double_col")
                .name(TEST_TABLE.STRING_COL, "string_col")
                .name(TEST_TABLE.BOOL_COL, "bool_col")
                .name(TEST_TABLE.TIME_COL, "time_col")
                .name(TEST_TABLE.DATE_COL, "date_col")
                .name(TEST_TABLE.TIMESTAMP_TZ_COL, "timestamp_tz_col")
                .name(TEST_TABLE.ARRAY_COL, "array_col")
                .name(TEST_TABLE.NULLABLE_COL, "nullable_col");

        static final JsonAttributeDictionary KEBAB_CASE = new JsonAttributeDictionary()
                .name(TEST_TABLE.INT_COL, "int-col")
                .name(TEST_TABLE.TINYINT_COL, "tinyint-col")
                .name(TEST_TABLE.BIGINT_COL, "bigint-col")
                .name(TEST_TABLE.DECIMAL_COL, "decimal-col")
                .name(TEST_TABLE.DOUBLE_COL, "double-col")
                .name(TEST_TABLE.STRING_COL, "string-col")
                .name(TEST_TABLE.BOOL_COL, "bool-col")
                .name(TEST_TABLE.TIME_COL, "time-col")
                .name(TEST_TABLE.DATE_COL, "date-col")
                .name(TEST_TABLE.TIMESTAMP_TZ_COL, "timestamp-tz-col")
                .name(TEST_TABLE.ARRAY_COL, "array-col")
                .name(TEST_TABLE.NULLABLE_COL, "nullable-col");

        private final Map<Field<?>, String> attributeNames = new HashMap<>();

        JsonAttributeDictionary name(Field<?> f, String attributeName) {
            attributeNames.put(f, attributeName);
            return this;
        }

        String get(Field<?> f) {
            return attributeNames.get(f);
        }

        String[] listAttributesAsArray(FieldList list) {
            return list.fields.stream()
                    .map(attributeNames::get)
                    .toArray(String[]::new);
        }
    }

    static class FieldList {
        static final FieldList ALL = new FieldList(
                TEST_TABLE.INT_COL,
                TEST_TABLE.TINYINT_COL,
                TEST_TABLE.BIGINT_COL,
                TEST_TABLE.DECIMAL_COL,
                TEST_TABLE.DOUBLE_COL,
                TEST_TABLE.STRING_COL,
                TEST_TABLE.BOOL_COL,
                TEST_TABLE.TIME_COL,
                TEST_TABLE.DATE_COL,
                TEST_TABLE.TIMESTAMP_TZ_COL,
                TEST_TABLE.ARRAY_COL,
                TEST_TABLE.NULLABLE_COL);

        static final FieldList WITHOUT_ARRAY = ALL.exclude(TEST_TABLE.ARRAY_COL);

        static final FieldList WITHOUT_NULLABLE = ALL.exclude(TEST_TABLE.NULLABLE_COL);

        static final FieldList WITHOUT_ARRAY_AND_NULLABLE = ALL.exclude(
                TEST_TABLE.ARRAY_COL,
                TEST_TABLE.NULLABLE_COL);

        static final FieldList WITHOUT_STRING = ALL.exclude(TEST_TABLE.STRING_COL);

        final List<Field<?>> fields;

        FieldList(Field<?>... fields) {
            this.fields = Arrays.asList(fields);
        }

        FieldList(List<Field<?>> fields) {
            this.fields = fields;
        }

        List<Field<?>> fields() {
            return fields;
        }

        FieldList exclude(Field<?>... fieldsToExclude) {
            Set<String> names = Arrays.stream(fieldsToExclude)
                    .map(Field::getName)
                    .collect(Collectors.toSet());
            return fields.stream()
                    .filter(f -> !names.contains(f.getName()))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), FieldList::new));
        }
    }

    static class ExpectedValues {
        static final ExpectedValues RECORD_0 = new ExpectedValues()
                .value(TEST_TABLE.INT_COL, 0)
                .value(TEST_TABLE.TINYINT_COL, 0)
                .value(TEST_TABLE.BIGINT_COL, 0)
                .value(TEST_TABLE.DECIMAL_COL, 0.0)
                .value(TEST_TABLE.DOUBLE_COL, 0.0)
                .value(TEST_TABLE.STRING_COL, "")
                .value(TEST_TABLE.BOOL_COL, false)
                .value(TEST_TABLE.TIME_COL, "00:00:00")
                .value(TEST_TABLE.DATE_COL, "1970-01-01")
                .value(TEST_TABLE.TIMESTAMP_TZ_COL, "1970-01-01T00:00:00Z")
                .value(TEST_TABLE.ARRAY_COL, Arrays.asList(1, 3, 5))
                .value(TEST_TABLE.NULLABLE_COL, false);

        static final ExpectedValues RECORD_1 = new ExpectedValues()
                .value(TEST_TABLE.INT_COL, 1)
                .value(TEST_TABLE.TINYINT_COL, 2)
                .value(TEST_TABLE.BIGINT_COL, 3)
                .value(TEST_TABLE.DECIMAL_COL, 4.5)
                .value(TEST_TABLE.DOUBLE_COL, 0.5)
                .value(TEST_TABLE.STRING_COL, "abc")
                .value(TEST_TABLE.BOOL_COL, true)
                .value(TEST_TABLE.TIME_COL, "12:34:56")
                .value(TEST_TABLE.DATE_COL, "2018-01-01")
                .value(TEST_TABLE.TIMESTAMP_TZ_COL, "2018-01-01T12:34:56+09:00")
                .value(TEST_TABLE.ARRAY_COL, Collections.emptyList())
                .value(TEST_TABLE.NULLABLE_COL, false);

        static final ExpectedValues RECORD_2 = new ExpectedValues()
                .value(TEST_TABLE.INT_COL, 2)
                .value(TEST_TABLE.TINYINT_COL, 3)
                .value(TEST_TABLE.BIGINT_COL, 4)
                .value(TEST_TABLE.DECIMAL_COL, 5.6)
                .value(TEST_TABLE.DOUBLE_COL, 0.25)
                .value(TEST_TABLE.STRING_COL, "ABC")
                .value(TEST_TABLE.BOOL_COL, false)
                .value(TEST_TABLE.TIME_COL, "11:22:33")
                .value(TEST_TABLE.DATE_COL, "2018-02-02")
                .value(TEST_TABLE.TIMESTAMP_TZ_COL, "2018-02-02T11:22:33Z")
                .value(TEST_TABLE.ARRAY_COL, Arrays.asList(1, 2))
                .value(TEST_TABLE.NULLABLE_COL, null);

        static final ExpectedValues RECORD_3 = new ExpectedValues()
                .value(TEST_TABLE.INT_COL, 3)
                .value(TEST_TABLE.TINYINT_COL, 4)
                .value(TEST_TABLE.BIGINT_COL, 5)
                .value(TEST_TABLE.DECIMAL_COL, 6.7)
                .value(TEST_TABLE.DOUBLE_COL, 0.125)
                .value(TEST_TABLE.STRING_COL, "123")
                .value(TEST_TABLE.BOOL_COL, true)
                .value(TEST_TABLE.TIME_COL, "22:33:44")
                .value(TEST_TABLE.DATE_COL, "2018-03-03")
                .value(TEST_TABLE.TIMESTAMP_TZ_COL, "2018-03-03T22:33:44-09:00")
                .value(TEST_TABLE.ARRAY_COL, Collections.emptyList())
                .value(TEST_TABLE.NULLABLE_COL, null);

        private final Map<Field<?>, Object> values = new HashMap<>();

        ExpectedValues value(Field<?> f, Object value) {
            values.put(f, value);
            return this;
        }

        Object get(Field<?> f) {
            return values.get(f);
        }

        static ExpectedValues get(int arrayIndex) {
            return Arrays.asList(RECORD_0, RECORD_1, RECORD_2, RECORD_3).get(arrayIndex);
        }
    }

    static class AssertionContext {
        final int arrayIndex;

        Field<?> field;
        String attributeName;

        AssertionContext(int arrayIndex) {
            this.arrayIndex = arrayIndex;
        }

        AssertionContext withFieldAndAttributeName(Field<?> field, String attributeName) {
            AssertionContext result = new AssertionContext(arrayIndex);
            result.field = field;
            result.attributeName = attributeName;
            return result;
        }

        Description toDescription() {
            if (attributeName == null) {
                return new TextDescription("JsonPath: $[%d]", arrayIndex);
            } else {
                return new TextDescription("JsonPath: $[%d].%s (was serialized from %s)", arrayIndex, attributeName, field);
            }
        }

        ListAssert assertThat(List<?> l) {
            return Assertions.assertThat(l).as(this.toDescription());
        }

        MapAssert<String, ?> assertThat(Map<String, ?> m) {
            return Assertions.assertThat(m).as(this.toDescription());
        }

        AbstractObjectAssert<?, ?> assertThat(Object o) {
            return Assertions.assertThat(o).as(this.toDescription());
        }

        AbstractIntegerAssert<?> assertThat(Integer v) {
            return Assertions.assertThat(v).as(this.toDescription());
        }

        AbstractDoubleAssert<?> assertThat(Double v) {
            return Assertions.assertThat(v).as(this.toDescription());
        }

        AbstractCharSequenceAssert<?, String> assertThat(String v) {
            return Assertions.assertThat(v).as(this.toDescription());
        }

        AbstractBooleanAssert<?> assertThat(Boolean v) {
            return Assertions.assertThat(v).as(this.toDescription());
        }
    }

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

    static List<Arguments> testSerialize() {
        return Arrays.asList(
                Arguments.of(
                        "Default setting",
                        new ObjectMapper(),
                        new JooqModule(),
                        JsonAttributeDictionary.LOWER_CAMEL_CASE,
                        Arrays.asList(FieldList.ALL, FieldList.ALL, FieldList.ALL, FieldList.ALL)),

                // -- Naming strategy

                Arguments.of(
                        "PropertyNamingStrategy: Upper camel case",
                        new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE),
                        new JooqModule(),
                        JsonAttributeDictionary.UPPER_CAMEL_CASE,
                        Arrays.asList(FieldList.ALL, FieldList.ALL, FieldList.ALL, FieldList.ALL)),
                Arguments.of(
                        "PropertyNamingStrategy: Snake case",
                        new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE),
                        new JooqModule(),
                        JsonAttributeDictionary.SNAKE_CASE,
                        Arrays.asList(FieldList.ALL, FieldList.ALL, FieldList.ALL, FieldList.ALL)),
                Arguments.of(
                        "PropertyNamingStrategy: Kebab case",
                        new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE),
                        new JooqModule(),
                        JsonAttributeDictionary.KEBAB_CASE,
                        Arrays.asList(FieldList.ALL, FieldList.ALL, FieldList.ALL, FieldList.ALL)),

                // -- Serialization inclusion

                Arguments.of(
                        "Serialization inclusion: Non-null",
                        new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL),
                        new JooqModule(),
                        JsonAttributeDictionary.LOWER_CAMEL_CASE,
                        Arrays.asList(
                                FieldList.ALL,
                                FieldList.ALL,
                                FieldList.WITHOUT_NULLABLE,
                                FieldList.WITHOUT_NULLABLE)),

                Arguments.of(
                        "Serialization inclusion: Non-empty",
                        new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY),
                        new JooqModule(),
                        JsonAttributeDictionary.LOWER_CAMEL_CASE,
                        Arrays.asList(
                                FieldList.WITHOUT_STRING,
                                FieldList.WITHOUT_ARRAY,
                                FieldList.WITHOUT_NULLABLE,
                                FieldList.WITHOUT_ARRAY_AND_NULLABLE)),

                Arguments.of(
                        "Serialization inclusion: Non-default",
                        new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_DEFAULT),
                        new JooqModule(),
                        JsonAttributeDictionary.LOWER_CAMEL_CASE,
                        Arrays.asList(
                                FieldList.ALL.exclude(
                                        TEST_TABLE.INT_COL,
                                        TEST_TABLE.TINYINT_COL,
                                        TEST_TABLE.BIGINT_COL,
                                        TEST_TABLE.DOUBLE_COL,
                                        TEST_TABLE.BOOL_COL,
                                        TEST_TABLE.STRING_COL,
                                        TEST_TABLE.NULLABLE_COL),
                                FieldList.WITHOUT_ARRAY.exclude(TEST_TABLE.NULLABLE_COL),
                                FieldList.WITHOUT_NULLABLE.exclude(TEST_TABLE.BOOL_COL),
                                FieldList.WITHOUT_ARRAY_AND_NULLABLE)),

                // -- JooqSerializationFeature

                Arguments.of(
                        "JooqSerializationFeature.USE_DEFAULT_BEAN_SERIALIZER_FOR_TABLE_RECORD",
                        new ObjectMapper(),
                        new JooqModule().enableFeatures(JooqSerializationFeature.USE_DEFAULT_BEAN_SERIALIZER_FOR_TABLE_RECORD),
                        JsonAttributeDictionary.LOWER_CAMEL_CASE,
                        Arrays.asList(FieldList.ALL, FieldList.ALL, FieldList.ALL, FieldList.ALL))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource
    void testSerialize(String settingName, ObjectMapper mapper, JooqModule jooqModule, JsonAttributeDictionary dictionary, List<FieldList> fieldLists) throws Exception {
        mapper.registerModule(jooqModule)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        withTestTableRecords(records -> {
            String result = mapper.writeValueAsString(records);

            List l = DEFAULT_OBJECT_MAPPER.readValue(result, List.class);

            assertThat(l).hasSize(4);

            for (int i = 0; i < 4; i++) {
                AssertionContext context = new AssertionContext(i);

                context.assertThat(l).element(i).isInstanceOfSatisfying(Map.class, (Consumer<Map<String, ?>>) map -> {
                    FieldList fieldList = fieldLists.get(context.arrayIndex);
                    ExpectedValues expectedValues = ExpectedValues.get(context.arrayIndex);
                    verifyRecord(context, map, dictionary, fieldList, expectedValues);
                });
            }
        });
    }

    private void verifyRecord(AssertionContext parent, Map<String, ?> record, JsonAttributeDictionary attributeNames, FieldList attributeList, ExpectedValues expectedValues) {
        parent.assertThat(record).containsOnlyKeys(attributeNames.listAttributesAsArray(attributeList));

        for (Field<?> field : attributeList.fields()) {
            String attributeName = attributeNames.get(field);
            Object expectedValue = expectedValues.get(field);

            parent.assertThat(record)
                    .hasEntrySatisfying(attributeName, actualValue -> {
                        AssertionContext child = parent.withFieldAndAttributeName(field, attributeName);

                        if (expectedValue == null) {
                            child.assertThat(actualValue).isNull();

                        } else if (expectedValue instanceof Integer) {
                            child.assertThat(actualValue).isInstanceOfSatisfying(
                                    Integer.class,
                                    intValue -> child.assertThat(intValue).isEqualTo(expectedValue));

                        } else if (expectedValue instanceof Double) {
                            child.assertThat(actualValue).isInstanceOfSatisfying(
                                    Double.class,
                                    doubleValue -> child.assertThat(doubleValue).isCloseTo((Double) expectedValue, Percentage.withPercentage(1e-10)));

                        } else if (expectedValue instanceof String) {
                            child.assertThat(actualValue).isInstanceOfSatisfying(
                                    String.class,
                                    stringValue -> child.assertThat(stringValue).isEqualTo(expectedValue));

                        } else if (expectedValue instanceof Boolean) {
                            child.assertThat(actualValue).isInstanceOfSatisfying(
                                    Boolean.class,
                                    booleanValue -> child.assertThat(booleanValue).isEqualTo(expectedValue));

                        } else if (expectedValue instanceof List) {
                            child.assertThat(actualValue).isInstanceOfSatisfying(
                                    List.class,
                                    listValue -> child.assertThat(listValue).isEqualTo(expectedValue));
                        } else {
                            Assertions.fail(String.format("Type of expected value is invalid (Class = %s).", expectedValue.getClass().getSimpleName()));
                        }
                    });
        }
    }
}
