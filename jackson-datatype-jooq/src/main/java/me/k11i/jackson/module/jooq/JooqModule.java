package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class JooqModule extends Module {
    @Override
    public String getModuleName() {
        return "JooqModule";
    }

    @Override
    public Version version() {
        // TODO version
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new JooqSerializers());
    }
}
