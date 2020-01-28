package com.alibaba.jvm.sandbox.core.manager.impl;

import com.alibaba.jvm.sandbox.core.api.EventListener;
import com.alibaba.jvm.sandbox.core.api.event.Event;
import com.alibaba.jvm.sandbox.core.enhance.EventEnhancer;
import com.alibaba.jvm.sandbox.core.util.ObjectIDs;
import com.alibaba.jvm.sandbox.core.util.SandboxClassUtils;
import com.alibaba.jvm.sandbox.core.util.SandboxProtector;
import com.alibaba.jvm.sandbox.core.util.matcher.Matcher;
import com.alibaba.jvm.sandbox.core.util.matcher.MatchingResult;
import com.alibaba.jvm.sandbox.core.util.matcher.UnsupportedMatcher;
import com.alibaba.jvm.sandbox.core.util.matcher.structure.ClassStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;

import static com.alibaba.jvm.sandbox.core.util.matcher.structure.ClassStructureFactory.createClassStructure;

/**
 * 沙箱类形变器
 *
 * @author luanjia@taobao.com
 */
public class SandboxClassFileTransformer implements ClassFileTransformer {
    private static final String DEFAULT_NAMESPACE = "default";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Matcher matcher;
    private final EventListener eventListener;
    private final Event.Type[] eventTypeArray;

    private final String namespace = DEFAULT_NAMESPACE;
    private final int listenerId;

    public SandboxClassFileTransformer(final Matcher matcher,
                                EventListener eventListener,
                                Event.Type[] eventTypeArray) {
        this.matcher = matcher;
        this.eventListener = eventListener;
        this.eventTypeArray = eventTypeArray;
        this.listenerId = ObjectIDs.instance.identity(eventListener);
    }

    // 获取当前类结构
    private ClassStructure getClassStructure(final ClassLoader loader,
                                             final Class<?> classBeingRedefined,
                                             final byte[] srcByteCodeArray) {
        return null == classBeingRedefined
                ? createClassStructure(srcByteCodeArray, loader)
                : createClassStructure(classBeingRedefined);
    }

    @Override
    public byte[] transform(final ClassLoader loader,
                            final String internalClassName,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] srcByteCodeArray) {
        try {

            // 这里过滤掉Sandbox所需要的类|来自SandboxClassLoader所加载的类|来自ModuleJarClassLoader加载的类
            // 防止ClassCircularityError的发生
            if (SandboxClassUtils.isComeFromSandboxFamily(internalClassName, loader)) {
                return null;
            }

            return _transform(
                    loader,
                    internalClassName,
                    classBeingRedefined,
                    srcByteCodeArray
            );


        } catch (Throwable cause) {
            logger.warn("sandbox transform {} in loader={}; failed will ignore this transform.",
                    internalClassName,
                    loader,
                    cause
            );
            return null;
        } finally {
        }
    }

    private byte[] _transform(final ClassLoader loader,
                              final String internalClassName,
                              final Class<?> classBeingRedefined,
                              final byte[] srcByteCodeArray) {
        final ClassStructure classStructure = getClassStructure(loader, classBeingRedefined, srcByteCodeArray);
        final MatchingResult matchingResult = new UnsupportedMatcher(loader, true).and(matcher).matching(classStructure);
        final Set<String> behaviorSignCodes = matchingResult.getBehaviorSignCodes();

        // 如果一个行为都没匹配上也不用继续了
        if (!matchingResult.isMatched()) {
            logger.debug("transform ignore {}, no behaviors matched in loader={}", internalClassName, loader);
            return null;
        }

        // 开始进行类匹配
        try {
            final byte[] toByteCodeArray = new EventEnhancer().toByteCodeArray(
                    loader,
                    srcByteCodeArray,
                    behaviorSignCodes,
                    namespace,
                    listenerId,
                    eventTypeArray
            );
            if (srcByteCodeArray == toByteCodeArray) {
                logger.debug("transform ignore {}, nothing changed in loader={}", internalClassName, loader);
                return null;
            }

            logger.info("transform {} finished, in loader={}", internalClassName, loader);
            return toByteCodeArray;
        } catch (Throwable cause) {
            logger.warn("transform {} failed, in loader={}", internalClassName, loader, cause);
            return null;
        }
    }

    /**
     * 获取事件监听器
     *
     * @return 事件监听器
     */
    EventListener getEventListener() {
        return eventListener;
    }

    /**
     * 获取事件监听器ID
     *
     * @return 事件监听器ID
     */
    int getListenerId() {
        return listenerId;
    }

    /**
     * 获取本次匹配器
     *
     * @return 匹配器
     */
    Matcher getMatcher() {
        return matcher;
    }

    /**
     * 获取本次监听事件类型数组
     *
     * @return 本次监听事件类型数组
     */
    Event.Type[] getEventTypeArray() {
        return eventTypeArray;
    }

}
