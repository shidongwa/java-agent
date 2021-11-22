package com.stone.agent;

import com.stone.agent.transformer.GreetingTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent1 {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain 1");
        inst.addTransformer(new GreetingTransformer(), true);
    }

    public static void agentmain(String args, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        System.out.println("agentmain 1");
        inst.addTransformer(new GreetingTransformer(), true);
    }
}
