package com.github.ompc.greys.core.advice;

import com.github.ompc.greys.core.ClassDataSource;
import com.github.ompc.greys.core.advisor.AdviceWeaver;
import com.github.ompc.greys.core.advisor.Enhancer;
import com.github.ompc.greys.core.advice.impl.TraceAdvice;
import com.github.ompc.greys.core.manager.ReflectManager;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
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

        scheduleTask();
    }

    private static void scheduleTask() {
        Timer timer = new Timer(); // creating timer
        /*
        // check if findDeadlockedThreads will cause STW
        TimerTask task1 = new DeadLockCheckTask(); // creating timer task
        // scheduling the task for repeated fixed-delay execution, beginning after the specified delay
        timer.schedule(task1, 1000, 3 * 60 * 1000);*/

        // check if thread dump will cause STW
        TimerTask task2 = new ThreadDumpTask();
        timer.schedule(task2, 1000, 3 * 60 * 1000);
    }

    static class DeadLockCheckTask extends TimerTask {
        @Override
        public void run() {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long[] threadIds = bean.findDeadlockedThreads(); // <1>
            System.out.println("dead lock checker task running...");
            if (threadIds != null) {
                ThreadInfo[] infos = bean.getThreadInfo(threadIds); // <2>

                for (ThreadInfo info : infos) {
                    StackTraceElement[] stack = info.getStackTrace();
                    // Log or store stack trace information.
                    for(StackTraceElement e : stack) {
                        System.out.println("threadName:" + info.getThreadName() + "-----" + e.toString());
                    }
                }
            }
        }
    }

    static class ThreadDumpTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("thread dump task running...");
            for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                System.out.println(ste + "\n");
            }
        }
    }
}
