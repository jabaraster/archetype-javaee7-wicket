/**
 * 
 */
package ${package}.web.container;

import ${package}.Environment;
import ${package}.web.ui.component.PanelBase;
import ${package}.web.ui.page.HomePage;
import ${package}.web.ui.page.WebPageBase;
import jabara.wicket.MarkupIdForceOutputer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.EnumSet;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.h2.jdbcx.JdbcDataSource;

/**
 * @author jabaraster
 */
@WebListener
public class WebStarter implements ServletContextListener {

    @Inject
    BeanManager injector;

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(@SuppressWarnings("unused") final ServletContextEvent pEvent) {
        // nop
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent pEvent) {
        setUpWicket(pEvent.getServletContext());
    }

    /**
     * http://www.coppermine.jp/docs/programming/2013/12/glassfish-embedded-server.html
     * 
     * @param pArgs -
     * @throws Exception -
     */
    @SuppressWarnings("nls")
    public static void main(final String[] pArgs) throws Exception {
        final GlassFishProperties properties = new GlassFishProperties();
        properties.setPort("http-listener", 8081);
        properties.setPort("https-listener", 8082);
        final GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(properties);
        glassfish.start();

        final String connectionPoolName = "ConnectionPool";
        glassfish.getCommandRunner().run("create-jdbc-connection-pool" //
                , "--datasourceclassname=" + JdbcDataSource.class.getName() //
                , "--restype=" + DataSource.class.getName() //
                , "--property", "url=jdbc\\:h2\\:" + Environment.getH2DatabasePath() //
                , connectionPoolName //
                );
        glassfish.getCommandRunner().run("create-jdbc-resource" //
                , "--connectionpoolid", connectionPoolName //
                , "jdbc/" + Environment.getApplicationName() //
        );

        glassfish.getDeployer().deploy( //
                new File("src/main/webapp") //
                , "--name=" + Environment.getApplicationName() //
                , "--contextroot", "/");

        new BufferedReader(new InputStreamReader(System.in)).readLine();
        glassfish.stop();
        jabara.Debug.write("GlassFishサーバを停止.");
    }

    private static void setUpWicket(final ServletContext pServletContext) {
        final String PATH = "/ui/*"; //$NON-NLS-1$
        final Dynamic d = pServletContext.addFilter(ExWicketFilter.class.getName(), ExWicketFilter.class);
        d.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, PATH);
        d.setInitParameter(WicketFilter.APP_FACT_PARAM, WebApplicationFactoryImpl.class.getName());
        d.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, PATH);
    }

    /**
     * @author jabaraster
     */
    public static class ExWebApplication extends WebApplication {
        private final BeanManager injector;

        /**
         * @param pInjector -
         */
        public ExWebApplication(final BeanManager pInjector) {
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
                        inject(ExWebApplication.this.injector, pComponent);
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

    /**
     * @author jabaraster
     */
    public static class ExWicketFilter extends WicketFilter {
        @Inject
        BeanManager injector;
    }

    /**
     * @author jabaraster
     */
    public static class WebApplicationFactoryImpl implements IWebApplicationFactory {

        /**
         * @see org.apache.wicket.protocol.http.IWebApplicationFactory#createApplication(org.apache.wicket.protocol.http.WicketFilter)
         */
        @Override
        public WebApplication createApplication(final WicketFilter pFilter) {
            return new ExWebApplication(((ExWicketFilter) pFilter).injector);
        }

        /**
         * @see org.apache.wicket.protocol.http.IWebApplicationFactory#destroy(org.apache.wicket.protocol.http.WicketFilter)
         */
        @Override
        public void destroy(@SuppressWarnings("unused") final WicketFilter pFilter) {
            // nop
        }
    }
}
