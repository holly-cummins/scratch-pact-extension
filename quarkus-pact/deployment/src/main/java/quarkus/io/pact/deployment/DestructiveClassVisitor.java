package quarkus.io.pact.deployment;

import io.quarkus.gizmo.Gizmo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

public class DestructiveClassVisitor extends ClassVisitor {

        /*
        This is the method we're altering
         private final File resolvePath() {
      URL resourcePath = PactFolderLoader.class.getClassLoader().getResource(this.path.getPath());
      return resourcePath != null ? new File(URLDecoder.decode(resourcePath.getPath(), "UTF-8")) : this.path;
   }
         */


    private final String className;

    public DestructiveClassVisitor(ClassVisitor visitor, String className) {
        super(Gizmo.ASM_API_VERSION, visitor);
        System.out.println("YO logging adder " + className);

        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {


        MethodVisitor parent = super.visitMethod(access, name, descriptor, signature, exceptions);
        LoggingVisitor insertInitCodeBeforeReturnMethodVisitor = new LoggingVisitor(parent, className, name);
        return insertInitCodeBeforeReturnMethodVisitor;

    }
}

//this is the custom method visitor
class LoggingVisitor extends MethodVisitor {
    private final String descriptor;
    MethodVisitor mv;

    public LoggingVisitor(MethodVisitor mv, String descriptor, String methodName) {
        super(Gizmo.ASM_API_VERSION, mv);
        this.mv = mv;
        this.descriptor = descriptor + "#" + methodName;
    }

    @Override
    public void visitInsn(int opcode) {
        String thingToLog = "hello" + descriptor;
        //whenever we find a RETURN, we insert the code, here only crazy example code
        switch (opcode) {
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.DRETURN:
            case RETURN: {

                mv.visitFieldInsn(GETSTATIC,
                        "java/lang/System",
                        "out",
                        "Ljava/io/PrintStream;");
                mv.visitLdcInsn(thingToLog);
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println",
                        "(Ljava/lang/String;)V");
                mv.visitInsn(RETURN);
//                mv.visitMaxs(2, 1);
                mv.visitEnd();
                break;
            }
            default: // do nothing
        }
        super.visitInsn(opcode);
    }
}

