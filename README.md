# spring-data-jdbc-splate
## Spring Data JDBC用のsplate(2Way-SQL)ラッパー

Spring Data JDBCを拡張し、2Way-SQL実行用の追加アノテーションを提供します。

2Way-SQLの解析は、S2JDBC由来の[splate（エス・プレート）](https://mygreen.github.io/splate/)を用いています。

> splate（エス・プレート）は、 2Way-SQL 機能のみを S2JDBC から分離し、使いやすくしたライブラリです。

2Way-SQLの解説は下記ドキュメントを参照してください。

[splate － 2Way-SQLとは](https://mygreen.github.io/splate/2waysql.html)

## 使い方

[テストケース](src/test/java/com/github/whitenoise0000/springdatajdbcsplate)も参照してください。

- `@EnableJdbcRepositories`の拡張ポイントに`SplateRepositoryFactoryBean`を指定

```java
@SpringBootApplication
@EnableJdbcRepositories(repositoryFactoryBeanClass = SplateRepositoryFactoryBean.class)
public class TestApplication {
  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }
}
```

- Repositoryクラスでメソッドを定義し、`@Splate`アノテーションで2-waySQLのパスを指定  
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

## 注意／制約／既知の不具合

- 最低限のテストケースのみであり、バリエーション検討不足による<span style="color: red; ">不具合がまだ潜在している</span>と思われます。

- splateの[基本的な使い方](https://mygreen.github.io/splate/howtouse.html)のうち、JavaBeanによるパラメータ指定は未サポートです。

- 1レコード取得の戻り値型に`Optional<T>`ではなく`T`を指定する場合、クラス可視性はpublicとしてください。  
→`T`のみ指定かつ可視性がpublicではない場合、Issue #2に記載のエラーが発生します。

- OSSライブラリ公開の経験等無く、様々なお作法に疎いので参考にとどめてください。

## 参考URL

本リポジトリ内のコードは、下記記事内のコードを<span style="color: gray; ">~~ほぼパクリ~~</span>参考にしています。

[Spring Data JDBCを拡張してみる その1 - クエリを受け取れるメソッドを増やす - 谷本 心 in せろ部屋](https://cero-t.hatenadiary.jp/entry/2022/12/26/051831)

[Spring Data JDBCを拡張してみる その2 - アノテーションでクエリを受け取る - 谷本 心 in せろ部屋](https://cero-t.hatenadiary.jp/entry/2022/12/27/071859)

[Proof of Concept - Spring Data SQL](https://github.com/cero-t/poc-data-sql)
