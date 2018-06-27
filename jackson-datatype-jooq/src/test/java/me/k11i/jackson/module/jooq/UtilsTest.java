package me.k11i.jackson.module.jooq;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {
    static List<Arguments> testToCamelCase() {
        return Arrays.asList(
                Arguments.of("single_underscore", "singleUnderscore"),
                Arguments.of("double__underscores", "double_Underscores"),
                Arguments.of("triple___underscores", "triple__Underscores"),
                Arguments.of("_leading_underscore", "_LeadingUnderscore"),
                Arguments.of("__leading_double_underscores", "__LeadingDoubleUnderscores"),
                Arguments.of("trailing_underscore_", "trailingUnderscore_"),
                Arguments.of("trailing_double_underscores__", "trailingDoubleUnderscores__"),
                Arguments.of("camelCase", "camelcase"),
                Arguments.of("UPCASE_STRING", "upcaseString"));
    }

    @ParameterizedTest
    @MethodSource
    void testToCamelCase(String input, String expected) {
        assertThat(Utils.toCamelCase(input)).isEqualTo(expected);
    }
}
