/**
 * 
 */
package ${package}.web;

import java.util.EnumSet;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;

import ${package}.web.ui.WicketApplication;

/**
 * @author jabaraster
 */
@WebListener
public class JavaEEConfigurator implements ServletContextListener {

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
            return new WicketApplication(((ExWicketFilter) pFilter).injector);
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
