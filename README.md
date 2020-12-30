<center>
    <h1>rainbow-security-boot-starter</h1>
    <h2>一款基于springboot+redis的权限框架</h2>
</center>

## 1.简介

​		对比`java`市场上的两大安全框架`shiro`和`spring security`，rainbow-security-boot-starter的设计初衷就是为了让权限框架使用的更为简单，快速应用。

## 2. 如何使用

> 实现声明：rainbow-security-boot-starter不会去控制你登录的业务逻辑，你只需要判断用户信息一切准确无误的时候，调用RainbowSecurityUtils下的方法即可。

### 2.1 配置yaml文件

```yaml
rainbow:
  security:
    is-read-body: false # 是否尝试从请求体里读取token
    is-read-cookie: false # 是否尝试从cookie里读取token
    timeout: 172800 # token有效期，单位s 默认1天
    token-name: rainbowSecurity # token名称 (同时也是cookie名称)
    is-read-head: false # 是否尝试从header里读取token
    is-share: false # 在多人登录同一账号时，是否共享会话 (为true时共用一个，为false时新登录挤掉旧登录)
```



### 2.2 执行登陆方法

> 如果 is-share为true的时候，如果A已经登陆了账号a并拿到了tokenA，这时候如果B也登陆了账号a，那么他也将返回tokenA
>
> 如果 is-share为false的时候，如果A已经登陆了账号a并拿到了tokenA，这时候如果B也登陆了账号a，那么tokenA作废，B则返回tokenB

```java
// loginId是用户唯一标识，token为登陆之后返回的令牌
String token = RainbowSecurityUtils.login(loginId);
```



### 2.3 将用户的一些个人信息放入到缓存中

```java
// loginId是用户唯一标识，sysUser为用户的个人信息
RainbowSecurityUtils.setDataByLoginID(loginId, sysUser);
```



### 2.4 根据用户唯一标识获取缓存中对应的信息

```java
// 获取loginID对应的缓存中的个人信息
Object sysUser = RainbowSecurityUtils.getDataByLoginID(loginId);
```



### 2.5 获取请求接口中的token

> 根据yml中配置的如何获取请求接口中的token来获取

```java
String token = RainbowSecurityUtils.getRequestToken();
```



### 2.6 根据用户唯一标识loginId来获取他对应的token

> 如果loginId获取

### 2.3 实现权限



## 3. 常见BUG

### 3.1 redis存入的数据被序列化了，无法查看

> 这是使用了阿里的fastjson进行反序列化，市场上还有很多种实现方法，这里就不一一列举了

1. pom 坐标

   ```xml
           <!-- 阿里JSON解析器 -->
           <dependency>
               <groupId>com.alibaba</groupId>
               <artifactId>fastjson</artifactId>
               <version>${fastjson.version}</version>
           </dependency>
   ```

   

2. 实现RedisSerializer接口

   ```java
   import com.alibaba.fastjson.JSON;
   import com.alibaba.fastjson.parser.ParserConfig;
   import com.alibaba.fastjson.serializer.SerializerFeature;
   import com.fasterxml.jackson.databind.JavaType;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import com.fasterxml.jackson.databind.type.TypeFactory;
   import org.springframework.data.redis.serializer.RedisSerializer;
   import org.springframework.data.redis.serializer.SerializationException;
   import org.springframework.util.Assert;
   
   import java.nio.charset.Charset;
   
   /**
    * Redis使用FastJson序列化
    *
    * @author ben
    */
   public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
   
       @SuppressWarnings("unused")
       private ObjectMapper objectMapper = new ObjectMapper();
   
       public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
   
       private Class<T> clazz;
   
       static {
           ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
       }
   
       public FastJson2JsonRedisSerializer(Class<T> clazz) {
           super();
           this.clazz = clazz;
       }
   
       @Override
       public byte[] serialize(T t) throws SerializationException {
           if (t == null) {
               return new byte[0];
           }
           return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
       }
   
       @Override
       public T deserialize(byte[] bytes) throws SerializationException {
           if (bytes == null || bytes.length <= 0) {
               return null;
           }
           String str = new String(bytes, DEFAULT_CHARSET);
   
           return JSON.parseObject(str, clazz);
       }
   
       public void setObjectMapper(ObjectMapper objectMapper) {
           Assert.notNull(objectMapper, "'objectMapper' must not be null");
           this.objectMapper = objectMapper;
       }
   
       protected JavaType getJavaType(Class<?> clazz) {
           return TypeFactory.defaultInstance().constructType(clazz);
       }
   }
   
   ```

   

3. 设置RedisTemplate的序列化方式

   ```java
   import com.fasterxml.jackson.annotation.JsonAutoDetect;
   import com.fasterxml.jackson.annotation.PropertyAccessor;
   import com.fasterxml.jackson.databind.ObjectMapper;
   import com.rainbow.security.config.FastJson2JsonRedisSerializer;
   import org.springframework.cache.annotation.CachingConfigurerSupport;
   import org.springframework.cache.annotation.EnableCaching;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.data.redis.connection.RedisConnectionFactory;
   import org.springframework.data.redis.core.RedisTemplate;
   import org.springframework.data.redis.serializer.StringRedisSerializer;
   
   /**
    * redis配置
    *
    * @author ben
    */
   @Configuration
   @EnableCaching
   public class RedisConfig extends CachingConfigurerSupport {
   
       @Bean
       @SuppressWarnings(value = {"unchecked", "rawtypes"})
       public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
           RedisTemplate<Object, Object> template = new RedisTemplate<>();
           template.setConnectionFactory(connectionFactory);
   
           com.rainbow.security.config.FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);
   
           ObjectMapper mapper = new ObjectMapper();
           mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
           mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
           serializer.setObjectMapper(mapper);
   
           template.setValueSerializer(serializer);
           // 使用StringRedisSerializer来序列化和反序列化redis的key值
           template.setKeySerializer(new StringRedisSerializer());
           template.afterPropertiesSet();
           return template;
       }
   
   }
   ```

   