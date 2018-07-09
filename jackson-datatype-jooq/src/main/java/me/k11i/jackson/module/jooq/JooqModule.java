package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class JooqModule extends Module {
    private final Set<JooqSerializationFeature> enabledFeatures = EnumSet.noneOf(JooqSerializationFeature.class);

    public JooqModule enableFeatures(JooqSerializationFeature... features) {
        enabledFeatures.addAll(Arrays.asList(features));
        return this;
    }

    @Override
    public String getModuleName() {
        return "JooqModule";
    }

    @Override
    public Version version() {
        return ModuleVersion.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new JooqSerializers(enabledFeatures));

        if (enabledFeatures.contains(JooqSerializationFeature.USE_DEFAULT_BEAN_SERIALIZER_FOR_TABLE_RECORD)) {
            context.insertAnnotationIntrospector(new TableRecordImplIgnoranceIntrospector());
        }
    }
}
