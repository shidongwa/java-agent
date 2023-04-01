### spring boot app error "target/java-agent-test-1.0.0-SNAPSHOT.jar中没有主清单属性"

```xml
    <!-- Package as an executable jar -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.7.9</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

### start with premain java agent
```
-javaagent:/Users/dong/git-project/java-agent/java-agent-object-size/target/java-agent-object-size-1.0-SNAPSHOT.jar
```

e.g. attach object size premain agent
```shell
java -javaagent:/Users/dong/git-project/java-agent/java-agent-object-size/target/java-agent-object-size-1.0-SNAPSHOT.jar -jar java-agent-test/target/java-agent-test-1.0.0-SNAPSHOT.jar
```

attach greys-agent
```shell
java -javaagent:/Users/dong/git-project/java-agent/greys-agent/target/greys-agent.jar="/Users/dong/git-project/java-agent/greys-core/target/greys-core.jar;" -jar java-agent-test/target/java-agent-test-1.0.0-SNAPSHOT.jar
```

attach with gc log, stw log
```shell
java -javaagent:/Users/dong/git-project/java-agent/greys-agent/target/greys-agent.jar="/Users/dong/git-project/java-agent/greys-core/target/greys-core.jar;" -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintGCApplicationStoppedTime -jar java-agent-test/target/java-agent-test-1.0.0-SNAPSHOT.jar
```



