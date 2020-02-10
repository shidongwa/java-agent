package com.alibaba.jvm.sandbox.core.matcher;


public interface Matcher<T> {
    boolean matching(T target);
}
