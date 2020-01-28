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
1. 访问`http://localhost:8080`
1. 观察java-agent-test spring Boot项目运行日志
```text
22:27:57.683 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.util.SandboxProtector - thread:Thread[Attach Listener,9,system] enter protect:0
22:27:57.832 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.util.SandboxProtector - thread:Thread[Attach Listener,9,system] exit protect:0 with clean
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#<init>(org.eclipse.jetty.util.thread.ThreadPool) for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#<init>(java.net.InetSocketAddress) for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#<init>(int) for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#<init>() for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#<clinit>() for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#main(java.lang.String[]) for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#toString() for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#join() for listener[id=1000];
22:27:57.855 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#start(org.eclipse.jetty.util.component.LifeCycle) for listener[id=1000];
22:27:57.856 [Attach Listener] INFO com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - rewrite method org.eclipse.jetty.server.Server#handle(org.eclipse.jetty.server.HttpChannel) for listener[id=1000];event=BEFORE;
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getAttributeNames() for listener[id=1000];
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#removeAttribute(java.lang.String) for listener[id=1000];
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#dump(java.lang.Appendable,java.lang.String) for listener[id=1000];
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getURI() for listener[id=1000];
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getErrorHandler() for listener[id=1000];
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setErrorHandler(org.eclipse.jetty.server.handler.ErrorHandler) for listener[id=1000];
22:27:57.873 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getAttribute(java.lang.String) for listener[id=1000];
22:27:57.874 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setAttribute(java.lang.String,java.lang.Object) for listener[id=1000];
22:27:57.874 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getVersion() for listener[id=1000];
22:27:57.874 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#doStop() for listener[id=1000];
22:27:57.920 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#doStart() for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getRequestLog() for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setRequestLog(org.eclipse.jetty.server.RequestLog) for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getStopAtShutdown() for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setStopTimeout(long) for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setStopAtShutdown(boolean) for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getConnectors() for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#addConnector(org.eclipse.jetty.server.Connector) for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#removeConnector(org.eclipse.jetty.server.Connector) for listener[id=1000];
22:27:57.922 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setConnectors(org.eclipse.jetty.server.Connector[]) for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getThreadPool() for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#isDumpAfterStart() for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setDumpAfterStart(boolean) for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#isDumpBeforeStop() for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setDumpBeforeStop(boolean) for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getDateField() for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#handleOptions(org.eclipse.jetty.server.Request,org.eclipse.jetty.server.Response) for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#handleAsync(org.eclipse.jetty.server.HttpChannel) for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#getSessionIdManager() for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#setSessionIdManager(org.eclipse.jetty.server.SessionIdManager) for listener[id=1000];
22:27:57.923 [Attach Listener] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.asm.EventWeaver - non-rewrite method org.eclipse.jetty.server.Server#clearAttributes() for listener[id=1000];
22:27:57.927 [Attach Listener] INFO com.alibaba.jvm.sandbox.core.enhance.EventEnhancer - dump org/eclipse/jetty/server/Server to ./sandbox-class-dump/org/eclipse/jetty/server/Server.class success.
22:27:57.927 [Attach Listener] INFO com.alibaba.jvm.sandbox.core.manager.impl.SandboxClassFileTransformer - transform org/eclipse/jetty/server/Server finished, in loader=sun.misc.Launcher$AppClassLoader@18b4aac2
22:27:57.939 [Attach Listener] INFO com.alibaba.jvm.sandbox.core.manager.impl.InstrumentManager - transform classes: [class org.eclipse.jetty.server.Server]
22:27:57.941 [Attach Listener] INFO com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler - activated listener[id=1000;target=com.alibaba.jvm.sandbox.core.manager.impl.InstrumentManager$1@7f15b67f;] event=BEFORE
22:28:19.230 [qtp778162712-25] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventProcessor - push process-stack, process-id=1000;invoke-id=1000;deep=1;listener=1000;
22:28:19.230 [qtp778162712-25] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler - on-event: event|BEFORE|1000|1000|1000
enter org.eclipse.jetty.server.Server.handle event = [com.alibaba.jvm.sandbox.core.api.event.BeforeEvent@58567ade]
22:28:19.320 [qtp778162712-25] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventProcessor - pop process-stack, process-id=1000;invoke-id=1000;deep=0;listener=1000;
22:28:19.320 [qtp778162712-25] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventProcessor - clean TLS: event-processor, listener=1000;
22:28:19.354 [qtp778162712-28] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventProcessor - push process-stack, process-id=1001;invoke-id=1001;deep=1;listener=1000;
22:28:19.354 [qtp778162712-28] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventListenerHandler - on-event: event|BEFORE|1001|1001|1000
enter org.eclipse.jetty.server.Server.handle event = [com.alibaba.jvm.sandbox.core.api.event.BeforeEvent@3c47afc4]
22:28:19.362 [qtp778162712-28] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventProcessor - pop process-stack, process-id=1001;invoke-id=1001;deep=0;listener=1000;
22:28:19.363 [qtp778162712-28] DEBUG com.alibaba.jvm.sandbox.core.enhance.weaver.EventProcessor - clean TLS: event-processor, listener=1000;
```

## 参考

- [greys](https://github.com/oldmanpushcart/greys-anatomy)