package com.alibaba.jvm.sandbox.core.listener;

import com.alibaba.jvm.sandbox.core.api.event.Event;

public class RceListener extends ListenerAdapter {
    public RceListener() {
        this.id = "100";
        this.classPattern = "java.lang.ProcessImpl";
        this.methodPattern = "start";
        this.paraCnt = 5;
    }

    @Override
    public void onEvent(Event event) throws Throwable {
    }

}
