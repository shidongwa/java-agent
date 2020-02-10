package com.alibaba.jvm.sandbox.core.listener;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.api.event.Event;
import com.google.common.cache.Cache;
import com.google.gson.Gson;

public abstract class ListenerAdapter implements EventListener {
    private final static int TIMEOUT_LIMIT = 500;
    private Gson gson = new Gson();
    private static Cache<String, Boolean> cache;
    protected String id;
    protected String classPattern;
    protected String methodPattern;
    protected int paraCnt;
    protected Event.Type[] eventTypes = new Event.Type[]{Event.Type.BEFORE, Event.Type.RETURN, Event.Type.THROWS};

    @Override
    public String getId() {
        return id;
    }

    public String getClassPattern() {
        return classPattern;
    }

    public String getMethodPattern() {
        return methodPattern;
    }

    public int getParaCnt() {
        return paraCnt;
    }

    @Override
    public Event.Type[] support() {
        return eventTypes;
    }
}
