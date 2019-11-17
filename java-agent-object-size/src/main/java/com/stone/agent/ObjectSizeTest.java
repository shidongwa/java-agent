package com.stone.agent;

import java.lang.instrument.Instrumentation;

public class ObjectSizeTest {
    private Instrumentation inst;

    /**
     * -XX:+UseCompressedOops: mark/8 + Klass/8  = 16
     * -XX:-UseCompressedOops: mark/8 + Klass/4  + padding/4 = 16
     */
    static class X1 {
    }

    /**
     * -XX:+UseCompressedOops: mark/8 + Klass/4  + a/4 = 16
     * -XX:-UseCompressedOops: mark/8 + Klass/8 + a/4 + padding/4 = 24
     */
    static class X2 {
        int a = 0;
    }

    /**
     * -XX:+UseCompressedOops: mark/8 + Klass/4  + i/4 = 16
     * -XX:-UseCompressedOops: mark/8 + Klass/8 + i/8 = 24
     */
    static class X3 {
        Integer i = new Integer(0);
    }

    /**
     * -XX:+UseCompressedOops: mark/8 + Klass/4  + size/4  = 16
     * -XX:-UseCompressedOops: mark/8 + Klass/8 + size/4 + padding/4 = 24
     */
    static class X4 {
        int[] a = new int[100];
    }

    /**
     * -XX:+UseCompressedOops: mark/8 + Klass/4  + size/4  = 16
     * -XX:-UseCompressedOops: mark/8 + Klass/8 + size/4 + padding/4 = 24
     */
    static class X5 {
        Integer[] a = new Integer[100];
    }

    /**
     * -XX:+UseCompressedOops: mark/8 + Klass/4  + i/4 + l/8 + d/8  = 32
     * -XX:-UseCompressedOops: mark/8 + Klass/8 + i/4 + l/8 + d/8 + padding/4= 40
     */
    static class X6 {
        int i = 0;
        long l = 0;
        double d = 0;
    }

    public ObjectSizeTest(Instrumentation inst) {
        this.inst = inst;
    }

    public void test() {
        /**
         * -XX:+UseCompressedOops: mark/8 + Klass/8  = 16
         * -XX:-UseCompressedOops: mark/8 + Klass/4  + padding/4 = 16
         */
        System.out.println("X0:" + inst.getObjectSize(new Object()));
        System.out.println("X1:" + inst.getObjectSize(new X1()));
        System.out.println("X2:" + inst.getObjectSize(new X2()));
        System.out.println("X3:" + inst.getObjectSize(new X3()));
        System.out.println("X4:" + inst.getObjectSize(new X4()));
        System.out.println("X5:" + inst.getObjectSize(new X5()));
        System.out.println("X6:" + inst.getObjectSize(new X6()));
    }

    public static void main(String[] args) {

    }
}
