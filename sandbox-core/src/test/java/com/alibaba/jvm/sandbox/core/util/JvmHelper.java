package com.alibaba.jvm.sandbox.core.util;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.api.event.Event;
import com.alibaba.jvm.sandbox.core.api.filter.Filter;
import com.alibaba.jvm.sandbox.core.enhance.EventEnhancer;
import com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler;
import com.alibaba.jvm.sandbox.core.util.matcher.ExtFilterMatcher;
import com.alibaba.jvm.sandbox.core.util.matcher.MatchingResult;
import com.alibaba.jvm.sandbox.core.util.matcher.structure.ClassStructureFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.alibaba.jvm.sandbox.core.CoreConfigure.toConfigure;
import static com.alibaba.jvm.sandbox.core.api.util.GaStringUtils.getJavaClassName;
import static com.alibaba.jvm.sandbox.core.util.QaClassUtils.toByteArray;
import static com.alibaba.jvm.sandbox.core.util.QaClassUtils.toResourceName;

/**
 * JVM帮助类，能模拟一个JVM对类的管理行为
 */
public class JvmHelper {

    private static final Logger logger = LoggerFactory.getLogger(JvmHelper.class);

    private final String namespace;
    private final PrivateClassLoader classLoader
            = new PrivateClassLoader();

    public JvmHelper(final String namespace) {
        this.namespace = namespace;
        SpyUtils.init(namespace);
        toConfigure(String.format(";namespace=%s;", namespace), "");
    }

    public JvmHelper defineClass(final Class<?> clazz) throws IOException, InvocationTargetException, IllegalAccessException {
        return defineClass(
                getJavaClassName(clazz),
                toByteArray(clazz)
        );
    }

    public JvmHelper defineClass(final String javaClassName,
                                 final byte[] byteCodeArray) throws InvocationTargetException, IllegalAccessException {
        classLoader.defineClass(javaClassName, byteCodeArray);
        return this;
    }

    public static class Transformer {
        private final Filter filter;
        private final EventListener listener;
        private final Event.Type[] eventTypes;

        public Transformer(final Filter filter,
                           final EventListener listener,
                           final Event.Type... eventTypes) {
            this.filter = filter;
            this.listener = listener;
            this.eventTypes = eventTypes;
        }

        public byte[] transform(final String namespace,
                                final ClassLoader loader,
                                final byte[] byteCodes) {

            final MatchingResult matchingResult = new ExtFilterMatcher(filter)
                    .matching(ClassStructureFactory.createClassStructure(byteCodes, loader));

            final int listenerId = ObjectIDs.instance.identity(listener);
            EventListenerHandler.getSingleton().active(
                    listenerId,
                    listener,
                    eventTypes
            );

            if (matchingResult.isMatched()) {
                return new EventEnhancer().toByteCodeArray(
                        loader,
                        byteCodes,
                        matchingResult.getBehaviorSignCodes(),
                        namespace,
                        listenerId,
                        eventTypes
                );
            } else {
                return byteCodes;
            }
        }

    }

    public JvmHelper defineClass(final Class<?> clazz,
                                 final Filter filter,
                                 final EventListener listener,
                                 final Event.Type... eventType) throws IllegalAccessException, IOException, InvocationTargetException {
        return defineClass(new Class<?>[]{clazz}, filter, listener, eventType);
    }

    public JvmHelper defineClass(final Class<?>[] classes,
                                 final Filter filter,
                                 final EventListener listener,
                                 final Event.Type... eventTypes) throws IllegalAccessException, IOException, InvocationTargetException {
        return defineClass(
                classes,
                new Transformer(filter, listener, eventTypes)
        );
    }

    public JvmHelper defineClass(final Class<?> clazz,
                                 final Transformer... transformers) throws IOException, InvocationTargetException, IllegalAccessException {
        return defineClass(new Class<?>[]{clazz}, transformers);
    }

    public JvmHelper defineClass(final Class<?>[] classes,
                                 final Transformer... transformers) throws IOException, InvocationTargetException, IllegalAccessException {
        for (final Class<?> clazz : classes) {
            final String javaClassName = getJavaClassName(clazz);
            byte[] byteCodes = toByteArray(clazz);
            for (final Transformer transformer : transformers) {
                byteCodes = transformer.transform(namespace, classLoader, byteCodes);
            }
            defineClass(javaClassName, byteCodes);
        }
        return this;
    }

    public Class<?> loadClass(String javaClassName) throws ClassNotFoundException {
        return classLoader.loadClass(javaClassName);
    }

    public JvmHelper dump(File dumpDir) {
        classLoader.dump(dumpDir);
        return this;
    }


    /**
     * 私有的ClassLoader
     */
    static class PrivateClassLoader extends ClassLoader {

        private final Map<String, byte[]> javaClassByteArrayMap
                = new HashMap<String, byte[]>();

        private final Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

        public PrivateClassLoader() {
        }

        public PrivateClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

            for (final Class<?> clazz : classes) {
                if (StringUtils.equals(name, getJavaClassName(clazz))) {
                    return clazz;
                }
            }

            final Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass == null) {
                try {
                    final Class<?> aClass = findClass(name);
                    if (resolve) {
                        resolveClass(aClass);
                    }
                    return aClass;
                } catch (Exception e) {
                    return super.loadClass(name, resolve);
                }
            } else {
                return loadedClass;
            }
        }

        public Class<?> defineClass(final String javaClassName,
                                    final byte[] classByteArray) throws InvocationTargetException, IllegalAccessException {
            javaClassByteArrayMap.put(toResourceName(javaClassName), classByteArray);
            final Class<?> clazz = SandboxReflectUtils.defineClass(this, javaClassName, classByteArray);
            classes.add(clazz);
            return clazz;
        }

        public void dump(File dumpDir) {
            for (Map.Entry<String, byte[]> entry : javaClassByteArrayMap.entrySet()) {
                final File dumpClassFile = new File(dumpDir.getPath() + File.separatorChar + entry.getKey());
                try {
                    FileUtils.writeByteArrayToFile(dumpClassFile, entry.getValue());
                    logger.info("dump class {}", dumpClassFile);
                } catch (IOException e) {
                    logger.warn("dump class file:{} occur error!", dumpClassFile, e);
                }
            }
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            if (javaClassByteArrayMap.containsKey(name)) {
                return new ByteArrayInputStream(javaClassByteArrayMap.get(name));
            }
            return super.getResourceAsStream(name);
        }

    }

    public static JvmHelper createJvm(final String namespace) {
        return new JvmHelper(StringUtils.isBlank(namespace) ? "default" : namespace);
    }

    public static JvmHelper createJvm() {
        return createJvm("default");
    }

}
