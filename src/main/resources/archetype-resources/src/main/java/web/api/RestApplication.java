/**
 * 
 */
package info.jabara.sandbox.web.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author jabaraster
 */
@ApplicationPath("/api")
public class RestApplication extends Application {

    /**
     * @see javax.ws.rs.core.Application#getClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.<Class<?>> asList( //
                ));
    }
}
