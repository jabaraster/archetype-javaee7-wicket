/**
 * 
 */
package ${package}.web.ui.page;

import ${package}.entity.EEmployee;
import ${package}.entity.EEmployee_;
import ${package}.service.EmployeeService;
import jabara.general.Empty;
import jabara.jpa.entity.EntityBase_;
import jabara.wicket.ComponentCssHeaderItem;
import jabara.wicket.Models;
import jabara.wicket.ValidatorUtil;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author jabaraster
 */
@SuppressWarnings("synthetic-access")
public class HomePage extends WebPageBase {
    private static final long   serialVersionUID = -1017140620750071969L;

    @Inject
    EmployeeService             employeeService;

    private final Handler       handler          = new Handler();

    private Label               now;
    private AjaxLink<?>         refresher;

    private FeedbackPanel       feedback;

    private Form<?>             newEmployeeForm;
    private TextField<String>   newEmployeeName;
    private AjaxButton          submitter;

    private WebMarkupContainer  employeesContainer;
    private ListView<EEmployee> employees;

    /**
     * 
     */
    public HomePage() {
        this.add(getNow());
        this.add(getRefresher());

        this.add(getNewEmployeeForm());

        this.add(getEmployeesContainer());
    }

    /**
     * @see ${package}.web.ui.page.WebPageBase#renderHead(org.apache.wicket.markup.head.IHeaderResponse)
     */
    @Override
    public void renderHead(final IHeaderResponse pResponse) {
        super.renderHead(pResponse);
        pResponse.render(ComponentCssHeaderItem.forType(HomePage.class));
    }

    private ListView<EEmployee> getEmployees() {
        if (this.employees == null) {
            this.employees = new ListView<EEmployee>("employees", new EmployeesModel()) { //$NON-NLS-1$
                private static final long serialVersionUID = 3158546979877767324L;

                @Override
                protected void populateItem(final ListItem<EEmployee> pItem) {
                    pItem.setModel(new CompoundPropertyModel<>(pItem.getModelObject()));
                    pItem.add(new Label(EntityBase_.id.getName()));
                    pItem.add(new Label(EEmployee_.name.getName()));
                    pItem.add(new Label(EntityBase_.created.getName() //
                            , Models.of(DateFormat.getDateTimeInstance().format(pItem.getModelObject().getCreated()))));

                    final AjaxButton deleter = new IndicatingAjaxButton("deleter") { //$NON-NLS-1$
                        @Override
                        protected void onSubmit(final AjaxRequestTarget pTarget, @SuppressWarnings("unused") final Form<?> pForm) {
                            HomePage.this.handler.onDeleteSubmit(pTarget, pItem.getModelObject());
                        }
                    };
                    final Form<?> deleteForm = new Form<>("employeeDeleteForm"); //$NON-NLS-1$
                    deleteForm.add(deleter);

                    pItem.add(deleteForm);
                }
            };
        }
        return this.employees;
    }

    private WebMarkupContainer getEmployeesContainer() {
        if (this.employeesContainer == null) {
            this.employeesContainer = new WebMarkupContainer(id());
            this.employeesContainer.add(getEmployees());
        }
        return this.employeesContainer;
    }

    private FeedbackPanel getFeedback() {
        if (this.feedback == null) {
            this.feedback = new FeedbackPanel(id());
        }
        return this.feedback;
    }

    private Form<?> getNewEmployeeForm() {
        if (this.newEmployeeForm == null) {
            this.newEmployeeForm = new Form<>(id());
            this.newEmployeeForm.add(getFeedback());
            this.newEmployeeForm.add(getNewEmployeeName());
            this.newEmployeeForm.add(getSubmitter());
        }
        return this.newEmployeeForm;
    }

    private TextField<String> getNewEmployeeName() {
        if (this.newEmployeeName == null) {
            this.newEmployeeName = new TextField<String>(id(), Models.of(Empty.STRING));
            ValidatorUtil.setSimpleStringValidator(this.newEmployeeName, EEmployee.class, EEmployee_.name.getName());
        }
        return this.newEmployeeName;
    }

    private Label getNow() {
        if (this.now == null) {
            this.now = new Label(id(), new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    return DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                }
            });
        }
        return this.now;
    }

    private AjaxLink<?> getRefresher() {
        if (this.refresher == null) {
            this.refresher = new IndicatingAjaxLink<Object>(id()) {
                @Override
                public void onClick(final AjaxRequestTarget pTarget) {
                    HomePage.this.handler.onRefresh(pTarget);
                }
            };
        }
        return this.refresher;
    }

    private AjaxButton getSubmitter() {
        if (this.submitter == null) {
            this.submitter = new IndicatingAjaxButton(id()) {
                @Override
                protected void onError(final AjaxRequestTarget pTarget, @SuppressWarnings("unused") final Form<?> pForm) {
                    HomePage.this.handler.onError(pTarget);
                }

                @Override
                protected void onSubmit(final AjaxRequestTarget pTarget, @SuppressWarnings("unused") final Form<?> pForm) {
                    HomePage.this.handler.onNewEmployeeSubmit(pTarget);
                }
            };
        }
        return this.submitter;
    }

    private class EmployeesModel extends LoadableDetachableModel<List<EEmployee>> {
        private static final long     serialVersionUID = 8743714600299730392L;

        @SuppressWarnings("hiding")
        private final EmployeeService employeeService  = HomePage.this.employeeService;

        @Override
        protected List<EEmployee> load() {
            return this.employeeService.getAll();
        }
    }

    private class Handler implements Serializable {
        @SuppressWarnings("hiding")
        private final EmployeeService employeeService = HomePage.this.employeeService;

        void onDeleteSubmit(final AjaxRequestTarget pTarget, final EEmployee pDeleteTarget) {
            this.employeeService.delete(pDeleteTarget);
            pTarget.add(getEmployeesContainer());
        }

        void onError(final AjaxRequestTarget pTarget) {
            pTarget.add(getFeedback());
        }

        void onNewEmployeeSubmit(final AjaxRequestTarget pTarget) {
            this.employeeService.insert(getNewEmployeeName().getModelObject());

            getNewEmployeeName().setModelObject(Empty.STRING);
            pTarget.add(getNewEmployeeName(), getEmployeesContainer());
            pTarget.add(getFeedback());
        }

        void onRefresh(final AjaxRequestTarget pTarget) {
            pTarget.add(getNow());
        }
    }
}
