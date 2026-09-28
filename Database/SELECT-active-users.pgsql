SELECT
    u.username AS login,
    u.email,
    'TAK' AS czy_zalogowany
FROM user_account u
WHERE EXISTS (
    SELECT 1
    FROM token t
    WHERE t.user_id = u.user_id
      AND t.revoked = false
      AND t.expired = false
)
ORDER BY u.username;