# システム概要

このコードは Spring Boot で構成された **プログラム教育用ECサイト**です。
会員制のECサイトで、**一般ユーザー向け機能（購入者）** と **管理者向け機能** を備えています。

- アクセス先: `http://localhost:55000/shared_shop`
- セッションタイムアウト: 120秒

技術構成：

* Spring Boot 4.0.6
* Spring MVC
* Spring Data JPA
* Thymeleaf
* Oracle Database（ojdbc11 / Oracle 21c: `jdbc:oracle:thin:@localhost:1521/xepdb1`）
* Bean Validation（`spring-boot-starter-validation`）
* Maven
* Java 17

---

# 主な機能

## 1. ユーザー機能

### 会員管理

* 新規会員登録
* ログイン／ログアウト
* 会員情報変更
* 退会

関連Controller

* `ClientUserRegistController`
* `ClientUserShowController`
* `ClientUserUpdateController`
* `ClientUserDeleteController`
* `LoginController`
* `LogoutController`

---

### 商品閲覧

* 商品一覧表示（新着順・価格順・人気順）
* 商品詳細表示
* カテゴリ別絞り込み
* 人気商品表示（注文回数集計）

関連Controller

* `ClientItemShowController`

---

### カート機能

* 商品をカートへ追加
* カート内商品一覧表示
* 商品削除

関連Controller

* `ClientBasketController`

---

### 注文機能

* 配送先入力
* 支払方法選択（デフォルト: クレジットカード）
* 注文確認
* 注文確定
* 注文履歴表示
* 注文詳細表示

関連Controller

* `ClientOrderRegistController`
* `ClientOrderShowController`

---

## 2. 管理者機能

管理画面（`/admin/menu`）から各マスタを管理できます。
権限は3段階（`AUTH_SYSTEM=0` / `AUTH_ADMIN=1` / `AUTH_CLIENT=2`）で管理しています。

---

### 商品管理

* 商品一覧
* 商品登録
* 商品編集
* 商品削除
* 商品画像アップロード（`images/` フォルダに日時付きファイル名で保存）

関連Controller

* `AdminItemShowController`
* `AdminItemRegistController`
* `AdminItemUpdateController`
* `AdminItemDeleteController`

---

### カテゴリ管理

* カテゴリ一覧
* カテゴリ登録
* カテゴリ編集
* カテゴリ削除

関連Controller

* `AdminCategoryShowController`
* `AdminCategoryRegistController`
* `AdminCategoryUpdateController`
* `AdminCategoryDeleteController`

---

### 会員管理

* 会員一覧
* 会員登録
* 会員編集
* 会員削除

関連Controller

* `AdminUserShowController`
* `AdminUserRegistController`
* `AdminUserUpdateController`
* `AdminUserDeleteController`

---

### 注文管理

* 注文一覧
* 注文詳細確認

関連Controller

* `AdminOrderShowController`

---

# データベース構造

主要テーブルは5つです。

| テーブル       | 内容       | シーケンス        |
| ----------- | --------- | --------------- |
| users       | 会員情報    | seq_users       |
| categories  | 商品カテゴリ  | seq_categories  |
| items       | 商品情報    | seq_items       |
| orders      | 注文情報    | seq_orders      |
| order_items | 注文明細    | seq_order_items |

削除フラグ（`delete_flag`）で論理削除を管理しています（`NOT_DELETED=0` / `DELETED=1`）。

---

# エンティティ関係

```text
User
 └─ Order
      └─ OrderItem
           └─ Item
                └─ Category
```

### 関係

* 1人の会員 → 複数注文（`@OneToMany`）
* 1注文 → 複数注文明細（`@OneToMany`）
* 1注文明細 → 1商品（`@ManyToOne`）
* 1カテゴリ → 複数商品（`@OneToMany`）

---

# パッケージ構成

```text
jp.co.sss.shop/
├── annotation/        カスタムバリデーションアノテーション
│   ├── CategoryCheck.java
│   ├── EmailCheck.java
│   ├── ItemCheck.java
│   └── LoginCheck.java
├── bean/              View表示・セッション管理用Bean
│   ├── BasketBean.java
│   ├── CategoryBean.java
│   ├── ItemBean.java
│   ├── OrderBean.java
│   ├── OrderItemBean.java
│   └── UserBean.java
├── config/            フィルター登録・アプリケーション設定
│   ├── FilterConfig.java
│   └── SharedShopConfig.java
├── controller/        MVCコントローラ
│   ├── ErrorController.java
│   ├── admin/
│   │   ├── category/  AdminCategoryXxxController
│   │   ├── item/      AdminItemXxxController
│   │   ├── order/     AdminOrderShowController
│   │   └── user/      AdminUserXxxController
│   ├── client/
│   │   ├── item/      ClientItemShowController
│   │   │   └── basket/ ClientBasketController
│   │   ├── order/     ClientOrderXxxController
│   │   └── user/      ClientUserXxxController
│   └── login/
│       ├── LoginController.java
│       └── LogoutController.java
├── entity/            JPAエンティティ
│   ├── Category.java
│   ├── Item.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── User.java
├── filter/            サーブレットフィルタ（認証・認可・共通処理）
│   ├── AdminAccountCheckFilter.java      運用管理者権限チェック
│   ├── CategoryListMakeFilter.java       サイドバー用カテゴリリスト生成
│   ├── ClientAccountCheckFilter.java     一般会員権限チェック
│   ├── LoginCheckFilter.java             未ログインアクセス制御
│   └── SystemAdminAccountCheckFilter.java システム管理者権限チェック
├── form/              入力フォームクラス
│   ├── CategoryForm.java
│   ├── ItemForm.java
│   ├── LoginForm.java
│   ├── OrderForm.java
│   └── UserForm.java
├── repository/        Spring Data JPAリポジトリ
│   ├── CategoryRepository.java
│   ├── ItemRepository.java
│   ├── OrderItemRepository.java
│   ├── OrderRepository.java
│   └── UserRepository.java
├── service/           共通サービス
│   ├── BeanTools.java          Entity⇔Bean間のフィールドコピー
│   ├── PriceCalc.java          小計・合計金額計算
│   └── UploadFileService.java  商品画像アップロード処理
├── util/              ユーティリティ
│   ├── Constant.java           定数定義
│   └── URLCheck.java           URLパターン判定
└── validator/         カスタムバリデーター実装
    ├── CategoryValidator.java
    ├── EmailValidator.java
    ├── ItemValidator.java
    └── LoginValidator.java
```

---

# 特徴的な実装

## 人気商品ランキング

`ItemRepository` にて注文明細（`order_items`）を集計し、購入回数の多い順に商品を取得しています。

```java
// 購入回数の多い商品を降順で取得するJPQLクエリの例
SELECT i FROM Item i
JOIN OrderItem oi ON oi.item.id = i.id
GROUP BY i
ORDER BY COUNT(i.id) DESC
```

---

## 画像アップロード

`UploadFileService` にて商品画像を管理します。

* アップロード先: プロジェクト直下の `images/` フォルダ
* ファイル名に登録日時を付与して重複を防止

```text
例: 20260617103015_apple.jpg
```

---

## 料金計算

`PriceCalc` にて以下を担当します。

* 商品の小計計算
* 注文全体の合計金額計算

---

## フィルターによるアクセス制御

`jakarta.servlet.http.HttpFilter` を継承した独自フィルターで認証・認可を制御しています。
フィルターの登録は `FilterConfig.java`（`FilterRegistrationBean`）で行っています。

| フィルタークラス                     | 役割                                         |
| -------------------------------- | ------------------------------------------ |
| `LoginCheckFilter`               | 未ログイン状態でのアクセスをログイン画面へリダイレクト              |
| `AdminAccountCheckFilter`        | 運用管理者（`AUTH_ADMIN=1`）の不正アクセスを制御           |
| `SystemAdminAccountCheckFilter`  | システム管理者（`AUTH_SYSTEM=0`）の不正アクセスを制御        |
| `ClientAccountCheckFilter`       | 一般会員（`AUTH_CLIENT=2`）の不正アクセスを制御           |
| `CategoryListMakeFilter`         | 全画面共通でサイドバー用カテゴリリストをリクエストスコープに格納         |

---

## 主要定数（`Constant.java`）

| 定数名                    | 値        | 説明                    |
| ----------------------- | -------- | --------------------- |
| `NOT_DELETED`           | `0`      | 削除フラグ：未削除             |
| `DELETED`               | `1`      | 削除フラグ：削除済み            |
| `AUTH_SYSTEM`           | `0`      | 権限：システム管理者            |
| `AUTH_ADMIN`            | `1`      | 権限：運用管理者              |
| `AUTH_CLIENT`           | `2`      | 権限：一般会員               |
| `DEFAULT_SORT_TYPE`     | `1`      | 表示順初期値（新着順）          |
| `DEFAULT_PAYMENT_METHOD`| `1`      | 支払方法初期値（クレジットカード）   |
| `FILE_UPLOAD_PATH`      | `"images"`| 商品画像アップロード先フォルダ名    |

---

# 画面構成

## 一般ユーザー（`/client/` 以下）

```text
トップページ（/）
 ├─ ログイン（/login）
 ├─ 会員登録（/client/user/regist/input）
 ├─ 商品一覧（/client/item/list）
 │    └─ 商品詳細（/client/item/detail）
 ├─ カート（/client/basket/list）
 └─ 注文
      ├─ 配送先入力（/client/order/address/input）
      ├─ 支払方法入力（/client/order/payment/input）
      ├─ 確認（/client/order/check）
      └─ 完了（/client/order/complete）
```

## 管理者（`/admin/` 以下）

```text
管理メニュー（/admin/menu）
 ├─ 商品管理（/admin/item/）
 ├─ カテゴリ管理（/admin/category/）
 ├─ 会員管理（/admin/user/）
 └─ 注文管理（/admin/order/）
```

---

# 開発時の禁止事項

* `pom.xml` の変更を生じる新たなライブラリの導入は禁止（フロントエンドのCDN利用はOK）
* サービスレイヤは設けず、基礎的なMVC構成とする（ControllerからJPAリポジトリのメソッドを直接呼び出す）
* コーディング規約は添付の規約ドキュメントに従うこと
  * クラス名: UpperCamelCase
  * メソッド名・変数名: lowerCamelCase
  * 定数名: UPPER_SNAKE_CASE
  * インデント: 半角スペース4つ（Tabキー禁止）
  * 1行120桁以内
  * `import` のワイルドカード（`*`）使用禁止

---

# Julesのルール

* セッション内のやり取り、プルリクエストの内容は**日本語**で記述してください
* 画面に変更がある場合はマルチモーダルで視覚的に示してください
* ブランチ名は `feat#nn`（`nn` はIssue番号）としてください
