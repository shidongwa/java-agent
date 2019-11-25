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