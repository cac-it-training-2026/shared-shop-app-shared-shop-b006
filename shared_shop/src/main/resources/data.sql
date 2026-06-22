-- 初期ユーザー (パスワード: password)
INSERT INTO users (id, name, email, password, postal_code, address, phone_number, authority, delete_flag) VALUES (1, 'ユーザー１', 'user01@example.com', 'password', '1111111', '東京都', '01011111111', 2, 0);
INSERT INTO users (id, name, email, password, postal_code, address, phone_number, authority, delete_flag) VALUES (2, '管理者', 'admin@example.com', 'password', '2222222', '東京都', '02022222222', 1, 0);

-- カテゴリ
INSERT INTO categories (id, name, description, delete_flag) VALUES (1, '食品', 'おいしい食べ物', 0);
INSERT INTO categories (id, name, description, delete_flag) VALUES (2, '家電', '便利な機械', 0);

-- 商品
INSERT INTO items (id, name, description, price, stock, image, category_id, delete_flag) VALUES (1, 'りんご', '青森県産のりんご', 100, 10, 'apple.jpg', 1, 0);
INSERT INTO items (id, name, description, price, stock, image, category_id, delete_flag) VALUES (2, 'オレンジ', '愛媛県産のオレンジ', 120, 20, 'orange.jpg', 1, 0);
INSERT INTO items (id, name, description, price, stock, image, category_id, delete_flag) VALUES (3, '辞書', '国語辞典', 2500, 5, 'dictionary.jpg', 1, 0);

-- シーケンスの調整 (H2)
ALTER SEQUENCE seq_users RESTART WITH 3;
ALTER SEQUENCE seq_categories RESTART WITH 3;
ALTER SEQUENCE seq_items RESTART WITH 4;
