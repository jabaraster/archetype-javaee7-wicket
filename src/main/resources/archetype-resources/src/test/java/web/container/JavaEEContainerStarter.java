/**
 * 
 */
package ${package}.web.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.sql.DataSource;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.h2.jdbcx.JdbcDataSource;

import ${package}.Environment;

/**
 * @author jabaraster
 */
public final class JavaEEContainerStarter {

    private JavaEEContainerStarter() {
        // nop
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

}
