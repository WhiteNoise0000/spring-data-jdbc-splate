[![Java CI with Gradle](https://github.com/WhiteNoise0000/spring-data-jdbc-splate/actions/workflows/gradle.yml/badge.svg)](https://github.com/WhiteNoise0000/spring-data-jdbc-splate/actions/workflows/gradle.yml)
# spring-data-jdbc-splate
## Spring Data JDBC用のsplate(2Way-SQL)ラッパー

Spring Data JDBCを拡張し、2Way-SQL実行用の追加アノテーションを提供します。

2Way-SQLの解析は、S2JDBC由来の[splate（エス・プレート）](https://mygreen.github.io/splate/)を用いています。

> splate（エス・プレート）は、 2Way-SQL 機能のみを S2JDBC から分離し、使いやすくしたライブラリです。

2Way-SQLの解説は下記ドキュメントを参照してください。

[splate － 2Way-SQLとは](https://mygreen.github.io/splate/2waysql.html)

## 使い方

- 依存関係を追加

```gradle
dependencies {
  implementation 'io.github.whitenoise0000:spring-data-jdbc-splate:0.1.1'
}
```


[テストケース](src/test/java/test)も参照してください。

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

## 動作確認済み環境

本リポジトリのビルド・テストは、以下の構成で確認しています（`./gradlew test` 9件成功）。

| 項目 | バージョン |
| --- | --- |
| JDK | 17 |
| Spring Boot | 3.5.2 |
| Spring Data JDBC | Spring Boot 3.5.2 が管理するバージョン（`spring-boot-starter-data-jdbc` 経由） |
| splate | 0.3 |

ローカル動作確認は `src/test/java/test/EmployeeRepositoryTest.java` および `src/test/resources/sql/*.sql` の通りです。実プロジェクトに組み込む際は、上記と利用者環境の差分に応じて動作検証を行ってください。

## 互換性に関する注意

本ライブラリは Spring Data JDBC の以下の public API に依存して実装されています。

- `org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean`
- `org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory`
- `org.springframework.data.jdbc.core.convert.EntityRowMapper`
- `org.springframework.data.jdbc.core.convert.JdbcConverter`
- `org.springframework.data.repository.core.support.RepositoryFactorySupport`
- `org.springframework.data.repository.query.QueryLookupStrategy`

上記は public として公開されている API ですが、Spring Data JDBC のマイナーバージョン更新で API 形状が予告なく変更される可能性があります。Spring Boot もしくは Spring Data JDBC のメジャーバージョン（例：3.x → 4.x）へ更新する際は、本リポジトリの `./gradlew build`／`./gradlew test` を再実行して互換性を確認してください。

## 注意／制約／既知の不具合

- 最低限のテストケースのみであり、バリエーション検討不足による<span style="color: red; ">不具合がまだ潜在している</span>と思われます。

- splateの[基本的な使い方](https://mygreen.github.io/splate/howtouse.html)のうち、JavaBeanによるパラメータ指定は未サポートです。

- 1レコード取得の戻り値型に`Optional<T>`ではなく`T`を指定する場合、クラス可視性はpublicとしてください。  
→`T`のみ指定かつ可視性がpublicではない場合、`IllegalAccessError`が発生します。これはSpring Data JDBC本体も同様です。参考：[Issue #2](https://github.com/WhiteNoise0000/spring-data-jdbc-splate/issues/2)

- OSSライブラリ公開の経験等無く、様々なお作法に疎いので参考にとどめてください。

## 参考URL

本リポジトリ内のコードは、下記記事内のコードを<span style="color: gray; ">~~ほぼパクリ~~</span>参考にしています。

[Spring Data JDBCを拡張してみる その1 - クエリを受け取れるメソッドを増やす - 谷本 心 in せろ部屋](https://cero-t.hatenadiary.jp/entry/2022/12/26/051831)

[Spring Data JDBCを拡張してみる その2 - アノテーションでクエリを受け取る - 谷本 心 in せろ部屋](https://cero-t.hatenadiary.jp/entry/2022/12/27/071859)

[Proof of Concept - Spring Data SQL](https://github.com/cero-t/poc-data-sql)
