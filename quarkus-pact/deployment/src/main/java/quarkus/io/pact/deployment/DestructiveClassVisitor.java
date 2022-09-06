package quarkus.io.pact.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class DestructiveClassVisitor extends ClassVisitor {

        /*
        This is the method we're altering
         private final File resolvePath() {
      URL resourcePath = PactFolderLoader.class.getClassLoader().getResource(this.path.getPath());
      return resourcePath != null ? new File(URLDecoder.decode(resourcePath.getPath(), "UTF-8")) : this.path;
   }
         */


    private final String className;
    private final String methodsWithBridges;

    public DestructiveClassVisitor(ClassVisitor visitor, String className, String methodsWithBridges) {
        super(Gizmo.ASM_API_VERSION, visitor);

        this.className = className;
        this.methodsWithBridges = methodsWithBridges;
        System.out.println("HOLLY done constructed");
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("HOLLY visiting " + name);


        //  if (methodsWithBridges.equals(name)) {
        System.out.println("HOLLY REBUILDING!");
        // This should remove the method, right?
        return null;
        //   }

        //  return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
