package com.github.ompc.greys.core.advice;

import com.github.ompc.greys.core.ClassDataSource;
import com.github.ompc.greys.core.advisor.AdviceWeaver;
import com.github.ompc.greys.core.advisor.Enhancer;
import com.github.ompc.greys.core.advice.impl.TraceAdvice;
import com.github.ompc.greys.core.manager.ReflectManager;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;

public class AdviceLauncher {
    private static Map<Integer, Advice> enhancerMap = new HashMap<Integer, Advice>();

    static {
        enhancerMap.put(TraceAdvice.ADVICE_ID, new TraceAdvice());
    }

    public static void launch(Instrumentation inst) {
        initForManager(inst);
        AdviceWeaver.reg(TraceAdvice.ADVICE_ID, enhancerMap.get(TraceAdvice.ADVICE_ID).getAdviceListener());

       Set<Map.Entry<Integer, Advice>> set = enhancerMap.entrySet();
       for(Map.Entry<Integer, Advice> entry : set) {
           try {
               Enhancer.enhance(inst, entry.getKey(), entry.getValue().getPointCut());
           } catch (UnmodifiableClassException e) {
               e.printStackTrace();
           }
       }
    }

    /*
     * 初始化各种manager
     */
    private static void initForManager(final Instrumentation inst) {
        ReflectManager.Factory.initInstance(new ClassDataSource() {
            @Override
            public Collection<Class<?>> allLoadedClasses() {
                final Class<?>[] classArray = inst.getAllLoadedClasses();
                return null == classArray
                        ? new ArrayList<Class<?>>()
                        : Arrays.asList(classArray);
            }
        });
    }
}
