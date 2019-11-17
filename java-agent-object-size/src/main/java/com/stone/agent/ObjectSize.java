package com.stone.agent;

import java.lang.instrument.Instrumentation;

public class ObjectSize {

    public static void premain(String args, Instrumentation inst) {
        System.out.println("args = [" + args + "]");
        ObjectSizeTest sizeTest = new ObjectSizeTest(inst);
        sizeTest.test();
    }

    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("args = [" + args + "], inst = [" + inst + "]");
        ObjectSizeTest sizeTest = new ObjectSizeTest(inst);
        sizeTest.test();
    }

}
