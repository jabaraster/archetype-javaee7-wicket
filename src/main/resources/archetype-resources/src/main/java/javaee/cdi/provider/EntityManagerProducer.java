/**
 *
 */
package ${package}.javaee.cdi.provider;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ${package}.Environment;

/**
 * @author jabaraster
 */
@RequestScoped
class EntityManagerProducer {

    @PersistenceContext(unitName = Environment.APPLICATION_NAME)
    @Produces
    EntityManager entityManager;
}
