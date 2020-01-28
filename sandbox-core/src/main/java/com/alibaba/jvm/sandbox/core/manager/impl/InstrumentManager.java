package com.alibaba.jvm.sandbox.core.manager.impl;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.api.ProcessControlException;
import com.alibaba.jvm.sandbox.core.api.event.Event;
import com.alibaba.jvm.sandbox.core.api.filter.NameRegexFilter;
import com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler;
import com.alibaba.jvm.sandbox.core.manager.CoreLoadedClassDataSource;
import com.alibaba.jvm.sandbox.core.manager.impl.DefaultCoreLoadedClassDataSource;
import com.alibaba.jvm.sandbox.core.manager.impl.SandboxClassFileTransformer;
import com.alibaba.jvm.sandbox.core.util.SpyUtils;
import com.alibaba.jvm.sandbox.core.util.matcher.ExtFilterMatcher;
import com.alibaba.jvm.sandbox.core.util.matcher.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;

public class InstrumentManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private  Instrumentation inst;
    private CoreLoadedClassDataSource classDataSource;

    public InstrumentManager(Instrumentation inst) {
        this.inst = inst;
        this.classDataSource = new DefaultCoreLoadedClassDataSource(inst, true);
    }


    public void instrument() {
        Matcher matcher = new ExtFilterMatcher(new NameRegexFilter("org.eclipse.jetty.server.Server",
                "handle"));

        // 监听THROWS事件并且改变原有方法抛出异常为正常返回
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event event) throws Throwable {
                System.out.println("enter org.eclipse.jetty.server.Server.handle event = [" + event + "]");
            }
        };

        Event.Type[] types = new Event.Type[]{Event.Type.BEFORE};
        SandboxClassFileTransformer sandClassFileTransformer = new SandboxClassFileTransformer(
                matcher, listener, types);

        // 注册到JVM加载上ClassFileTransformer处理新增的类
        if(inst != null) {

            // 查找需要渲染的类集合
            final List<Class<?>> waitingReTransformClasses = classDataSource.findForReTransform(matcher);
            if(waitingReTransformClasses != null && waitingReTransformClasses.size() > 0) {
                try {
                    inst.addTransformer(sandClassFileTransformer, true);
                    inst.retransformClasses(waitingReTransformClasses.toArray(new Class[0]));
                    logger.info("transform classes: {}", waitingReTransformClasses);

                    EventListenerHandler.getSingleton().active(sandClassFileTransformer.getListenerId(),
                            sandClassFileTransformer.getEventListener(),
                            sandClassFileTransformer.getEventTypeArray());
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                } finally {
                    inst.removeTransformer(sandClassFileTransformer);
                }
            }
        }
    }

}
