INSERT INTO role_service.user_role(user_id, role_id)
VALUES ('00000000-0000-0001-0000-000000000001', 1), -- default test user
       ('00000000-0000-0001-0000-000000000001', 2), -- default test user
       ('00000000-0000-0001-0000-000000000001', 3), -- default test user
       ('00000000-0000-0001-0000-000000000001', 4), -- default test user
       ('7fffffff-ffff-ffff-0000-000000000001', 5), -- default srv user
       ('7fffffff-ffff-ffff-0000-000000000002', 5), -- tea srv user
       ('7fffffff-ffff-ffff-0000-000000000003', 5), -- kraken srv user
       ('7fffffff-ffff-ffff-0000-000000000003', 6) -- kraken srv user
;

DO
$$
    DECLARE
        id  bigint;
        uid UUID;
        r   bigint;
    BEGIN
        FOR id in 1..10000
            LOOP
                uid := CAST(LPAD(TO_HEX(id), 32, '0') AS UUID);
                CASE WHEN id = 1 THEN
                    INSERT INTO role_service.user_role(user_id, role_id)
                    VALUES (uid, 4);
                    ELSE
                        FOR r IN (SELECT DISTINCT unnest(array [
                            4,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int,
                            floor(random() * 10000 + 1)::int
                            ]))
                            LOOP
                                INSERT INTO role_service.user_role(user_id, role_id)
                                VALUES (uid, r);
                            end loop;
                    END CASE;
            end loop;
    end;
$$;
