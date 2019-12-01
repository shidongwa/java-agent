package com.github.ompc.greys.core;

import java.util.List;

import static com.github.ompc.greys.core.util.GaStringUtils.getCauseMessage;
import static java.lang.System.getProperty;

/**
 * Greys启动器
 */
public class GreysLauncher {

    /**
     * greys' core jarfile
     */
    public static final String CORE_JARFILE =
            getProperty("user.dir") + "/.greys/lib/1.7.6.6/greys/greys-core.jar";

    /**
     * greys' agent jarfile
     */
    public static final String AGENT_JARFILE =
            getProperty("user.dir") + "/.greys/lib/1.7.6.6/greys/greys-agent.jar";


    public GreysLauncher(String[] args) throws Exception {

        // 解析配置文件
        Configure configure = new Configure();
        configure.setGreysAgent("/Users/shidonghua/git-project/java-agent/greys-agent/target/greys-agent.jar");
        configure.setGreysCore("/Users/shidonghua/git-project/java-agent/greys-core/target/greys-core.jar");
        configure.setJavaPid(26830);

        // 加载agent
        attachAgent(configure);
    }

    /*
     * 加载Agent
     */
    private void attachAgent(Configure configure) throws Exception {

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> vmdClass = loader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
        final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

        Object attachVmdObj = null;
        for (Object obj : (List<?>) vmClass.getMethod("list", (Class<?>[]) null).invoke(null, (Object[]) null)) {
            if ((vmdClass.getMethod("id", (Class<?>[]) null).invoke(obj, (Object[]) null))
                    .equals(Integer.toString(configure.getJavaPid()))) {
                attachVmdObj = obj;
            }
        }

//        if (null == attachVmdObj) {
//            // throw new IllegalArgumentException("pid:" + configure.getJavaPid() + " not existed.");
//        }

        Object vmObj = null;
        try {
            if (null == attachVmdObj) { // 使用 attach(String pid) 这种方式
                vmObj = vmClass.getMethod("attach", String.class).invoke(null, "" + configure.getJavaPid());
            } else {
                vmObj = vmClass.getMethod("attach", vmdClass).invoke(null, attachVmdObj);
            }
            vmClass.getMethod("loadAgent", String.class, String.class).invoke(vmObj, configure.getGreysAgent(), configure.getGreysCore() + ";" + configure.toString());
        } finally {
            if (null != vmObj) {
                vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
            }
        }
    }


    public static void main(String[] args) {
        try {
            new GreysLauncher(args);
        } catch (Throwable t) {
            System.err.println("start greys failed, because : " + getCauseMessage(t));
            System.exit(-1);
        }
    }
}
