/**
 * 
 */
package ${package}.service.internal;

import ${package}.entity.EEmployee;
import ${package}.service.EmployeeService;
import jabara.general.ArgUtil;
import jabara.jpa.JpaDaoBase;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

/**
 * @author jabaraster
 */
public class EmployeeServiceImpl extends JpaDaoBase implements EmployeeService {

    /**
     * @param pEntityManagerFactory -
     */
    @Inject
    public EmployeeServiceImpl(final PersistenceUnitProvider pEntityManagerFactory) {
        super(pEntityManagerFactory.get());
    }

    /**
     * @see ${package}.service.EmployeeService#delete(${package}.entity.EEmployee)
     */
    @Transactional
    @Override
    public void delete(final EEmployee pDeleteTarget) {
        ArgUtil.checkNull(pDeleteTarget, "pDeleteTarget"); //$NON-NLS-1$
        final EntityManager em = getEntityManager();
        em.remove(em.merge(pDeleteTarget));
    }

    /**
     * @see ${package}.service.EmployeeService#getAll()
     */
    @Override
    public List<EEmployee> getAll() {
        final EntityManager em = getEntityManager();
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<EEmployee> query = builder.createQuery(EEmployee.class);
        query.from(EEmployee.class);
        return em.createQuery(query).getResultList();
    }

    /**
     * @see ${package}.service.EmployeeService#insert(java.lang.String)
     */
    @Transactional
    @Override
    public EEmployee insert(final String pName) {
        final EEmployee newEmp = new EEmployee();
        newEmp.setName(pName);
        this.getEntityManager().persist(newEmp);
        return newEmp;
    }

}
