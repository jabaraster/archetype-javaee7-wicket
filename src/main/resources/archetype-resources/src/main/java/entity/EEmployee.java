/**
 * 
 */
package ${package}.entity;

import jabara.jpa.entity.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author jabaraster
 */
@Entity
public class EEmployee extends EntityBase<EEmployee> {

    /**
     * 
     */
    public static final int MAX_CHAR_COUNT_NAME = 50;

    @Column(length = MAX_CHAR_COUNT_NAME * 3, unique = true)
    @NotNull
    @Size(min = 1, max = MAX_CHAR_COUNT_NAME)
    String                  name;

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param pName the name to set
     */
    public void setName(final String pName) {
        this.name = pName;
    }
}
