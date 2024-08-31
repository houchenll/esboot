# esboot
详细讲解在 MacOS 上如何在 SpringBoot 中集成 ElasticSearch，并展示模糊匹配查询实现。

代码对应文章：https://juejin.cn/post/7408816726028107816

## 条件
- 电脑：macOs
- SpringBoot: 3.3.3
- 本机ES: 8.14.3

## 添加 ES 依赖
在 `pom.xml` 中添加以下 `elasticSearch` 依赖，注意版本指定为和 springBoot 相同。
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        <version>3.3.3</version>
    </dependency>
</dependencies>
```

## 添加 ES 配置
预先启动本地ES。

在 `application.yml` 中添加如下配置。

```yml
spring:
  elasticsearch:
    uris: https://localhost:9200    # Elasticsearch 服务器地址
    username: elastic               # 用户名
    password: VwpZZnl012345zwfG8B  # 密码
```

注意：uris 使用 https。

如果使用项目中 `docker-compose.yml` 启动项目，且指定禁用安全校验，则上面配置不需要 `username` 和 `password` 两项，且 `uris` 中的端口需要对应修改为 `9920`，且 https 换成 `http`，即 `http://localhost:9920`。注意，如果 docker 启动失败，需要先删除相关的 docker container。

如果本地ES有 cert 校验，则运行之前，需先把ES证书添加到java sdk的密钥库中，步骤如下：
```bash
# 进入 java sdk 证书所在位置
cd /Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/lib/security
# 执行以下命令，将 ES 的 ca 证书添加到 java sdk 的密钥库中，且取别名为 es_http_ca
sudo keytool -keystore cacerts -importcert -alias "es_http_ca" -file ~/env/elasticsearch-8.14.3/config/certs/http_ca.crt
```
安装证书时，当需要输入密钥库口令时，使用默认的 `changeit`。当询问是否信任此证书时，输入 `y` 并按回车。然后证书就会被添加到密钥库中。

如果不知道本机 jdk 安装位置，可以使用以下命令获取：
```bash
# 查看 java 命令位置
which java
# 查看 java 版本
/usr/bin/java --version
# 查看 java 实际安装位置
/usr/libexec/java_home -v 17
```

## 定义 ES 文档对应 bean 文件
假设 ES 索引中文档有 `text` 和 `title` 字段，则定义如下 Bean 文件，将 es document 中的字段和 Bean 中的属性对应起来。

```java
@Document(indexName = "poem-index")
@Data
public class Poem {

    // 对应 document._id，为字符串
    @Id
    private String id;

    // 对应 document._source['title']
    @Field(type = FieldType.Text)
    private String title;

    // 对应 document._source['text']
    @Field(type = FieldType.Text)
    private String text;

}
```

## 定义数据操作类
```java
public interface PoemRepository extends ElasticsearchRepository<Poem, String> {
    // 根据text字段模糊匹配
    @Query("{"match": {"text": {"query": "?0"}}}")
    List<Poem> findByText(String text);

    // 根据text和title字段模糊匹配,fuzziness使用"AUTO" 使查询支持模糊匹配，根据查询词的长度自动决定模糊度
    @Query("{"multi_match": {"query": "?0", "fields": ["text", "title"], "fuzziness": "AUTO"}}")
    List<Poem> findByTextOrTitle(String text);
}
```

## 定义 service 和 controller

Service
```java
@Service
public class PoemService {

    @Autowired
    private PoemRepository poemRepository;

    public Iterable<Poem> searchAll() {
        return poemRepository.findAll();
    }

    public List<Poem> searchPosts(String query) {
        return poemRepository.findByTextOrTitle(query);
    }

}
```

Controller
```java
@RestController
@RequestMapping("hello")
public class HelloController {

    @Autowired
    private PoemService poemService;

    @GetMapping("/search/all")
    public Iterable<Poem> searchAll() {
        return poemService.searchAll();
    }

    @GetMapping("/search")
    public List<Poem> search(@RequestParam(name = "query") String query) {
        return poemService.searchPosts(query);
    }

}
```
