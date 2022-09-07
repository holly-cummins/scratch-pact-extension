package quarkus.io.pact.deployment;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.loader.PactFolderLoader;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.RemovedResourceBuildItem;
import io.quarkus.maven.dependency.ArtifactKey;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;

import java.util.Collections;
import java.util.List;

class QuarkusPactProcessor {

    private static final String FEATURE = "quarkus-pact";
    private static final DotName PACT_FOLDER = DotName.createSimple(PactFolder.class.getName());
    private static final DotName JAX_RS_GET = DotName.createSimple("javax.ws.rs.GET");
    private static final DotName SPAN_KIND = DotName.createSimple(PactFolder.class.getName());

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
    AnnotationsTransformerBuildItem transform2() {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            public boolean appliesTo(org.jboss.jandex.AnnotationTarget.Kind kind) {
                return kind == AnnotationTarget.Kind.CLASS;
            }

            public void transform(TransformationContext context) {

                AnnotationTarget target = context.getTarget();
                final AnnotationInstance annotationInstance = target.asClass().classAnnotation(PACT_FOLDER);
                if (annotationInstance != null) {
                    System.out.println("HOLLY Overriding annotation value of " + annotationInstance.value("value"));
                    AnnotationValue value = AnnotationValue.createStringValue("value", "frogs");
                    AnnotationInstance annotation = AnnotationInstance.create(
                            PACT_FOLDER,
                            target,
                            List.of(value));
                    context.transform().add(annotation).done();
                }

//                if (annotationInstance != null) {
//
//                    context.transform().remove(annotationInstance2 -> {
//                          return annotationInstance == annotationInstance2;
//                    }).done();
////                       }
            }
        });
    }


    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSource() {


        // TODO is dotname needed?
        String name = PactFolderLoader.class.getName();
        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(DotName.createSimple(name).toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        name))
                .build();

    }


    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSource2() {

        String name = PactFolder.class.getName();
        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(DotName.createSimple(name).toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        name))
                .build();

    }

    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSourceTest() {
        DotName simple = DotName.createSimple("sample.house.HouseContractTest");
        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(simple.toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        simple.toString()))
                .build();

    }

    @BuildStep
    BytecodeTransformerBuildItem reworkClassLoadingOfPactSource3() {

        return new BytecodeTransformerBuildItem.Builder()
                .setClassToTransform(DotName.createSimple(PactVerificationContext.class.getName()).toString())
                .setVisitorFunction((ignored, visitor) -> new DestructiveClassVisitor(visitor,
                        PactVerificationContext.class.getName()))
                .build();

    }

    @BuildStep
    RemovedResourceBuildItem removeClass() {
        return new RemovedResourceBuildItem(ArtifactKey.fromString("au.com.dius.pact.provider:junit5"),
                Collections.singleton("au/com/dius/pact/provider/junit5/PactVerificationContext.class"));
    }

    @BuildStep
    RemovedResourceBuildItem overrideSubstitutions2() {
        return new RemovedResourceBuildItem(ArtifactKey.fromString("au.com.dius.pact:provider"),
                Collections.singleton("au/com/dius/pact/provider/junitsupport/Provider.class"));
    }
}