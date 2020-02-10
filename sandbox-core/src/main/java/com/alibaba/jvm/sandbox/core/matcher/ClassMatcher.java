package com.alibaba.jvm.sandbox.core.matcher;

public class ClassMatcher implements Matcher<String> {

    private Matcher matcher;

    public ClassMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matching(String target) {
        return matcher.matching(target);
    }
}
