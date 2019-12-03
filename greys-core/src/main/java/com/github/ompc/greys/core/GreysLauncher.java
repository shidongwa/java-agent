package com.github.ompc.greys.core;

import com.github.ompc.greys.core.util.LogUtil;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.ompc.greys.core.util.GaStringUtils.getCauseMessage;
import static java.lang.System.getProperty;

/**
 * Greys启动器
 */
public class GreysLauncher {
    private static final Logger logger = LogUtil.getLogger();

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
        configure.setJavaPid(getJavaPid());

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

    private int getJavaPid() {
        String line;
        String pid = "";
        //Executable file name of the application to check.
        final String applicationToCheck = "com.stone.Application";
        try {
            //Running command that will get all the working processes.
            Process proc = Runtime.getRuntime().exec("ps -e -opid,command");
            InputStream stream = proc.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            //Parsing the input stream.
            while ((line = reader.readLine()) != null) {
                Pattern pattern = Pattern.compile(applicationToCheck);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    pid = line.split(" ")[0];
                    logger.info("java pid: {}", pid);
                    break;
                }
            }
        } catch (Exception ex) {
            // ignore
        }

        return Integer.valueOf(pid);
    }
}
