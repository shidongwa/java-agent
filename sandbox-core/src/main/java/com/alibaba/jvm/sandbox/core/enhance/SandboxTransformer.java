package com.alibaba.jvm.sandbox.core.enhance;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.util.matcher.Matcher;
import com.alibaba.jvm.sandbox.core.util.matcher.MatchingResult;
import com.alibaba.jvm.sandbox.core.util.matcher.UnsupportedMatcher;
import com.alibaba.jvm.sandbox.core.util.matcher.structure.ClassStructure;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.alibaba.jvm.sandbox.core.util.matcher.structure.ClassStructureFactory.createClassStructure;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


public class SandboxTransformer implements ClassFileTransformer {
    private static final String NAMESPACE = "default";
    private static final List<String> CLASSLOADER_4_SKIP =
            Arrays.asList("com.alibaba.jvm.sandbox.agent.SandboxClassLoader",
                    "com.alibaba.jvm.sandbox.core.classloader.ModuleClassLoader");
    private String adviceId;
    protected List<Class<?>> matchedClasses;
    protected Instrumentation instrumentation;
    private final Matcher matcher;

    public SandboxTransformer(Instrumentation instrumentation, String adviceId, List<Class<?>> matchingClasses,
                              final Matcher matcher) {
        this.instrumentation = instrumentation;
        this.matchedClasses = matchingClasses;
        this.adviceId = adviceId;
        this.matcher = matcher;
    }

    @Override
    public byte[] transform(ClassLoader targetClassLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            // 这里需要进行过滤
            // 在transform的过程中,有可能还会加载新的类,符合的className才能被处理，
            if (!matchedClasses.contains(classBeingRedefined)) {
                return null;
            }

            // skip classes loaded from com.alibaba.jvm.sandbox.agent.SandboxClassLoader，
            // com.taobao.arthas.agent.ArthasClassloader
            if(targetClassLoader != null && CLASSLOADER_4_SKIP.contains(targetClassLoader.getClass().getCanonicalName())) {
                return null;
            }

            final ClassStructure classStructure = getClassStructure(targetClassLoader, classBeingRedefined, classfileBuffer);
            final MatchingResult matchingResult = new UnsupportedMatcher(targetClassLoader, true).and(matcher).matching(classStructure);
            final Set<String> behaviorSignCodes = matchingResult.getBehaviorSignCodes();

            // 如果一个行为都没匹配上也不用继续了
            if (!matchingResult.isMatched()) {
//                logger.debug("transform ignore {}, no behaviors matched in loader={}", internalClassName, loader);
                return null;
            }

            EventListener advice = EventListener.Factory.getEventListener(adviceId);
            if (advice == null) {
                return null;
            }

            // classfileBuffer是原始字节码
            final byte[] enhanceClassByteArray = new EventEnhancer().toByteCodeArray(
                    targetClassLoader,
                    classfileBuffer,
                    behaviorSignCodes,
                    NAMESPACE,
                    Integer.valueOf(adviceId),
                    advice.support());

            return enhanceClassByteArray;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    // 获取当前类结构
    private ClassStructure getClassStructure(final ClassLoader loader,
                                             final Class<?> classBeingRedefined,
                                             final byte[] srcByteCodeArray) {
        return null == classBeingRedefined
                ? createClassStructure(srcByteCodeArray, loader)
                : createClassStructure(classBeingRedefined);
    }

    /**
     * @param cr
     * @param targetClassLoader
     * @return 注意，为了自动计算帧的大小，有时必须计算两个类共同的父类。
     * 缺省情况下，ClassWriter将会在getCommonSuperClass方法中计算这些，通过在加载这两个类进入虚拟机时，使用反射API来计算。
     * 但是，如果你将要生成的几个类相互之间引用，这将会带来问题，因为引用的类可能还不存在。
     * 在这种情况下，你可以重写getCommonSuperClass方法来解决这个问题。
     * <p>
     * 通过重写 getCommonSuperClass() 方法，更正获取ClassLoader的方式，改成使用指定ClassLoader的方式进行。
     * 规避了原有代码采用Object.class.getClassLoader()的方式
     */
    private ClassWriter createClassWriter(ClassReader cr, final ClassLoader targetClassLoader) {
        return new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                Class<?> c, d;
                try {
                    c = Class.forName(type1.replace('/', '.'), false, targetClassLoader);
                    d = Class.forName(type2.replace('/', '.'), false, targetClassLoader);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (c.isAssignableFrom(d)) {
                    return type1;
                }
                if (d.isAssignableFrom(c)) {
                    return type2;
                }
                if (c.isInterface() || d.isInterface()) {
                    return "java/lang/Object";
                } else {
                    do {
                        c = c.getSuperclass();
                    } while (!c.isAssignableFrom(d));
                    return c.getName().replace('.', '/');
                }
            }
        };
    }
}
