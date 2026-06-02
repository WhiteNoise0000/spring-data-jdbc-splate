# Release Procedure

## リリース前確認

```bash
./gradlew clean test
./gradlew build
git diff --check
```

## リリース手順

1. `build.gradle` の `mavenPublishing.coordinates` のバージョンを更新する
   - 例: `'0.1.1'` → `'0.1.2'`

2. READMEの依存関係例も同じバージョンへ更新する
   - `implementation 'io.github.whitenoise0000:spring-data-jdbc-splate:0.1.2'`

3. `CHANGELOG.md` の対象バージョンの日付を `TBD` から確定日付に変更する

4. 上記をコミットし、mainブランチへマージする

5. GitHub上で main ブランチから GitHub Release を作成する
   - タグには `v` プレフィックス付きのバージョンを指定（例: `v0.1.2`）
   - Release title にはバージョン番号を記載（例: `v0.1.2`）
   - CHANGELOG の内容を参考にリリースノートを記入する

6. Release 作成イベントにより `.github/workflows/gradle-publish.yml` が発火し、Maven Central へ自動公開される

## シークレット情報

`.github/workflows/gradle-publish.yml` では以下のシークレットを使用しています（値は GitHub Secrets 経由で渡すこと）：

- `MAVEN_USERNAME`
- `MAVEN_PASSWORD`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`
- `SIGNING_KEY_ID`

## 公開後確認

- Maven Central の [Central Portal](https://central.sonatype.com/) で反映を確認する
- 反映までに数時間〜1日程度かかる場合がある
- 公開後、該当バージョンの `./gradlew test` が正常に動作することを改めて確認する
