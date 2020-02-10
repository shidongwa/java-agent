package com.alibaba.jvm.sandbox.core.matcher;

import java.util.Set;

public class ContainMatcher<T> implements Matcher<T> {

    private Set<T> pattern;

    public ContainMatcher(Set<T> pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matching(T target) {
        return !(pattern == null || pattern.size() == 0) && pattern.contains(target);
    }
}
