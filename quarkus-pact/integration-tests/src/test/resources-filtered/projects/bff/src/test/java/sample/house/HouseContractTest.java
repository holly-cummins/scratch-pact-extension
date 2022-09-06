package sample.house;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("House")
//@PactFolder("pacts")
@PactFolder("/Users/holly/Code/quarkus/pact/quarkus-pact/integration-tests/target/test-classes/projects/bff/src/test/resources/pacts")
@QuarkusTest // This starts the server on port 8081 for convenience in testing
public class HouseContractTest {

    @BeforeEach
    void before(PactVerificationContext context) {
        System.out.println("holly made a context with " + context.getClass().getProtectionDomain());
        context.setTarget(new HttpTestTarget("localhost", 8081));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {

        System.out.println("holly extendaroo with " + context.getClass().getProtectionDomain());

        context.verifyInteraction();
    }

}
