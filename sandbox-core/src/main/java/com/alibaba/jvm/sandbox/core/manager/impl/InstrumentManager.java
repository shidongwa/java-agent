package com.alibaba.jvm.sandbox.core.manager.impl;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.api.filter.NameRegexExtFilter;
import com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler;
import com.alibaba.jvm.sandbox.core.manager.CoreLoadedClassDataSource;
import com.alibaba.jvm.sandbox.core.util.matcher.ExtFilterMatcher;
import com.alibaba.jvm.sandbox.core.util.matcher.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.Set;

public class InstrumentManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Instrumentation inst;
    private CoreLoadedClassDataSource classDataSource;


    public InstrumentManager(Instrumentation inst) {
        this.inst = inst;
        this.classDataSource = new DefaultCoreLoadedClassDataSource(inst, true);
    }

    public void instrument() {

        Set<String> adviceIds = EventListener.Factory.getAdviceIds();
        if(adviceIds != null) {
            for(String adviceId : adviceIds) {
                enhance(adviceId, inst);
            }

            active();
        }
    }

    public void enhance(String adviceId, Instrumentation instrumentation) {
        EventListener eventListener = EventListener.Factory.getEventListener(adviceId);
        Matcher matcher = new ExtFilterMatcher(new NameRegexExtFilter(eventListener.getClassPattern(),
                eventListener.getMethodPattern(), eventListener.getParaCnt()));
        final List<Class<?>> enhanceClasses = classDataSource.findForReTransform(matcher);
        if(enhanceClasses != null && enhanceClasses.size() > 0) {
            DemoClassFileTransformer sandboxTransformer =
                    new DemoClassFileTransformer(matcher, eventListener, eventListener.support());
            try {
                instrumentation.addTransformer(sandboxTransformer, true);
                for (Class<?> clazz : enhanceClasses) {
                    instrumentation.retransformClasses(clazz);
                }
                logger.info("instrument class [{}], method [{}] success", eventListener.getClassPattern(),
                        eventListener.getMethodPattern());
            } catch (UnmodifiableClassException e) {
                e.printStackTrace();
            } finally {
                instrumentation.removeTransformer(sandboxTransformer);
            }
        } else {
            logger.info("no loaded class found for [{}]", eventListener.getClassPattern());
        }

    }

    public void active() {
        EventListenerHandler elh = EventListenerHandler.getSingleton();
        Set<String> adviceIds = EventListener.Factory.getAdviceIds();
        for(String adviceId : adviceIds) {
            EventListener el = EventListener.Factory.getEventListener(adviceId);
            elh.active(Integer.valueOf(el.getId()), el, el.support());
            logger.info("active listener [{}]", el.getClassPattern());
        }
        logger.info("all EventListeners active now");
    }

    public static void deactive() {
        EventListenerHandler elh = EventListenerHandler.getSingleton();
        Set<String> adviceIds = EventListener.Factory.getAdviceIds();
        for(String adviceId : adviceIds) {
            EventListener el = EventListener.Factory.getEventListener(adviceId);
            elh.frozen(Integer.valueOf(el.getId()));
        }
    }
}
