-- 1. レビューテーブル (reviews) とシーケンスの作成
CREATE SEQUENCE seq_reviews NOCACHE;

CREATE TABLE reviews (
    id NUMBER PRIMARY KEY,
    order_id NUMBER,          -- 注文情報への外部キー
    item_id NUMBER,           -- 商品情報への外部キー
    user_id NUMBER,           -- 会員情報への外部キー
    display_name VARCHAR2(255),
    title VARCHAR2(255),
    body VARCHAR2(4000),      -- 本文（長文になるため少し大きめ）
    rating NUMBER,
    helpful_count NUMBER DEFAULT 0,
    delete_flag NUMBER(1) DEFAULT 0,
    insert_date TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 2. 「参考になった」ログテーブル (review_helpful_logs) とシーケンスの作成
CREATE SEQUENCE seq_review_helpful_logs NOCACHE;

CREATE TABLE review_helpful_logs (
    id NUMBER PRIMARY KEY,
    review_id NUMBER,         -- レビュー情報への外部キー
    user_id NUMBER            -- 会員情報への外部キー
);
