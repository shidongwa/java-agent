package com.alibaba.jvm.sandbox.core;

import com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler;
import com.alibaba.jvm.sandbox.core.manager.impl.InstrumentManager;
import com.alibaba.jvm.sandbox.core.util.SpyUtils;

import java.lang.instrument.Instrumentation;

public class CustomSandbox {
    private static volatile CustomSandbox sandbox;
    private Instrumentation inst;
    private final CoreConfigure cfg;


    public CustomSandbox(final CoreConfigure cfg, final Instrumentation inst) {
        EventListenerHandler.getSingleton();
        this.cfg = cfg;
        this.inst = inst;

        SpyUtils.init(cfg.getNamespace());
    }

    public void instrument() {
        new InstrumentManager(inst).instrument();
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

}
