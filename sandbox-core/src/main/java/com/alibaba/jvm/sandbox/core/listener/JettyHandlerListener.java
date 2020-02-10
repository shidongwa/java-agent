package com.alibaba.jvm.sandbox.core.listener;

import com.alibaba.jvm.sandbox.core.api.event.Event;

public class JettyHandlerListener extends ListenerAdapter {

    public JettyHandlerListener() {
        this.id = "101";
        this.classPattern = "org.eclipse.jetty.server.handler.HandlerWrapper";
//        this.classPattern = "org.eclipse.jetty.server.Server";
        this.methodPattern = "handle";
        this.paraCnt = 4;
//        this.paraCnt = 1;

    }

    @Override
    public void onEvent(Event event) throws Throwable {
        System.out.println("jetty handler event occurs:" + event.type);
    }
}
