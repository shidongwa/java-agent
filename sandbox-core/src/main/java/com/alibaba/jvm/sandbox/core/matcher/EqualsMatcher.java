package com.alibaba.jvm.sandbox.core.matcher;

public class EqualsMatcher<T> implements Matcher<T> {

    private final T pattern;

    public EqualsMatcher(T pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matching(T target) {
        return isEquals(target, pattern);
    }

    public static <E> boolean isEquals(E src, E target) {
        return null == src
                && null == target
                || null != src
                && null != target
                && src.equals(target);
    }
}