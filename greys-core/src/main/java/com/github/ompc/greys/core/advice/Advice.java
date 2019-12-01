package com.github.ompc.greys.core.advice;

import com.github.ompc.greys.core.advisor.AdviceListener;
import com.github.ompc.greys.core.util.PointCut;

public interface Advice {
    /**
     * 获取增强功能点
     * @return
     */
    PointCut getPointCut();

    /**
     * 获取监听器
     *
     * @return 返回监听器
     */
    AdviceListener getAdviceListener();
}
