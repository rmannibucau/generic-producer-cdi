package com.github.rmannibucau.cdi.producer.generic.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

public class ProvidedTypesBean implements Bean<Object> {
    private final Bean delegate;
    private final Set<Type> types;

    public ProvidedTypesBean(final Bean<?> bean, final Set<Type> types) {
        this.delegate = bean;
        this.types = types;
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    //
    // full delegation
    //

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return delegate.getInjectionPoints();
    }

    @Override
    public Class<?> getBeanClass() {
        return delegate.getBeanClass();
    }

    @Override
    public boolean isNullable() {
        return delegate.isNullable();
    }

    @Override
    public Object create(final CreationalContext<Object> context) {
        return delegate.create(context);
    }

    @Override
    public void destroy(final Object instance, final CreationalContext<Object> context) {
        delegate.destroy(instance, context);
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return delegate.getQualifiers();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return delegate.getScope();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return delegate.getStereotypes();
    }

    @Override
    public boolean isAlternative() {
        return delegate.isAlternative();
    }
}
