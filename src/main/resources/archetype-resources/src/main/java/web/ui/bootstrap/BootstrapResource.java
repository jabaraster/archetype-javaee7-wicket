/**
 * 
 */
package ${package}.web.ui.bootstrap;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

/**
 * Twitter Bootstrap関連のリソースを定義します.
 * 
 * @author jabaraster
 */
public final class BootstrapResource {

    /**
     * 
     */
    public static final ResourceReference    CSS_RESOURCE_REF    = new CssResourceReference(BootstrapResource.class, "css/bootstrap.min.css");     //$NON-NLS-1$

    /**
     * 
     */
    public static final CssHeaderItem        CSS                 = CssHeaderItem.forReference(CSS_RESOURCE_REF);

    /**
     * このリソースは{@link JQueryResourceReference}に依存します.
     */
    public static final ResourceReference    SCRIPT_RESOURCE_REF = new JavaScriptResourceReference(BootstrapResource.class, "js/bootstrap.min.js"); //$NON-NLS-1$

    /**
     */
    public static final JavaScriptHeaderItem SCRIPT              = JavaScriptHeaderItem.forReference(SCRIPT_RESOURCE_REF);

    private BootstrapResource() {
        // nop
    }
}
