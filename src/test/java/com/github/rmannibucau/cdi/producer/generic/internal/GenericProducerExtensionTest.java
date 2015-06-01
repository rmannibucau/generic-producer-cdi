package com.github.rmannibucau.cdi.producer.generic.internal;

import com.github.rmannibucau.cdi.producer.generic.api.GenericQualifier;
import com.github.rmannibucau.cdi.producer.generic.internal.rule.OWBRule;
import org.junit.Rule;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;

@Dependent // just to get it scanned and process during container boot
public class GenericProducerExtensionTest {
    @Rule
    public final OWBRule rule = new OWBRule(this);

    @Inject
    @MyProducerQualifier1
    private String string;

    @Inject
    @MyProducerQualifier1
    private Integer integer;

    @Inject
    @MyProducerQualifier2
    private String string2;

    @Inject
    @MyProducerQualifier2
    private Integer integer2;

    @Test
    public void checkInjection() {
        assertEquals(100, integer.intValue());
        assertEquals(200, integer2.intValue());
        assertEquals("generic", string);
        assertEquals("generic2", string2);
    }

    @ApplicationScoped
    public static class MyProducer1 {
        @Produces
        @MyProducerQualifier1
        public Object create(final InjectionPoint ip) {
            if (ip.getType() == Integer.class) {
                return 100;
            } else if (ip.getType() == String.class) {
                return "generic";
            }
            throw new IllegalArgumentException("unknown ip: " + ip);
        }
    }

    @GenericQualifier
    @Qualifier
    @Target({ METHOD, FIELD })
    @Retention(RUNTIME)
    public @interface MyProducerQualifier1 {}

    @ApplicationScoped
    public static class MyProducer2 {
        @Produces
        @MyProducerQualifier2
        public Object create(final InjectionPoint ip) {
            if (ip.getType() == Integer.class) {
                return 200;
            } else if (ip.getType() == String.class) {
                return "generic2";
            }
            throw new IllegalArgumentException("unknown ip: " + ip);
        }
    }

    @GenericQualifier
    @Qualifier
    @Target({ METHOD, FIELD })
    @Retention(RUNTIME)
    public @interface MyProducerQualifier2 {}
}
