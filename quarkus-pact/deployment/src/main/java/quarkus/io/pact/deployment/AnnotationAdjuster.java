package quarkus.io.pact.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


public class AnnotationAdjuster extends ClassVisitor {


    private final String className;

    public AnnotationAdjuster(ClassVisitor visitor, String className) {
        super(Gizmo.ASM_API_VERSION, visitor);
        System.out.println("YO annotation adjuster " + className);

        this.className = className;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.println("visitAnnotation: desc=" + desc + " visible=" + visible);
        AnnotationVisitor sup = super.visitAnnotation(desc, visible);
        return new Ug(sup, "hi", "fred");
    }

    public String getClassName() {
        return className;
    }


    @Override
    public void visitAttribute(Attribute attr) {
        System.out.println("visitAttribute: attr=" + attr);
        super.visitAttribute(attr);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        System.out.println("HOLLY annotater visiting methd " + name);
        return super.visitMethod(access, name, descriptor, signature, exceptions);

    }

}

//this is the custom method visitor
class Ug extends AnnotationVisitor {
    private final String descriptor;
    AnnotationVisitor mv;

    public Ug(AnnotationVisitor mv, String descriptor, String methodName) {
        super(Gizmo.ASM_API_VERSION, mv);
        this.mv = mv;
        this.descriptor = descriptor + "#" + methodName;
    }

    public void visit(final String name, final Object value) {
        System.out.println("now looking at " + name + " -> " + value);
        if (av != null) {
            av.visit(name, value + "frog");
        }
    }


}

