package com.alibaba.jvm.sandbox.core.enhance.weaver.asm;

import com.alibaba.jvm.sandbox.spy.Spy;
import org.objectweb.asm.commons.Method;

import static com.alibaba.jvm.sandbox.core.enhance.weaver.asm.AsmMethods.InnerHelper.getAsmMethod;
import static com.alibaba.jvm.sandbox.core.util.SandboxReflectUtils.unCaughtGetClassDeclaredJavaMethod;

/**
 * 常用的ASM method 集合
 * 省得我到处声明
 * Created by luanjia@taobao.com on 16/5/21.
 */
public interface AsmMethods {

    class InnerHelper {
        private InnerHelper() {
        }

        static Method getAsmMethod(final Class<?> clazz,
                                   final String methodName,
                                   final Class<?>... parameterClassArray) {
            return Method.getMethod(unCaughtGetClassDeclaredJavaMethod(clazz, methodName, parameterClassArray));
        }
    }

    Method ASM_METHOD_Spy$spyMethodOnBefore = getAsmMethod(
            Spy.class,
            "spyMethodOnBefore",
            Object[].class, String.class, int.class, int.class, String.class, String.class, String.class, Object.class
    );

    Method ASM_METHOD_Spy$spyMethodOnReturn = getAsmMethod(
            Spy.class,
            "spyMethodOnReturn",
            Object.class, String.class, int.class
    );

    Method ASM_METHOD_Spy$spyMethodOnThrows = getAsmMethod(
            Spy.class,
            "spyMethodOnThrows",
            Throwable.class, String.class, int.class
    );

    Method ASM_METHOD_Class$getName = getAsmMethod(
            Class.class,
            "getName"
    );

    Method ASM_METHOD_Object$getClass = getAsmMethod(
            Object.class,
            "getClass"
    );

//    /**
//     * asm method of {@link Class#getClassLoader()}
//     */
//    Method ASM_METHOD_Class$getClassLoader = getAsmMethod(
//            Class.class,
//            "getClassLoader"
//    );


}
