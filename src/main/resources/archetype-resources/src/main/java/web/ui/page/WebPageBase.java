package ${package}.web.ui.page;

import ${package}.web.ui.bootstrap.BootstrapScope;
import jabara.general.ArgUtil;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

/**
 * @author jabaraster
 */
public abstract class WebPageBase extends WebPage {

    private static final CssResourceReference        REF_BOOTSTRAP_CSS = new CssResourceReference(BootstrapScope.class, "css/bootstrap.min.css");     //$NON-NLS-1$
    private static final JavaScriptResourceReference REF_BOOTSTRAP_JS  = new JavaScriptResourceReference(BootstrapScope.class, "js/bootstrap.min.js"); //$NON-NLS-1$

    /**
     * 
     */
    protected WebPageBase() {
        this(new PageParameters());
    }

    /**
     * @param pParameters -
     */
    protected WebPageBase(final PageParameters pParameters) {
        super(pParameters);
    }

    /**
     * @see org.apache.wicket.Component#renderHead(org.apache.wicket.markup.head.IHeaderResponse)
     */
    @Override
    public void renderHead(final IHeaderResponse pResponse) {
        super.renderHead(pResponse);
        renderCommonHead(pResponse);
    }

    /**
     * @param pResponse 全ての画面に共通して必要なheadタグ内容を出力します.
     */
    public static void renderCommonHead(final IHeaderResponse pResponse) {
        ArgUtil.checkNull(pResponse, "pResponse"); //$NON-NLS-1$

        pResponse.render(CssHeaderItem.forReference(REF_BOOTSTRAP_CSS));
        pResponse.render(JavaScriptHeaderItem.forReference(JQueryResourceReference.get()));
        pResponse.render(JavaScriptHeaderItem.forReference(REF_BOOTSTRAP_JS));
    }

    /**
     * WicketのComponentのidを類推します. <br>
     * このメソッドはWicketのComponentを生成するgetterの中から呼び出して下さい. <br>
     * 
     * @return このメソッドを呼び出したgetterからgetを除き、先頭文字を小文字にした文字列.
     */
    protected static String id() {
        final String getterName = new Throwable().getStackTrace()[1].getMethodName();
        if (getterName.length() < 4) {
            throw new IllegalStateException();
        }
        if (!getterName.startsWith("get")) { //$NON-NLS-1$
            throw new IllegalStateException();
        }
        final String s = getterName.substring("get".length()); //$NON-NLS-1$
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
