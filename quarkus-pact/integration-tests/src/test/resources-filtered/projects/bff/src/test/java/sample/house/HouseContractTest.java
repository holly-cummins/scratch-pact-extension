package sample.house;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.loader.PactFolderLoader;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;
import java.util.Arrays;

@Provider("House")
@PactFolder("pacts")
//@PactFolder("/Users/holly/Code/quarkus/pact/quarkus-pact/integration-tests/target/test-classes/projects/bff/src/test/resources/pacts")
@QuarkusTest // This starts the server on port 8081 for convenience in testing
public class HouseContractTest {

    @BeforeEach
    void before(PactVerificationContext context) {
        URL resourcePath = PactFolderLoader.class.getClassLoader().getResource("pacts");
        System.out.println("HOLLY this loader " + this.getClass().getClassLoader());
        System.out.println("HOLLY this would see " + this.getClass().getClassLoader().getResource("pacts"));
        System.out.println("HOLLY folder  " + PactFolder.class.getClassLoader());
        System.out.println("HOLLY folder would see " + PactFolder.class.getClassLoader().getResource("pacts"));

        System.out.println("HOLLY loader " + PactFolderLoader.class.getClassLoader());
        System.out.println("my annotations is " + Arrays.toString(this.getClass().getAnnotations()));
        System.out.println("my dec annotations is " + Arrays.toString(this.getClass().getDeclaredAnnotations()));

        System.out.println("HOLLY URL is " + resourcePath);


        context.setTarget(new HttpTestTarget("localhost", 8081));
    }


    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

}
