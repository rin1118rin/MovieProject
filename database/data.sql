-- ==========================================
-- DB名 : Movie
-- O-HARAFILM 初期データ
-- ==========================================


-- ==========================================
-- 1. 管理者
-- ==========================================

-- 開発用データ
-- 本番ではPASSWORDにはハッシュ化した値を保存する
INSERT INTO ADMINS (
    ADMIN_ID,
    PASSWORD
)
VALUES (
    1,
    'admin123'
);


-- ==========================================
-- 2. 映画
-- ==========================================

INSERT INTO MOVIES (
    TITLE,
    DURATION,
    RELEASE_START_DATE,
    RELEASE_END_DATE,
    GENRE,
    AGE_LIMIT,
    DESCRIPTION,
    DIRECTOR,
    "CAST",
    POSTER_URL
)
VALUES
(
    'ますお',
    106,
    '2026-10-02',
    '2026-11-30',
    'ホラー',
    'G',
    'その名前を呼んではいけない',
    '中村 健',
    '松本 莉子',
    'ますお.png'
),
(
    'エイリアン',
    112,
    '2026-09-25',
    '2026-11-15',
    '恋愛',
    'G',
    '違っているから、きっと、好きになった。',
    '中村 彩',
    '田中 蓮、伊藤 葵',
    'エイリアン.png'
),
(
    'カンガルーマン',
    124,
    '2026-10-01',
    '2026-12-20',
    'SF',
    'G',
    '世界を跳ぶ',
    '佐々木 健',
    '山本 海斗、木村 凛、松本 大輝',
    'カンガルーマン.png'
);


-- ==========================================
-- 3. 上映スケジュール
-- ==========================================

-- ますお
INSERT INTO SCREENING_SCHEDULES (
    MOVIE_ID,
    SCREEN_NO,
    START_DATETIME,
    END_DATETIME,
    SCREENING_FORMAT
)
VALUES
(1, 1, '2026-10-08 10:00:00', '2026-10-08 11:46:00', '2D'),
(1, 1, '2026-10-08 13:30:00', '2026-10-08 15:16:00', '2D'),
(1, 2, '2026-10-08 18:00:00', '2026-10-08 19:46:00', '2D');


-- エイリアン
INSERT INTO SCREENING_SCHEDULES (
    MOVIE_ID,
    SCREEN_NO,
    START_DATETIME,
    END_DATETIME,
    SCREENING_FORMAT
)
VALUES
(2, 2, '2026-10-08 11:00:00', '2026-10-08 12:52:00', '2D'),
(2, 2, '2026-10-08 15:00:00', '2026-10-08 16:52:00', '2D'),
(2, 3, '2026-10-08 19:00:00', '2026-10-08 20:52:00', '2D');


-- カンガルーマン
INSERT INTO SCREENING_SCHEDULES (
    MOVIE_ID,
    SCREEN_NO,
    START_DATETIME,
    END_DATETIME,
    SCREENING_FORMAT
)
VALUES
(3, 3, '2026-10-08 10:30:00', '2026-10-08 12:34:00', '2D'),
(3, 3, '2026-10-08 14:30:00', '2026-10-08 16:34:00', 'IMAX'),
(3, 3, '2026-10-08 19:00:00', '2026-10-08 21:04:00', 'IMAX');



-- ==========================================
-- 4. 予約
-- ==========================================

-- 星降る駅で：一般2枚
INSERT INTO RESERVATIONS (
    SCHEDULE_ID,
    CUSTOMER_NAME,
    CUSTOMER_KANA,
    EMAIL,
    PHONE,
    PAYMENT_METHOD,
    TOTAL_PRICE,
    RESERVED_AT,
    STATUS
)
VALUES (
    1,
    'テスト 太郎',
    'テスト タロウ',
    'taro@example.com',
    '09011112222',
    'card',
    3800,
    '2026-10-07 15:30:00',
    '予約済み'
);


-- 星降る駅で：学生1枚
INSERT INTO RESERVATIONS (
    SCHEDULE_ID,
    CUSTOMER_NAME,
    CUSTOMER_KANA,
    EMAIL,
    PHONE,
    PAYMENT_METHOD,
    TOTAL_PRICE,
    RESERVED_AT,
    STATUS
)
VALUES (
    2,
    'テスト 花子',
    'テスト ハナコ',
    'hanako@example.com',
    '09022223333',
    'counter',
    1500,
    '2026-10-07 16:00:00',
    '予約済み'
);


-- LAST ORBIT：一般1枚
INSERT INTO RESERVATIONS (
    SCHEDULE_ID,
    CUSTOMER_NAME,
    CUSTOMER_KANA,
    EMAIL,
    PHONE,
    PAYMENT_METHOD,
    TOTAL_PRICE,
    RESERVED_AT,
    STATUS
)
VALUES (
    7,
    'テスト 次郎',
    'テスト ジロウ',
    'jiro@example.com',
    '09033334444',
    'card',
    1900,
    '2026-10-07 17:00:00',
    '予約済み'
);


-- ==========================================
-- 5. 予約座席
-- ==========================================

-- 予約1：一般2名
INSERT INTO RESERVATION_SEATS (
    RESERVATION_ID,
    SCHEDULE_ID,
    SEAT_NO
)
VALUES
(1, 1, 'B3'),
(1, 1, 'B4');


-- 予約2：学生1名
INSERT INTO RESERVATION_SEATS (
    RESERVATION_ID,
    SCHEDULE_ID,
    SEAT_NO
)
VALUES
(2, 2, 'C5');


-- 予約3：一般1名
INSERT INTO RESERVATION_SEATS (
    RESERVATION_ID,
    SCHEDULE_ID,
    SEAT_NO
)
VALUES
(3, 7, 'A4');


-- ==========================================
-- 6. 予約券種
-- ==========================================

INSERT INTO RESERVATION_TICKETS (
    RESERVATION_ID,
    TICKET_TYPE,
    QUANTITY,
    UNIT_PRICE
)
VALUES
(1, '一般', 2, 1900),
(2, '学生', 1, 1500),
(3, '一般', 1, 1900);


-- ==========================================
-- 7. お問い合わせ
-- ==========================================

INSERT INTO INQUIRIES (
    NAME,
    EMAIL,
    SUBJECT,
    BODY,
    SENT_AT,
    STATUS
)
VALUES
(
    'テスト 太郎',
    'taro@example.com',
    'チケット・予約について',
    '予約した座席を変更することはできますか。',
    '2026-10-07 18:00:00',
    '未対応'
),
(
    'テスト 花子',
    'hanako@example.com',
    '上映作品について',
    '上映終了日は変更になることがありますか。',
    '2026-10-07 19:00:00',
    '対応済み'
),
(
    'テスト 次郎',
    'jiro@example.com',
    'その他',
    '劇場内に忘れ物をしました。',
    '2026-10-08 09:00:00',
    '未対応'
);


-- ==========================================
-- 8. お問い合わせ返信
-- ==========================================

INSERT INTO INQUIRY_REPLIES (
    INQUIRY_ID,
    REPLY_BODY,
    STAFF_NAME,
    REPLIED_AT
)
VALUES (
    2,
    'お問い合わせありがとうございます。上映終了日は状況により変更となる場合がございます。',
    '管理者',
    '2026-10-08 09:30:00'
);