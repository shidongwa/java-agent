### demo how to introduce asm and customize hook logic to business hook class, especially hook class loaded by bootstrap classloader

```shell
java -javaagent:/Users/xxx/git-project/java-agent/out/artifacts/java_agent_injector_jar/java-agent-injector.jar  -Xbootclasspath/a:/Users/xxx/git-project/java-agent/out/artifacts/java_agent_injector_jar/asm-9.4.jar:/Users/xxx/git-project/java-agent/out/artifacts/java_agent_test_jar/java-agent-test.jar  -jar ~/git-project/java-agent/out/artifacts/java_agent_test_jar/java-agent-test.jar
```

here jar is packaged by idea.
  

### demo how to package by mvn plugin with MANIFEST.MF
java -javaagent:/Users/dong/git-project/java-agent/out/artifacts/java_agent_injector_jar/java-agent-injector.jar  -Xbootclasspath/a:/Users/dong/git-project/java-agent/out/artifacts/java_agent_injector_jar/asm-9.4.jar:/Users/dong/git-project/java-agent/out/artifacts/java_agent_test_jar/java-agent-test.jar -jar ~/git-project/java-agent/out/artifacts/java_agent_test_jar/java-agent-test.jar


### about SOF question
https://stackoverflow.com/questions/75767855/why-are-my-self-written-classes-3-party-library-classes-invisible-to-jre-class/75776208#75776208