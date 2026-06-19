-- Issue #8: 1日1回ログインルーレット＆クーポン獲得機能のためのDB変更

-- 1. usersテーブルに最後にルーレットを実行した日付を保存するカラムを追加
ALTER TABLE users ADD last_roulette_date DATE;

-- 2. user_couponsテーブルを新規作成
CREATE TABLE user_coupons (
    id NUMBER(10) PRIMARY KEY,
    user_id NUMBER(10) NOT NULL,
    discount_rate NUMBER(3) NOT NULL,
    is_used NUMBER(1) DEFAULT 0 NOT NULL,
    expiry_date DATE NOT NULL,
    insert_date DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_user_coupons_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3. user_couponsテーブル用のシーケンスを作成
CREATE SEQUENCE seq_user_coupons START WITH 1 INCREMENT BY 1;

COMMENT ON COLUMN users.last_roulette_date IS '最後にルーレットを実行した日付';
COMMENT ON TABLE user_coupons IS 'ユーザー獲得クーポン情報';
COMMENT ON COLUMN user_coupons.id IS 'クーポンID';
COMMENT ON COLUMN user_coupons.user_id IS 'ユーザーID';
COMMENT ON COLUMN user_coupons.discount_rate IS '割引率(%)';
COMMENT ON COLUMN user_coupons.is_used IS '使用済みフラグ(0:未使用, 1:使用済み)';
COMMENT ON COLUMN user_coupons.expiry_date IS '有効期限';
COMMENT ON COLUMN user_coupons.insert_date IS '獲得日時';
