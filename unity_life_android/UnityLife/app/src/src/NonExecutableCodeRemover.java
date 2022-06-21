import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;

public class NonExecutableCodeRemover extends ByteCodeRewriter {
    public NonExecutableCodeRemover() {}

    public static void main(String[] args) throws IOException {
        // Invoke this script using //build/android/gyp/bytecode_rewriter.py
        if (args.length == 2) {
            NonExecutableCodeRemover rewriter = new NonExecutableCodeRemover();
            rewriter.rewrite(new File(args[0]), new File(args[1]));
        } else {
            System.err.println("Expected arguments: <input.jar> <output.jar>");
            System.exit(1);
        }
    }

    @Override
    protected boolean shouldRewriteClass(String classPath) {
        return true;
    }

    @Override
    protected ClassVisitor getClassVisitorForClass(String classPath, ClassVisitor delegate) {
        ClassVisitor invocationVisitor = new InvocationVisitor(delegate);
        return new MyTrackerLibraryClassVisitor(invocationVisitor, classPath);
    }

    private static class InvocationVisitor extends ClassVisitor {
        private InvocationVisitor(ClassVisitor baseVisitor) {
            super(Opcodes.ASM7, baseVisitor);
        }
    }

    private static class MyTrackerLibraryClassVisitor extends ClassVisitor {
        String classPath;

        private MyTrackerLibraryClassVisitor(ClassVisitor baseVisitor, String classPath) {
            super(Opcodes.ASM7, baseVisitor);
            this.classPath = classPath;
        }

        @Override
        public MethodVisitor visitMethod(
                int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor baseVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            MethodVisitor replacedNonExecutableInstructions = new ReplaceInsnInGotoAdapter(baseVisitor, classPath, descriptor);
            return new ReplaceInsnInGotoAdapter(replacedNonExecutableInstructions, classPath, descriptor);
        }

        public static class ReplaceInsnInGotoAdapter extends MethodVisitor {
            String classPath;
            String descriptor;

            public ReplaceInsnInGotoAdapter(MethodVisitor mv, String classPath, String descriptor) {
                super(Opcodes.ASM7, mv);
                this.classPath = classPath;
                this.descriptor = descriptor;
            }

            @Override
            public void visitInsn(int i) {
                if (i != Opcodes.NOP) {
                    mv.visitInsn(i);
                } else {
                    System.out.println("Nop: " + classPath + " " + descriptor);
                }
            }
        }
    }
}