# Gemini CLI Instructions for Compose Multiplatform Project

## プロジェクト概要 & 技術スタック

このプロジェクトは、Compose Multiplatformを使用してAndroidおよびiOS向けのクロスプラットフォームアプリケーションを開発することを目的としています。

## 技術スタック

- **言語** Kotlin
- **UIフレームワーク** Jetpack Compose / Compose Multiplatform
- **ビルドツール** Gradle
- **バージョン管理** Git

## コーディングルール

### 共通

- **プラットフォーム依存の回避**：可能な限り、`composeApp/src/commonMain/`ディレクトリ内でコードを記述し、プラットフォーム依存のコードは`androidMain`および`iosMain`ディレクトリ内に記述すること
- **expect/actual**：プラットフォーム固有のAPIが必要な場合のみ、`expect`/`actual`キーワードを使用して実装し安易な乱用は避けてください
- **Javaライブラリの禁止**：`java.*`パッケージの仕様は禁止し、Kotlin純正の標準ライブラリを使用してください

### UI(Compose)

- **State Hoisting**：UIの状態を適切に分離し、コンポーネント間で状態を共有する際は、`state hoisting`パターンを活用すること
- **Unidirectional Data Flow**：UIのデータフローを一方向に保つことで、状態管理を容易にし、予測可能なアプリケーションを構築すること。
- **Modifier**：UIコンポーネントには必ず`modifier: Modifier = Modifier`を引数として受け取り、外部からのスタイリングやレイアウト調整を可能にすること。
- **Preview**：Composeの`@Preview`アノテーションを活用して、UIコンポーネントのプレビューを積極的に作成すること。

### ロジック & 非同期処理

- **Immutability**：変数は可能な限り`val`で宣言し、ミュータブルな状態は`MutableStateFlow`などで管理してください。
- **Coroutines**：スレッドブロックを避け、`suspend`関数を使用してください。`GlobalScope`の使用は避け、適切な`viewModelScope`や`lifecycleScope`を利用してください。

## ディレクトリ構成

- コードの生成・修正は適切なソースセットを意識してください。

* `composeApp`
  - `/composeApp/src/commonMain`: 共通コード
  - `/composeApp/src/androidMain`: Android固有コード
  - `/composeApp/src/iosMain`: iOS固有コード
  - `/composeApp/src/commonTest`: 共通テストコード

* `limirepi-android`
  - Androidアプリケーション固有コード

* `limirepi-ios`
  - iOSアプリケーション固有コード

## テスト

- 共通コードは`composeApp/src/commonTest/`にテストコードを配置してください
- プラットフォーム固有のテストコードはそれぞれのソースセットに配置してください
- モックが必要な場合は、`MockK`ライブラリなどを適切に使用してください

## ライブラリ

- 有用と判断したライブラリを導入する場合は、そのライブラリの導入方法を必ずコメントで記載してください。
- ライブラリの導入にはプロジェクトのビルド時間や依存関係の複雑化を考慮し、必要最低限に留めてください。
- ライブラリを導入する際は最新のバージョンを使用してください。

## import文

- コードの先頭に必要なimport文を明示的に記載してください。特に、Kotlin標準ライブラリやCompose関連のimportは省略せずに記載してください。

## 回答

- **言語**：日本語で回答してください。
- **コードブロック**：生成するコードは必ずファイルパス(例：`composeApp/src/commonMain/kotlin/com/example/MyFile.kt`)を明記してください。
- **プラットフォームの優先順位**：質問に指定がない限り、Android/iOS両方で動作する`commonMain`向けのコードを優先して提示してください。
- **補足説明**：必要に応じて複雑な処理の場合などはコードの後に補足説明を追加してください。
- **最新情報**：非推奨なAPIの使用を避け、可能な限り最新の推奨される方法でコードを生成してください。
- **回答**：READMEの生成は行わずに、コードの生成や修正に集中してください。ユーザーから必要な情報が提供された場合にのみ作成してください。
