package com.alibaba.jvm.sandbox.core.matcher;

public class MethodMatcher implements Matcher<String> {

    private Matcher matcher;

    public MethodMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matching(String target) {
        return matcher.matching(target);
    }
}
