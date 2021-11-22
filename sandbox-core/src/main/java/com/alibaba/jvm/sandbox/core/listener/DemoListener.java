package com.alibaba.jvm.sandbox.core.listener;

import com.alibaba.jvm.sandbox.core.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.core.api.event.Event;

public class DemoListener extends ListenerAdapter {

    public DemoListener() {
        this.id = "104";
        this.classPattern = "com.stone.service.DemoService";
        this.methodPattern = "sayHello";
        this.paraCnt = 1;
    }

    @Override
    public void onEvent(Event event) throws Throwable {
        if(event.type == Event.Type.BEFORE) {
            BeforeEvent beforeEvent = (BeforeEvent)event;

            System.out.println("before event, processId:" + beforeEvent.processId + ", invokeId:" + beforeEvent.invokeId
                    + ", body:" + (String)beforeEvent.argumentArray[0]);
        }
    }
}
