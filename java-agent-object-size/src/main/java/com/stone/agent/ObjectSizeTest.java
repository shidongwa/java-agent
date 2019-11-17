package com.stone.agent;

import java.lang.instrument.Instrumentation;

public class ObjectSizeTest {
    private Instrumentation inst;

    static class X1 {
    }

    static class X2 {
        int a;
    }

    static class X3 {
        Integer i;
        Long l;
        Double d;
    }

    static class X4 {
        int[] a = new int[100];
    }

    static class X5 {
        Integer[] a;
    }

    public ObjectSizeTest(Instrumentation inst) {
        this.inst = inst;
    }

    public void test() {
        System.out.println("X0:" + inst.getObjectSize(new Object()));
        System.out.println("X1:" + inst.getObjectSize(new X1()));
        System.out.println("X2:" + inst.getObjectSize(new X2()));
        System.out.println("X3:" + inst.getObjectSize(new X3()));
        System.out.println("X4:" + inst.getObjectSize(new X4()));
        System.out.println("X5:" + inst.getObjectSize(new X5()));
    }

    public static void main(String[] args) {

    }
}
