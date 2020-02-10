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

## greys advice listner方式运行

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

## sandbox event listener方式运行

1. 项目根目录运行`mvn clean package`, lib目录下面生成sandbox-agent.jar, sandbox-core.jar, sandbox-spy.jar
1. 运行java-agent-test SBA，这是被java agent注入的目标应用
1. 运行sandbox-core项目中的CoreLauncher，指定program argument参数`3112 /Users/shidonghua/git-project/java-agent/lib/sandbox-agent.jar na`。替换3112为第二步SBA java进程id，更新sandbox-agent.jar绝对路径
1. 访问`http://localhost:8080/form`
1. 观察java-agent-test spring Boot项目运行日志

## sandbox event listener性能测试
* 运行java-agent-test SBA，这是被java agent注入的目标应用
```bash
java -jar java-agent-test.jar
```
* 访问`http://localhost:8080/demo`, （HttpInput类需要访问一次才能加载）
* 运行注入程序，发起java agent attach
```bash
java -cp .:/usr/local/java/lib/tools.jar:lib/sandbox-core.jar  com.alibaba.jvm.sandbox.core.CoreLauncher 142846  /opt/meituan/apps/rasp-test/perf-test/lib/sandbox-agent.jar na
```
* ab并发发起请求, post.data文件中包括form请求参数比如`a=1&b=2`
```bash
ab -T 'application/x-www-form-urlencoded'  -n 100 -c 10 -p post.data "http://localhost:8080/demo"
```

## 参考

- [greys](https://github.com/oldmanpushcart/greys-anatomy)