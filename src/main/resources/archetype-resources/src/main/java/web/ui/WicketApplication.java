/**
 * 
 */
package ${package}.web.ui;

import jabara.wicket.MarkupIdForceOutputer;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.protocol.http.WebApplication;

import ${package}.web.ui.component.PanelBase;
import ${package}.web.ui.page.HomePage;
import ${package}.web.ui.page.WebPageBase;

/**
 * @author jabaraster
 */
public class WicketApplication extends WebApplication {
    private final BeanManager injector;

    /**
     * @param pInjector -
     */
    public WicketApplication(final BeanManager pInjector) {
        this.injector = pInjector;
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.protocol.http.WebApplication#init()
     */
    @Override
    protected void init() {
        super.init();

        this.getComponentInstantiationListeners().add(new IComponentInstantiationListener() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void onInstantiation(final Component pComponent) {
                if (pComponent instanceof PanelBase || pComponent instanceof WebPageBase) {
                    inject(WicketApplication.this.injector, pComponent);
                }
            }
        });
        this.getComponentInstantiationListeners().add(new MarkupIdForceOutputer());
    }

    /**
     * http://d.hatena.ne.jp/jabaraster/20120211/1328932645
     * 
     * @param pBeanManager -
     * @param pComponent -
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void inject(final BeanManager pBeanManager, final Component pComponent) {
        final Class pType = pComponent.getClass();
        final Bean<Object> bean = (Bean<Object>) pBeanManager.resolve(pBeanManager.getBeans(pType));
        final CreationalContext<Object> cc = pBeanManager.createCreationalContext(bean);
        final AnnotatedType<Object> at = pBeanManager.createAnnotatedType(pType);
        final InjectionTarget<Object> it = pBeanManager.createInjectionTarget(at);
        it.inject(pComponent, cc);
    }
}
