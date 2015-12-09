/**
 *
 */
package ${package}.web.container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
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
        final int HTTP_PORT = 8081;
        final int HTTPS_PORT = 8082;
        final int COMMAND_PORT = 10001;

        sendStopCommand(COMMAND_PORT); // 既に起動しているプロセスがあれば停止する.

        final GlassFishProperties properties = new GlassFishProperties();
        properties.setPort("http-listener", HTTP_PORT);
        properties.setPort("https-listener", HTTPS_PORT);
        final GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(properties);
        glassfish.start();

        final String connectionPoolName = "AppConnectionPool";

        // RDBとしてH2を使う場合.
        glassfish.getCommandRunner().run("create-jdbc-connection-pool" //
                , "--datasourceclassname=" + JdbcDataSource.class.getName() //
                , "--restype=" + DataSource.class.getName() //
                , "--property", "url=jdbc\\:h2\\:" + Environment.getH2DatabasePath() //
                , connectionPoolName //
        );

        // RDBとしてPostgreSQL9.4を使う場合.
        // この場合、pom.xmlの下記記述を有効にする必要がある.
        // <dependency>
        // <groupId>org.postgresql</groupId>
        // <artifactId>postgresql</artifactId>
        // <version>9.4-1203-jdbc41</version>
        // <scope>provided</scope>
        // </dependency>
        // glassfish.getCommandRunner().run("create-jdbc-connection-pool" //
        // , "--datasourceclassname=" + PGConnectionPoolDataSource.class.getName() //
        // , "--restype=" + ConnectionPoolDataSource.class.getName() //
        // , "--steadypoolsize=2" //
        // , "--maxpoolsize=10" //
        // , "--poolresize=2" //
        // , "--property", "serverName=192.168.50.13:portNumber=5432:databaseName=app:user=app:password=xxx" //
        // , connectionPoolName //
        // );

        glassfish.getCommandRunner().run("create-jdbc-resource" //
                , "--connectionpoolid", connectionPoolName //
                , "jdbc/App" //
        );

        glassfish.getDeployer().deploy( //
                new File("src/main/webapp") //
                , "--name=" + Environment.getApplicationName() //
                , "--contextroot", "/");

        startMonitoringStopCommand(COMMAND_PORT, glassfish);
    }

    private static void sendStopCommand(final int pPort) {
        try (final Socket accept = new Socket(InetAddress.getLocalHost(), pPort)) {
            new BufferedWriter(new OutputStreamWriter(accept.getOutputStream())).newLine();
        } catch (@SuppressWarnings("unused") final ConnectException e) {
            // nop
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                TimeUnit.SECONDS.sleep(1); // 別プロセスの停止を少し待つ
            } catch (@SuppressWarnings("unused") final InterruptedException e) {
                // nop
            }
        }
    }

    private static void startMonitoringStopCommand(final int pPort, final GlassFish pGlassfish) {
        final ExecutorService ex = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable pR) {
                final Thread ret = new Thread(pR);
                ret.setDaemon(true);
                return ret;
            }
        });
        ex.execute(new Runnable() {
            @Override
            public void run() {
                try (final ServerSocket server = new ServerSocket(pPort); //
                        final Socket accept = server.accept()) {
                    new BufferedReader(new InputStreamReader(accept.getInputStream())).readLine();
                    pGlassfish.stop();
                } catch (final IOException | GlassFishException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
