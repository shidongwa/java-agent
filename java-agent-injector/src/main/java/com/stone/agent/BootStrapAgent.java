package com.stone.agent;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class BootStrapAgent {

    public static void premain(String ops, Instrumentation inst) {
        System.out.println("premain start");
        inst.addTransformer(new MyTransformer());
        System.out.println("premain end");
    }

    static class MyTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            //System.out.println("transform start");
           try {
               if ("java/util/Scanner".equals(className)) {
                   System.out.println("transform scanner start");
                   ClassReader cr = new ClassReader(classfileBuffer);
                   ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
                   MyClassVisitor cv = new MyClassVisitor(Opcodes.ASM9, cw);
                   cr.accept(cv, ClassReader.EXPAND_FRAMES);
                   System.out.println("transform scanner end");
                   return cw.toByteArray();
               }
               return null;
           } catch (Throwable t) {
               t.printStackTrace();
               return null;
           }
        }
    }

    static class MyClassVisitor extends ClassVisitor implements Opcodes {
        protected MyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                         String[] exceptions) {
            final MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            if ("nextLine".equals(name)) {
                System.out.println("Modify Scanner.nextLine");
                return new MethodVisitor(api, methodVisitor) {
                    @Override
                    public void visitCode() {
                        methodVisitor.visitMethodInsn(INVOKESTATIC, "com/stone/service/Injector", "insert", "()V", false);
                        super.visitCode();
                    }
                };
            }
            return methodVisitor;
        }
    }

}
