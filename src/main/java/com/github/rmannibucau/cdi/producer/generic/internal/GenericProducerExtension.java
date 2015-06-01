package com.github.rmannibucau.cdi.producer.generic.internal;

import com.github.rmannibucau.cdi.producer.generic.api.GenericQualifier;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessProducerMethod;

import static java.util.Collections.singletonList;

// based on Antoine's work:
// https://gist.github.com/antoinesd/3097661ca99fa61900fb
// and
// https://github.com/antoinesd/CDI-Sandbox/blob/CDI-1.0/converter/
//
// Note: it doesn't use ProcessBeanAttributes.setBeanAttributes()
// cause it is not yet portable if attributes are eagerly copied
// and all injection point events were not fired
public class GenericProducerExtension implements Extension {
    private static final Function<Class<?>, GenericProducer> NEW_SET = (k) -> new GenericProducer();

    private final Map<Class<?>, GenericProducer> typesByProducer = new HashMap<>();

    public void captureProducerTypes(@Observes final ProcessInjectionPoint<?, ?> pip) {
        findQualifier(pip.getInjectionPoint().getAnnotated().getAnnotations())
                .forEach(a -> typesByProducer.computeIfAbsent(a, NEW_SET).types
                                    .add(pip.getInjectionPoint().getType()));
    }

    public void captureConvertBean(@Observes final ProcessProducerMethod<?,?> ppm) {
        findQualifier(ppm.getAnnotated().getAnnotations())
                .findFirst()
                .ifPresent(a -> typesByProducer.computeIfAbsent(a, NEW_SET).bean = ppm.getBean());
    }

    public void addProducers(@Observes final AfterBeanDiscovery abd) {
        typesByProducer.values()
                .stream()
                .forEach(e -> abd.addBean(new ProvidedTypesBean(e.bean, e.types)));
        typesByProducer.clear();
    }

    private Stream<Class<? extends Annotation>> findQualifier(final Collection<Annotation> annotations) {
        return annotations
                .stream()
                .map(Annotation::annotationType)
                .filter(a -> a.isAnnotationPresent(GenericQualifier.class));
    }

    private static class GenericProducer {
        private final Set<Type> types = new HashSet<>(singletonList(Object.class));
        private Bean<?> bean;
    }
}
