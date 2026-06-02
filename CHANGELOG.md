# Changelog

## Unreleased

### Added

- `@Splate` メソッドで単一JavaBean引数をサポート
  - JavaBeans getterプロパティがそのまま2Way-SQL内のバインドパラメータ名として参照可能
  - 既存SQL（`sampleQuery.sql` / `sampleCount.sql` / `sampleInsert.sql` 等）をそのまま流用できる
  - `null` の単一JavaBean引数は `IllegalArgumentException` を投げる
  - 対象外: 複数Bean引数 / `Map` / `Collection` / `Iterable` / `Optional` / 配列引数の展開 / ネストしたBeanプロパティ

### Changed

- READMEの依存関係例修正
- 動作確認済み環境と互換性注意の追記
- build workflowから不要な `Restore gradle.properties` step を削除
- 既存Splateクエリの条件バリエーションテスト追加
- README冒頭にライブラリの利用意図を追記
- READMEにJavaBean引数の使い方と対応範囲を追記

### Notes

- Spring Boot 4.x対応は未検証です
- 次のMaven Central公開は、機能追加を含む `v0.2.0` を想定しています

## 0.1.1

- Maven Central公開済みの現行バージョン
