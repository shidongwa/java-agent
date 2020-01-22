# java-agent **java agent应用场景验证**
## 对象大小计算

### 关闭指针压缩JVM启动参数中加入
```
-XX:-UseCompressedOops -javaagent:/Users/shidonghua/git-project/java-agent/java-agent-object-size/target/java-agent-object-size-1.0-SNAPSHOT.jar
```

### 开启指针压缩JVM启动参数加入
```
-XX:+UseCompressedOops -javaagent:/Users/shidonghua/git-project/java-agent/java-agent-object-size/target/java-agent-object-size-1.0-SNAPSHOT.jar
```

或者不加UseCompressedOops参数，默认是开启的

```
-javaagent:/Users/shidonghua/git-project/java-agent/java-agent-object-size/target/java-agent-object-size-1.0-SNAPSHOT.jar
```

## java方法跟踪，比如入参，返回

1. 项目根目录运行`mvn clean package`,生成greys-agent.jar，greys-core.jar
1. 运行java-agent-test SBA，这是被java agent注入的目标应用
1. 运行greys-core项目中的GreysLauncher，不需要指定参数。需要修改第一步生成的greys-agent.jar,greys-core.jar的路径
1. 访问`http://localhost:8080`
1. 观察java-agent-test spring Boot项目运行日志
```text
2020-01-22 09:15:47 [Attach Listener] INFO  greys-anatomy - reg adviceId=1;listener=com.github.ompc.greys.core.advice.impl.TraceAdvice$1@36b0f275
2020-01-22 09:15:48 [Attach Listener] INFO  greys-anatomy - Spy already in targetClassLoader : sun.misc.Launcher$AppClassLoader@18b4aac2
2020-01-22 09:17:29 [qtp1414013111-23] INFO  greys-anatomy - advice before invoked
2020-01-22 09:17:29 [qtp1414013111-23] INFO  greys-anatomy - cost: 87
2020-01-22 09:17:29 [qtp1414013111-28] INFO  greys-anatomy - advice before invoked
2020-01-22 09:17:29 [qtp1414013111-28] INFO  greys-anatomy - cost: 19

```

## 参考

- [greys](https://github.com/oldmanpushcart/greys-anatomy)