-- Issue #3: アカウントロック機能の実装に伴うテーブル変更
ALTER TABLE users ADD login_fail_count NUMBER(2) DEFAULT 0 NOT NULL;
ALTER TABLE users ADD lock_time TIMESTAMP;
