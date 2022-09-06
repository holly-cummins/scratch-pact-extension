package quarkus.io.pact.deployment;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.loader.PactFolderLoader;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.RemovedResourceBuildItem;
import io.quarkus.maven.dependency.ArtifactKey;
import org.jboss.jandex.DotName;

import java.util.Collections;

class QuarkusPactProcessor {

    private static final String FEATURE = "quarkus-pact";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void addDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        // TODO is this needed?
        indexDependency.produce(new IndexDependencyBuildItem("au.com.dius.pact.provider", "junit5"));
    }

    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSource() {
        System.out.println("HOLLY making the transformer");

        // TODO is dotname needed?
        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(DotName.createSimple(PactFolderLoader.class.getName()).toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        PactFolderLoader.class.getName(),
                        "resolvePath"))
                .build();

    }

    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSource2() {
        System.out.println("HOLLY making the transformer2");

        // TODO is dotname needed?
        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(DotName.createSimple(PactFolder.class.getName()).toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        PactFolder.class.getName(),
                        "resolvePath"))
                .build();

    }

    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSource3() {
        System.out.println("HOLLY making the transformer2");

        // TODO is dotname needed?
        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(DotName.createSimple(PactVerificationContext.class.getName()).toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        PactVerificationContext.class.getName(),
                        "resolvePath"))
                .build();

    }

    @BuildStep
    RemovedResourceBuildItem overrideSubstitutions() {
        return new RemovedResourceBuildItem(ArtifactKey.fromString("au.com.dius.pact.provider:junit5"),
                Collections.singleton("au/com/dius/pact/provider/junit5/PactVerificationContext.class"));
    }

    @BuildStep
    RemovedResourceBuildItem overrideSubstitutions2() {
        return new RemovedResourceBuildItem(ArtifactKey.fromString("au.com.dius.pact:provider"),
                Collections.singleton("au/com/dius/pact/provider/junitsupport/Provider.class"));
    }
}