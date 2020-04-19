package com.alibaba.jvm.sandbox.core.listener;

import com.alibaba.jvm.sandbox.core.api.event.BeforeEvent;
import com.alibaba.jvm.sandbox.core.api.event.Event;

public class JettyHttpBodyListener extends ListenerAdapter {
//    protected Event.Type[] eventTypes = new Event.Type[]{Event.Type.BEFORE};

    public JettyHttpBodyListener() {
        this.id = "102";
        this.classPattern = "org.eclipse.jetty.server.HttpInput";
        this.methodPattern = "read";
        this.paraCnt = 3;
    }

/*    @Override
    public Event.Type[] support() {
        return eventTypes;
    }*/

    @Override
    public void onEvent(Event event) throws Throwable {
        System.out.println("jetty http body event occurs:" + event.type);
        if(event.type == Event.Type.BEFORE) {
            BeforeEvent beforeEvent = (BeforeEvent)event;

            System.out.println("processId:" + beforeEvent.processId + ", invokeId:" + beforeEvent.invokeId
                    + ", body:" + new String((byte[])beforeEvent.argumentArray[0]));
        }
    }
}
