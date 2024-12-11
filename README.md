<h1 align="center">Redisson封装工具类</h1>
<p align="center">
  <img src="https://img.shields.io/github/languages/code-size/fzzhuang/redisson-plus-spring-boot-starter" alt="code size"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen" alt="Spring Boot"/>
  <img src="https://img.shields.io/github/languages/count/fzzhuang/redisson-plus-spring-boot-starter" alt="languages"/>
  <img src="https://img.shields.io/badge/Java-17-blue" alt="Java"/>
  <img src="https://img.shields.io/github/last-commit/fzzhuang/redisson-plus-spring-boot-starter" alt="last commit"/><br>
  <img src="https://img.shields.io/badge/Author-fzzhuang-orange" alt="Author" />
</p>
<hr>

## 介绍
Redisson Plus增强组件，通过注解的方式简化了分布式锁、消息队列、发布订阅、缓存等。
实现基于Caffeine+Redisson实现多级缓存，并且支持注解，消息队列更新节点。

- 分布式锁：支持可重入锁、公平锁、读写锁等多种锁类型。
- 消息队列：自动注册消息队列监听器，简化消息消费。
- 发布订阅：实现发布订阅模式，异步处理消息发布和订阅。
- 配置丰富：提供丰富的配置选项，包括连接池、超时设置、哨兵支持等。
- 多级缓存：提供基于注解的多级缓存操作，简化其操作
- 缓存实现：提供基于注解和工具类操作缓存

### 使用

#### pom引入依赖

```xml
<dependency>
    <groupId>cn.fzzhuang</groupId>
    <artifactId>redisson-plus-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 配置
```yaml
redisson:
  address: 127.0.0.1:6379
  cache-prefix: demo

multi:
  cache:
    l1-caches:
      - cache-name: demo
        expire: 300
```
### demo
```java
@Slf4j
@Service
public class UserServiceImp implements UserService {

    /**
     * 消息队列
     */
    @Resource
    private IMessageQueue messageQueue;

    /**
     * 分布式缓存保存数据
     * @param uid 用户ID
     * @return 用户实体, tips:保存数据时必须指定保存的数据类型，比如这里的UserEntity
     */
    @Override
    @RedissonCachePut(prefix = "user", keys = "#uid", timeout = 10)
    public UserEntity getUserInfo(String uid) {
        log.info("save user info by uid: {}", uid);
        return UserEntity.builder()
                .id(1100L)
                .username("yishotech")
                .password("123456")
                .uid("1111012")
                .email("yishotech@gmail.com")
                .ip("江苏宿迁")
                .role(1)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }


    /**
     * 分布式缓存，查询信息&分布式锁使用
     * @param uid 用户ID
     * @return 用户信息
     */
    @Override
    @Lock(name = "user", keys = {"#uid"})
    @RedissonCache(prefix = "user", key = "#uid")
    public UserEntity getUserInfo1(String uid) {
        log.info("query user info by uid: {}", uid);
        return UserEntity.builder().id(111L).username("yishotech").password("123456").uid(uid).build();
    }

    /**
     * 分布式缓存，删除信息
     * @param uid 用户ID
     */
    @Override
    @RedissonCacheEvict(prefix = "user", keys = "#uid")
    public void getUserInfo3(String uid) {
        log.info("delete user by uid: {}", uid);
    }

    /**
     * 分布式缓存，保存数据，类型为Map
     * @param uid 用户Id
     * @param userInfo 用户信息
     * @return 保存的数据类型
     */
    @Override
    @RedissonCachePut(prefix = "user_map", keys = "#uid", type = DataType.MAP)
    public Map<String, Object> saveMap(String uid, UserEntity userInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("userInfo", userInfo);
        return map;
    }

    /**
     * 分布式缓存，查询数据，Map类型
     * @param uid 用户ID
     * @return Map
     */
    @Override
    @RedissonCache(prefix = "user_map", keys = "#uid", type = DataType.MAP)
    public Map<String, Object> getMapInfo(String uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("demo", "暂无消息");
        return map;
    }

    /**
     * 发送消息
     */
    @Override
    public void send() {
        Event<String> event = Event.<String>builder().desc("用户信息").data("1").build();
        messageQueue.sendMessage("user_queue", event);
    }


    /**
     * 发送延迟消息
     */
    @Override
    public void send3() {
        Event<String> event = Event.<String>builder().desc("延迟用户信息").data("延迟").build();
        messageQueue.sendDelayMessage("user_delay", event, 2, TimeUnit.SECONDS);
    }

    /**
     * 多级缓存，保存消息
     * @param uid 用户ID
     * @return 消息类型
     */
    @Override
    @MultiCachePut(cacheName = "user", prefix = "demo", keys = "#uid")
    public Map<String, Object> setCache(String uid) {
        log.info("multi cache set user by uid: {}", uid);
        UserEntity userEntity = UserEntity.builder()
                .id(1101L)
                .username("yishotech")
                .password("123456")
                .uid("1111012")
                .email("yishotech@gmail.com")
                .ip("江苏:宿迁")
                .role(1)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("userInfo", userEntity);
        return map;
    }

    /**
     * 多级缓存，获取数据
     * @param uid 用户ID
     * @return 数据类型
     */
    @Override
    @MultiCache(cacheName = "user", prefix = "demo", keys = "#uid")
    public Map<String, Object> getCache(String uid) {
        log.info("multi cache get user by uid: {}", uid);
        return null;
    }

    /**
     * 多级缓存，删除数据
     * @param uid 用户Id
     */
    @Override
    @MultiCacheEvict(cacheName = "user", prefix = "demo", keys = "#uid")
    public void delCache(String uid) {
        log.info("multi cache delete user by uid: {}", uid);
    }
}
```
消息订阅demo
```java
@Slf4j
@Component
@RedissonListener(topic = "user_queue")
public class UserConsumer implements MessageListener<Event<String>> {

    @Override
    public void onMessage(CharSequence charSequence, Event<String> event) {
        log.info("接收到消息:{}", JSON.toJSONString(event));
        String data = event.getData();
        String desc = event.getDesc();
        log.info("data:{} desc:{}", data, desc);
    }
}
```
延迟消息监听demo
```java
@Slf4j
@Component
public class UserDelayConsumer implements CommandLineRunner {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(String... args) {
        // 异步线程，监听延时队列
        new Thread(() -> {
            RBlockingDeque<Object> userDelay = redissonClient.getBlockingDeque("user_delay");
            while (true) {
                try {
                    Object take = userDelay.take();
                    Event<String> event = (Event<String>) take;
                    log.info("take {}", event.getData());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
```

**注意:** 使用注解时，需要在pom.xml中配置 **-parameters**
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                    <encoding>UTF-8</encoding>
                    <compilerArgs>
                        <arg>-parameters</arg>
                    </compilerArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
## 许可证

根据 License 许可证分发。打开 [LICENSE](LICENSE) 查看更多内容。
