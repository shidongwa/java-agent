package com.stone.greys.agent;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

/**
 * 代理启动类
 * Created by oldmanpushcart@gmail.com on 15/5/19.
 */
public class AgentLauncher {

    // 全局持有classloader用于隔离greys实现
    private static volatile ClassLoader greysClassLoader;

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }


    /**
     * 重置greys的classloader<br/>
     * 让下次再次启动时有机会重新加载
     */
    public synchronized static void resetGreysClassLoader() {
        greysClassLoader = null;
    }

    private static ClassLoader loadOrDefineClassLoader(String agentJar) throws Throwable {

        final ClassLoader classLoader;

        // 如果已经被启动则返回之前启动的classloader
        if (null != greysClassLoader) {
            classLoader = greysClassLoader;
        }

        // 如果未启动则重新加载
        else {
            classLoader = new AgentClassLoader(agentJar);

            // 获取各种Hook
            final Class<?> adviceWeaverClass = classLoader.loadClass("com.github.ompc.greys.core.advisor.AdviceWeaver");

            // 初始化全局间谍
            Spy.initForAgentLauncher(
                    classLoader,
                    adviceWeaverClass.getMethod("methodOnBegin",
                            int.class,
                            ClassLoader.class,
                            String.class,
                            String.class,
                            String.class,
                            Object.class,
                            Object[].class),
                    adviceWeaverClass.getMethod("methodOnReturnEnd",
                            Object.class,
                            int.class),
                    adviceWeaverClass.getMethod("methodOnThrowingEnd",
                            Throwable.class,
                            int.class),
                    adviceWeaverClass.getMethod("methodOnInvokeBeforeTracing",
                            int.class,
                            Integer.class,
                            String.class,
                            String.class,
                            String.class),
                    adviceWeaverClass.getMethod("methodOnInvokeAfterTracing",
                            int.class,
                            Integer.class,
                            String.class,
                            String.class,
                            String.class),
                    adviceWeaverClass.getMethod("methodOnInvokeThrowTracing",
                            int.class,
                            Integer.class,
                            String.class,
                            String.class,
                            String.class,
                            String.class),
                    AgentLauncher.class.getMethod("resetGreysClassLoader")
            );
        }

        return greysClassLoader = classLoader;
    }

    private static synchronized void main(final String args, final Instrumentation inst) {
        try {

            // 传递的args参数分两个部分:agentJar路径和agentArgs
            // 分别是Agent的JAR包路径和期望传递到服务端的参数
            final int index = args.indexOf(';');
            final String agentJar = args.substring(0, index);
            final String agentArgs = args.substring(index, args.length());

            // 将Spy添加到BootstrapClassLoader
            inst.appendToBootstrapClassLoaderSearch(
                    new JarFile(AgentLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile())
            );

            // 构造自定义的类加载器，尽量减少Greys对现有工程的侵蚀
            final ClassLoader agentLoader = loadOrDefineClassLoader(agentJar);

            // Configure类定义
            final Class<?> adviceLauncher = agentLoader.loadClass("com.github.ompc.greys.core.advice.AdviceLauncher");

            // 运行时字节码修改
            adviceLauncher
                    .getMethod("launch", Instrumentation.class)
                    .invoke(null, inst);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
