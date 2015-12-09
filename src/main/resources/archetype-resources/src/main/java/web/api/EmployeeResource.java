/**
 *
 */
package ${package}.web.api;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ${package}.entity.EEmployee;
import ${package}.service.EmployeeService;

/**
 * @author jabaraster
 */
@Path("employee")
@Dependent
public class EmployeeResource {

    @Inject
    EmployeeService employeeService;

    /**
     * @return -
     */
    @Path("/")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<EEmployee> getAll() {
        return this.employeeService.getAll();
    }
}
