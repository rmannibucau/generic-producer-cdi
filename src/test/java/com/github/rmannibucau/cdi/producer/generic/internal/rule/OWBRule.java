package com.github.rmannibucau.cdi.producer.generic.internal.rule;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Collection;
import java.util.LinkedList;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

public class OWBRule implements TestRule {
    private final Object[] toInject;

    public OWBRule(final Object... toInject) {
        this.toInject = toInject == null ? new Object[0] : toInject;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final WebBeansContext wbc = WebBeansContext.currentInstance();
                final ContainerLifecycle service = wbc.getService(ContainerLifecycle.class);
                service.startApplication(null);

                final Collection<CreationalContext<?>> creationalContexts = doInjects(wbc);
                try {
                    base.evaluate();
                } finally {
                    doRealeases(creationalContexts);
                    service.stopApplication(null);
                }
            }
        };
    }

    private Collection<CreationalContext<?>> doInjects(final WebBeansContext wbc) {
        final Collection<CreationalContext<?>> creationalContexts = new LinkedList<>();
        final BeanManager bm = wbc.getBeanManagerImpl();
        for (final Object instance : toInject) {
            try {
                final CreationalContext<?> creationalContext = bm.createCreationalContext(null);
                creationalContexts.add(creationalContext);
                final AnnotatedType annotatedType = bm.createAnnotatedType(instance.getClass());
                bm.createInjectionTarget(annotatedType).inject(instance, creationalContext);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return creationalContexts;
    }

    private void doRealeases(final Collection<CreationalContext<?>> creationalContexts) {
        for (final CreationalContext<?> cc : creationalContexts) {
            cc.release();
        }
    }
}
