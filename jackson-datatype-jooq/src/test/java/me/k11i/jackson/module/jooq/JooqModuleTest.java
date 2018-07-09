package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.core.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JooqModuleTest {
    @Test
    void testVersion() {
        Version version = new JooqModule().version();

        assertThat(version.getGroupId()).isEqualTo("me.k11i");
        assertThat(version.getArtifactId()).isEqualTo("jackson-datatype-jooq");
        assertThat(version.isUnknownVersion()).isFalse();
        assertThat(version.getMajorVersion()).isGreaterThanOrEqualTo(1);
        assertThat(version.getMinorVersion()).isGreaterThanOrEqualTo(0);
        assertThat(version.getPatchLevel()).isGreaterThanOrEqualTo(0);
    }
}
