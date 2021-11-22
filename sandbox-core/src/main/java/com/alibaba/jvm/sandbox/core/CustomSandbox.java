package com.alibaba.jvm.sandbox.core;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler;
import com.alibaba.jvm.sandbox.core.listener.*;
import com.alibaba.jvm.sandbox.core.manager.impl.InstrumentManager;
import com.alibaba.jvm.sandbox.core.util.LogbackUtils;
import com.alibaba.jvm.sandbox.core.util.SpyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.instrument.Instrumentation;

public class CustomSandbox {
    private static final String DEFAULT_NAMESPACE = "default";
    private static volatile CustomSandbox sandbox;
    private Instrumentation inst;
    private final CoreConfigure cfg;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public CustomSandbox(final CoreConfigure cfg, final Instrumentation inst) {
        EventListenerHandler.getSingleton();
        this.cfg = cfg;
        this.inst = inst;

        SpyUtils.init(cfg.getNamespace());

        LogbackUtils.init(DEFAULT_NAMESPACE,
                cfg.getJvmSandboxHome() + File.separatorChar + "lib" + File.separator + "sandbox-logback.xml"
        );
        logger.info("initializing server. cfg={}", cfg);
    }

    public void instrument() {
//        initCommonListener();
//        initJettyListener();
        initTestListener();
        logger.info("init EventListener success");
        new InstrumentManager(inst).instrument();
        logger.info("all classes instrument success");
    }

    /**
     * 单例
     *
     * @return CoreServer单例
     */
    public static CustomSandbox getInstance(final CoreConfigure cfg, final Instrumentation inst) {
        if (null == sandbox) {
            synchronized (CustomSandbox.class) {
                if (null == sandbox) {
                    sandbox = new CustomSandbox(cfg, inst);
                }
            }
        }

        return sandbox;
    }

    private void initCommonListener() {
        EventListener.Factory.register(new RceListener());
    }

    private void initJettyListener() {
        EventListener.Factory.register(new JettyServerListener());
//        EventListener.Factory.register(new JettyHandlerListener());
        EventListener.Factory.register(new JettyHttpBodyListener());
    }

    private void initTestListener() {
        EventListener.Factory.register(new DemoListener());
    }
}
