package com.alibaba.jvm.sandbox.core.util.matcher;

import com.alibaba.jvm.sandbox.core.api.filter.Filter;
import com.alibaba.jvm.sandbox.core.util.matcher.structure.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.alibaba.jvm.sandbox.core.api.filter.AccessFlags.*;
import static com.alibaba.jvm.sandbox.core.util.SandboxStringUtils.toInternalClassName;

/**
 * 过滤器实现的匹配器
 *
 * @author luanjia@taobao.com
 */
public class ExtFilterMatcher implements Matcher {

    private final Filter extFilter;

    public ExtFilterMatcher(final Filter extFilter) {
        this.extFilter = extFilter;
    }

    // 获取需要匹配的类结构
    // 如果要匹配子类就需要将这个类的所有家族成员找出
    private Collection<ClassStructure> getWaitingMatchClassStructures(final ClassStructure classStructure) {
        final Collection<ClassStructure> waitingMatchClassStructures = new ArrayList<ClassStructure>();
        waitingMatchClassStructures.add(classStructure);

        return waitingMatchClassStructures;
    }

    private String[] toJavaClassNameArray(final Collection<ClassStructure> classStructures) {
        if (null == classStructures) {
            return null;
        }
        final List<String> javaClassNames = new ArrayList<String>();
        for (final ClassStructure classStructure : classStructures) {
            javaClassNames.add(classStructure.getJavaClassName());
        }
        return javaClassNames.toArray(new String[0]);
    }

    private boolean matchingClassStructure(ClassStructure classStructure) {
        for (final ClassStructure wmCs : getWaitingMatchClassStructures(classStructure)) {

            // 匹配类结构
            if (extFilter.doClassFilter(
                    toFilterAccess(wmCs.getAccess()),
                    wmCs.getJavaClassName(),
                    null == wmCs.getSuperClassStructure()
                            ? null
                            : wmCs.getSuperClassStructure().getJavaClassName(),
                    toJavaClassNameArray(wmCs.getFamilyInterfaceClassStructures()),
//                    toJavaClassNameArray(wmCs.getFamilyAnnotationTypeClassStructures())
                    null
            )) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MatchingResult matching(final ClassStructure classStructure) {

        try {
            return _matching(classStructure);
        } catch (NoClassDefFoundError error) {

            // 根据 #203 ClassStructureImplByJDK会存在类加载异步的问题
            // 所以这里对JDK实现的ClassStructure抛出NoClassDefFoundError的时候做一个兼容
            // 转换为ASM实现然后进行match
            if (classStructure instanceof ClassStructureImplByJDK
                    && classStructure.getClassLoader() != null) {
                final String javaClassResourceName = toInternalClassName(classStructure.getJavaClassName()).concat(".class");
                InputStream is = null;
                try {
                    is = classStructure.getClassLoader().getResourceAsStream(javaClassResourceName);
                    _matching(ClassStructureFactory.createClassStructure(is, classStructure.getClassLoader()));
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }

            // 其他情况就直接抛出error
            throw error;
        }

    }

    private MatchingResult _matching(final ClassStructure classStructure) {
        final MatchingResult result = new MatchingResult();

        // 匹配ClassStructure
        if (!matchingClassStructure(classStructure)) {
            return result;
        }

        // 匹配BehaviorStructure
        for (final BehaviorStructure behaviorStructure : classStructure.getBehaviorStructures()) {
            if (extFilter.doMethodFilter(
                    toFilterAccess(behaviorStructure.getAccess()),
                    behaviorStructure.getName(),
                    toJavaClassNameArray(behaviorStructure.getParameterTypeClassStructures()),
                    toJavaClassNameArray(behaviorStructure.getExceptionTypeClassStructures()),
                    toJavaClassNameArray(behaviorStructure.getAnnotationTypeClassStructures())
            )) {
                result.getBehaviorStructures().add(behaviorStructure);
            }
        }
        return result;
    }


    /**
     * 转换为AccessFlags的Access体系
     *
     * @param access access flag
     * @return 部分兼容ASM的access flag
     */
    private static int toFilterAccess(final Access access) {
        int flag = 0;
        if (access.isPublic()) flag |= ACF_PUBLIC;
        if (access.isPrivate()) flag |= ACF_PRIVATE;
        if (access.isProtected()) flag |= ACF_PROTECTED;
        if (access.isStatic()) flag |= ACF_STATIC;
        if (access.isFinal()) flag |= ACF_FINAL;
        if (access.isInterface()) flag |= ACF_INTERFACE;
        if (access.isNative()) flag |= ACF_NATIVE;
        if (access.isAbstract()) flag |= ACF_ABSTRACT;
        if (access.isEnum()) flag |= ACF_ENUM;
        if (access.isAnnotation()) flag |= ACF_ANNOTATION;
        return flag;
    }



    public static Matcher toOrGroupMatcher(final Filter[] filterArray) {

        final Matcher[] matcherArray = new Matcher[ArrayUtils.getLength(filterArray)];
        for (int index = 0; index < matcherArray.length; index++) {
            matcherArray[index] = new ExtFilterMatcher(filterArray[index]);
        }
        return new GroupMatcher.Or(matcherArray);
    }
}
