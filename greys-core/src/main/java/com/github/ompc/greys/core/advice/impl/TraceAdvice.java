package com.github.ompc.greys.core.advice.impl;

import com.github.ompc.greys.core.advice.Advice;
import com.github.ompc.greys.core.advisor.AdviceListener;
import com.github.ompc.greys.core.advisor.ReflectAdviceListenerAdapter;
import com.github.ompc.greys.core.util.InvokeCost;
import com.github.ompc.greys.core.util.LogUtil;
import com.github.ompc.greys.core.util.PointCut;
import com.github.ompc.greys.core.util.matcher.ClassMatcher;
import com.github.ompc.greys.core.util.matcher.GaMethodMatcher;
import com.github.ompc.greys.core.util.matcher.PatternMatcher;
import org.slf4j.Logger;

public class TraceAdvice implements Advice {
    private String classPattern = "org.eclipse.jetty.server.Server";
    private String methodPattern = "handle";
    private boolean isRegEx = false;
    public static final int ADVICE_ID = 1;
    private static final Logger logger = LogUtil.getLogger();


    @Override
    public PointCut getPointCut() {
        return new PointCut(
                new ClassMatcher(new PatternMatcher(isRegEx, classPattern)),
                new GaMethodMatcher(new PatternMatcher(isRegEx, methodPattern)),
                false
                );
    }

    @Override
    public AdviceListener getAdviceListener() {
        return new ReflectAdviceListenerAdapter() {
            private final InvokeCost invokeCost = new InvokeCost();

            @Override
            public void before(com.github.ompc.greys.core.Advice advice) throws Throwable {
                invokeCost.begin();
                logger.info("advice before invoked");
            }

            @Override
            public void afterReturning(com.github.ompc.greys.core.Advice advice) throws Throwable {
                logger.info("cost: " + invokeCost.cost());
            }

            @Override
            public void afterThrowing(com.github.ompc.greys.core.Advice advice) throws Throwable {
                logger.info("cost: " + invokeCost.cost());
            }
        };
    }
}
