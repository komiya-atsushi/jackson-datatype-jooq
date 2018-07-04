package me.k11i.jackson.module.jooq;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.jooq.impl.TableRecordImpl;

class TableRecordImplIgnoranceIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return m.getDeclaringClass() == TableRecordImpl.class
                || super.hasIgnoreMarker(m);
    }
}
