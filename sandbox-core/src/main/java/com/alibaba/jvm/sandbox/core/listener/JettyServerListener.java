package com.alibaba.jvm.sandbox.core.listener;

import com.alibaba.jvm.sandbox.core.api.event.Event;

public class JettyServerListener extends ListenerAdapter {

    public JettyServerListener() {
        this.id = "103";
        this.classPattern = "org.eclipse.jetty.server.Server";
        this.methodPattern = "handle";
        this.paraCnt = 1;

    }

    @Override
    public void onEvent(Event event) throws Throwable {
        System.out.println("jetty server event occurs:" + event.type);
    }
}
