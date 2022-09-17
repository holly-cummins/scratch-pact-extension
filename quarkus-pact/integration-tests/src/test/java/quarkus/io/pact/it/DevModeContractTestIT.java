package quarkus.io.pact.it;

import io.quarkus.maven.it.RunAndCheckMojoTestBase;
import io.quarkus.maven.it.continuoustesting.ContinuousTestingMavenTestUtils;
import io.quarkus.test.devmode.util.DevModeTestUtils;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Because Pact uses Kotlin under the covers, we see different behaviour
 * in continuous testing and normal maven modes. This test exercises the continuous
 * testing with Pact.
 * <p>
 * NOTE to anyone diagnosing failures in this test, to run a single method use:
 * <p>
 * mvn install -Dit.test=DevMojoIT#methodName
 */
@DisabledIfSystemProperty(named = "quarkus.test.native", matches = "true")
public class DevModeContractTestIT extends RunAndCheckMojoTestBase {


    protected void runAndCheck(String... options) throws MavenInvocationException, FileNotFoundException {
        // To avoid clashes with other tests, we use a non-default port
        runAndCheck(true, "-Dquarkus.http.test-port=8083");
    }

    protected void runAndCheck(boolean performCompile, String... options)
            throws MavenInvocationException, FileNotFoundException {
        run(performCompile, options);

        String resp = DevModeTestUtils.getHttpResponse();

        assertThat(resp).containsIgnoringCase("ready").containsIgnoringCase("application")
                .containsIgnoringCase("1.0-SNAPSHOT");

        String json = DevModeTestUtils.getHttpResponse("/resident");
        assertThat(json).containsIgnoringCase("room");
    }

    @Test
    public void testThatTheTestsPassed() throws MavenInvocationException, FileNotFoundException {
        //we also check continuous testing
        testDir = initProject("projects/bff", "projects/multimodule-with-deps");
        runAndCheck();

        // test that we don't get multiple instances of a resource when loading from the ClassLoader
        await()
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .atMost(5, TimeUnit.SECONDS)
                .until(() -> DevModeTestUtils.getHttpResponse("/resident").startsWith("{"));

        ContinuousTestingMavenTestUtils testingTestUtils = new ContinuousTestingMavenTestUtils();
        ContinuousTestingMavenTestUtils.TestStatus results = testingTestUtils.waitForNextCompletion();
//check that the tests in both modules run
        Assertions.assertEquals(4, results.getTestsPassed());
    }

}
