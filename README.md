## spring-data-jdbc-splate
#### Spring Data JDBC用のsplate(2Way-SQL)ラッパー

Spring Data JDBCを拡張し、2Way-SQL実行用の追加アノテーションを提供します。

2Way-SQLの解析は、S2JDBC由来の[splate（エス・プレート）](https://mygreen.github.io/splate/)を用いています。

> splate（エス・プレート）は、 2Way-SQL 機能のみを S2JDBC から分離し、使いやすくしたライブラリです。

2Way-SQLの解説は下記ドキュメントを参照してください。

[splate － 2Way-SQLとは](https://mygreen.github.io/splate/2waysql.html)

#### 使い方

[テストケース](src/test/java)も参照してください。

- @EnableJdbcRepositoriesの拡張ポイントに下記

```java
@SpringBootApplication
@EnableJdbcRepositories(repositoryFactoryBeanClass = SplateRepositoryFactoryBean.class)
public class TestApplication {
  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
```

- Repositoryクラスでメソッドを定義し、「@Splate」アノテーションで2-waySQLのパスを指定  
なお、引数名と2Way-SQL内のパラメータ名を一致させてください。

```java
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

  // Listで複数レコード取得
  @Splate("/sql/sampleQuery.sql")
  List<Employee> queryForList(Integer salaryMin, Integer salaryMax);
  
  // Streamで複数レコード取得
  @Splate("/sql/sampleQuery.sql")
  Stream<Employee> queryForStream(Integer salaryMin, Integer salaryMax);

  // 1レコード取得
  @Splate("/sql/sampleQuery2.sql")
  Optional<Employee> querySingle(Long id);
}
```

#### 注意／制約／既知の不具合

- 基礎的なテストケースのみ実施しており、バリエーション等の検討不足による不具合がまだ潜在していると思われます。

- splateの[基本的な使い方](https://mygreen.github.io/splate/howtouse.html)のうち、JavaBeanによるパラメータ指定が未サポートです。

- 1レコード取得の戻り値型は、TではなくOptional<T>を指定してください。  
→現在のバージョンでは、Tのみ指定ではエラーが発生します。

#### 参考URL

[Spring Data JDBCを拡張してみる その1 - クエリを受け取れるメソッドを増やす - 谷本 心 in せろ部屋](https://cero-t.hatenadiary.jp/entry/2022/12/26/051831)

[Spring Data JDBCを拡張してみる その2 - アノテーションでクエリを受け取る - 谷本 心 in せろ部屋](https://cero-t.hatenadiary.jp/entry/2022/12/27/071859)
