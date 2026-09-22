-- ============================================================
-- Demo data for screenshots.
-- Run AFTER db/DatabaseLogic.sql, on an empty database.
-- All names, emails and phone numbers are fictional.
-- ============================================================

DO $$
DECLARE
v_admin_user   INT;
    v_demo_user    INT;
    v_maria_user   INT;
    v_admin_ph     INT;
    v_demo_ph      INT;
    v_maria_ph     INT;
    v_pg_center    INT;
    v_pg_seaside   INT;
    v_pg_park      INT;
    v_prod_photo   INT;
    v_prod_frame   INT;
    v_prod_magnet  INT;
    v_prod_album   INT;
    v_report       INT;
BEGIN
    -- 1. Users (the password of every demo account is: demo1234)
CALL create_user('admin@photometal.com', 'demo1234', 'ADMIN', v_admin_user);
CALL create_user('demo@photometal.com',  'demo1234', 'USER',  v_demo_user);
CALL create_user('maria@photometal.com', 'demo1234', 'USER',  v_maria_user);

-- The audit triggers record who performed each change
PERFORM set_app_user_id(v_admin_user);

    -- 2. Photographers
CALL create_photographer('Admin Demo',      v_admin_user, FALSE, CURRENT_DATE - 400, v_admin_ph);
CALL create_photographer('Demo Photographer', v_demo_user, FALSE, CURRENT_DATE - 200, v_demo_ph);
CALL create_photographer('Maria Demo',      v_maria_user, FALSE, CURRENT_DATE - 90,  v_maria_ph);

-- 3. Playgrounds
CALL create_playground('Playground Center', 'Main Street 10, Athens',  '2100000001', '10:00 - 21:00', v_pg_center);
CALL create_playground('Seaside Fun Park',  'Coast Avenue 5, Piraeus', '2100000002', '09:00 - 22:00', v_pg_seaside);
CALL create_playground('City Kids Park',    'Park Square 3, Athens',   '2100000003', '11:00 - 20:00', v_pg_park);

-- 4. Products (stock rows are created automatically by the trigger)
CALL create_products('Photo 10x15',   'Standard print',            5.00,  v_prod_photo);
CALL create_products('Photo Frame',   'Wooden frame 13x18',        12.50, v_prod_frame);
CALL create_products('Photo Magnet',  'Fridge magnet',             3.50,  v_prod_magnet);
CALL create_products('Party Album',   'Album with 20 photos',      25.00, v_prod_album);

-- 5. Stock per playground
CALL update_stock_amount(v_prod_photo,  v_pg_center,  120);
CALL update_stock_amount(v_prod_frame,  v_pg_center,  40);
CALL update_stock_amount(v_prod_magnet, v_pg_center,  75);
CALL update_stock_amount(v_prod_album,  v_pg_center,  15);
CALL update_stock_amount(v_prod_photo,  v_pg_seaside, 80);
CALL update_stock_amount(v_prod_frame,  v_pg_seaside, 25);
CALL update_stock_amount(v_prod_magnet, v_pg_seaside, 60);
CALL update_stock_amount(v_prod_album,  v_pg_seaside, 10);
CALL update_stock_amount(v_prod_photo,  v_pg_park,    95);
CALL update_stock_amount(v_prod_frame,  v_pg_park,    18);
CALL update_stock_amount(v_prod_magnet, v_pg_park,    45);
CALL update_stock_amount(v_prod_album,  v_pg_park,    8);

-- 6. Work reports for the demo photographer (last four months)
PERFORM set_app_user_id(v_demo_user);

CALL create_work_report_and_details(v_demo_ph, v_pg_center, CURRENT_DATE - 7,  3, 45, 120,
        ARRAY[v_prod_photo, v_prod_frame, v_prod_magnet], ARRAY[24, 5, 12], v_report);
CALL create_work_report_and_details(v_demo_ph, v_pg_seaside, CURRENT_DATE - 21, 2, 30, 80,
        ARRAY[v_prod_photo, v_prod_album], ARRAY[18, 2], v_report);
CALL create_work_report_and_details(v_demo_ph, v_pg_center, CURRENT_DATE - 45, 4, 60, 150,
        ARRAY[v_prod_photo, v_prod_frame, v_prod_album], ARRAY[32, 8, 3], v_report);
CALL create_work_report_and_details(v_demo_ph, v_pg_park, CURRENT_DATE - 75, 1, 18, 40,
        ARRAY[v_prod_photo, v_prod_magnet], ARRAY[11, 7], v_report);
CALL create_work_report_and_details(v_demo_ph, v_pg_center, CURRENT_DATE - 110, 3, 38, 95,
        ARRAY[v_prod_photo, v_prod_frame], ARRAY[21, 6], v_report);

-- 7. Work reports for the second photographer
PERFORM set_app_user_id(v_maria_user);

CALL create_work_report_and_details(v_maria_ph, v_pg_seaside, CURRENT_DATE - 14, 2, 26, 70,
        ARRAY[v_prod_photo, v_prod_magnet, v_prod_album], ARRAY[15, 9, 1], v_report);
CALL create_work_report_and_details(v_maria_ph, v_pg_park, CURRENT_DATE - 35, 1, 15, 35,
        ARRAY[v_prod_photo, v_prod_frame], ARRAY[9, 3], v_report);

-- 8. A couple of admin changes, so the audit log shows UPDATE entries too
PERFORM set_app_user_id(v_admin_user);
CALL update_playground(v_pg_park, 'City Kids Park', 'Park Square 3, Athens', '2100000003', '10:00 - 20:00');
CALL adjust_stock(v_prod_photo, v_pg_center, 110);
END $$;