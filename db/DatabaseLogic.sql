CREATE EXTENSION IF NOT EXISTS pgcrypto;
--Create the schema for the database


--Δημιουργία πίνακα για τους users
CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    email     TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role          TEXT NOT NULL ,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_role_type CHECK (role IN ('ADMIN', 'USER'))

);


--Δημιουργία πίνακα για τα στοιχεία photographers
CREATE TABLE PHOTOGRAPHER
(
    id            SERIAL PRIMARY KEY,
    user_id       INT UNIQUE REFERENCES users(id),
    full_name     TEXT,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    HireDate      DATE
);

--Δημιουργία πίνακα για τους playgrounds
CREATE TABLE playgrounds
(
    id        SERIAL PRIMARY KEY,
    name      TEXT NOT NULL,
    address   TEXT NOT NULL,
    phone     TEXT,
    OpenTime  TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_playground_name_address UNIQUE (name, address)
);

--Δημηριογία πίνακα για τους products
CREATE TABLE products
(
    id        SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    name      TEXT NOT NULL,
    price     NUMERIC(10,2) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_product_name_description UNIQUE (name, description)

);


--Δημιουρυγία πίνακα για τα Work_reports
CREATE TABLE work_reports
(
    id        SERIAL PRIMARY KEY,
    photographer_id INT NOT NULL REFERENCES PHOTOGRAPHER(id),
    playground_id INT NOT NULL REFERENCES playgrounds(id),
    report_date DATE NOT NULL,
    parties_count INT,
    customers_count INT,
    papers_count INT,
    TotalAmount NUMERIC(10,2)
);

--Δημιουργία πίνακα για τα Report_details
CREATE TABLE report_details
(
    id        SERIAL PRIMARY KEY,
    work_report_id INT NOT NULL REFERENCES work_reports(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id),
    quantity_sold INT NOT NULL CHECK (quantity_sold > 0),
    unit_price NUMERIC(10,2) NOT NULL -- Kυριως για να βλεπει αν υπαρχει μεταβολη στην τιμη μπορουμε και να το βγαλουμε αν θες
);

--δημιουργεια του πινακα των store_house_stock
CREATE TABLE store_house_stock
(
    id BIGSERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES products(id),
    playground_id INT NOT NULL REFERENCES playgrounds(id),
    current_amount INT NOT NULL CHECK (current_amount >= 0),
    last_update TIMESTAMP DEFAULT now(),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_store_house_stock UNIQUE (playground_id, product_id)
);





CREATE TABLE log_audit
(
    id          SERIAL PRIMARY KEY,
    user_id     INT REFERENCES users(id),
    action_type TEXT NOT NULL CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE')),
    table_name  TEXT NOT NULL,
    record_id   INT,
    new_data    JSONB,
    old_data    JSONB,
    log_time    TIMESTAMP DEFAULT now()
);



-----------------------------------------------
-----------------------------------------------
--Procedures of the database for CRUD operations
-----------------------------------------------
-----------------------------------------------

CREATE OR REPLACE PROCEDURE create_playground(
    IN  p_name       TEXT,
    IN  p_address    TEXT,
    IN  p_phone      TEXT,
    IN  p_open_time  TEXT,
    OUT new_id       INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1) Υπάρχει ήδη ΕΝΕΡΓΟ playground με ίδιο name + address;
    IF EXISTS (
        SELECT 1
        FROM playgrounds
        WHERE name = p_name
          AND address = p_address
          AND is_deleted = FALSE
    ) THEN
        RAISE EXCEPTION 'Playground "%" at "%" already exists and is active',
            p_name, p_address;
END IF;

    -- 2) Υπάρχει soft-deleted playground με ίδιο name + address;
SELECT id
INTO new_id
FROM playgrounds
WHERE name = p_name
  AND address = p_address
  AND is_deleted = TRUE
    LIMIT 1;

IF new_id IS NOT NULL THEN
        -- 2a) RESTORE: το φέρνουμε πίσω
UPDATE playgrounds
SET phone      = p_phone,
    OpenTime   = p_open_time,
    is_deleted = FALSE
WHERE id = new_id;


ELSE
        -- 3) Τελείως νέο playground → κανονικό INSERT
        INSERT INTO playgrounds(name, address, phone, OpenTime)
        VALUES (p_name, p_address, p_phone, p_open_time)
        RETURNING id INTO new_id;
        -- Για αυτό το νέο id, από Java καλείς:
        -- CALL initialize_new_playground_stock(new_id);
END IF;
END;
$$;






CREATE OR REPLACE PROCEDURE update_playground(
    IN p_id INT,
    IN p_name TEXT,
    IN p_address TEXT,
    IN p_phone TEXT,
    IN p_open_time TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE playgrounds
SET name = p_name,
    address = p_address,
    phone = p_phone,
    OpenTime = p_open_time
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Playground with id % not found', p_id;
END IF;
END;
$$;






CREATE OR REPLACE PROCEDURE delete_playground(
    IN p_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE playgrounds
SET is_deleted = TRUE
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Playground with id % not found', p_id;
END IF;
END;
$$;


CREATE OR REPLACE FUNCTION update_store_house_stock_on_playground_soft_delete()
RETURNS trigger AS $$
BEGIN
    -- Ελέγχουμε αν η τιμή του is_deleted άλλαξε από FALSE (OLD) σε TRUE (NEW)
    IF OLD.is_deleted = FALSE AND NEW.is_deleted = TRUE THEN

        -- Κάνουμε Soft Delete (UPDATE) όλες τις σχετικές εγγραφές στο store_house_stock
UPDATE store_house_stock
SET is_deleted = TRUE
WHERE playground_id = NEW.id;
END IF;

if OLD.is_deleted = TRUE AND NEW.is_deleted = FALSE THEN
UPDATE store_house_stock
SET is_deleted = FALSE
WHERE playground_id = NEW.id;
END IF;

    -- Επιστρέφουμε τη νέα γραμμή (απαραίτητο για AFTER UPDATE)
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_soft_delete_playground_stock
    AFTER UPDATE ON playgrounds
    FOR EACH ROW
    EXECUTE FUNCTION update_store_house_stock_on_playground_soft_delete();





CREATE OR REPLACE PROCEDURE create_photographer(
    IN p_full_name TEXT,
    IN p_user_id INT,
    IN p_is_deleted BOOLEAN,
    IN p_HireDate DATE,
    OUT new_id INT

)
LANGUAGE plpgsql
AS $$
BEGIN
 IF EXISTS (
        SELECT 1 FROM PHOTOGRAPHER
        WHERE user_id = p_user_id
    ) THEN
        RAISE EXCEPTION 'User  with id : % already exists', p_user_id;
END IF;

INSERT INTO PHOTOGRAPHER(full_name, is_deleted, HireDate, user_id)
VALUES (p_full_name, p_is_deleted, p_HireDate,p_user_id)
    RETURNING id INTO new_id;
END;
$$;

CREATE OR REPLACE PROCEDURE update_photographer(
    IN p_id INT,
    IN p_full_name TEXT,
    IN p_user_id INT,
    IN p_is_deleted BOOLEAN,
    IN p_HireDate DATE
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE PHOTOGRAPHER
SET full_name = p_full_name,
    is_deleted = p_is_deleted,
    HireDate = p_HireDate,
    user_id = p_user_id
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Photographer with id % not found', p_id;
END IF;
END;
$$;



CREATE OR REPLACE PROCEDURE delete_photographer(
    IN p_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN

UPDATE PHOTOGRAPHER
SET is_deleted = TRUE
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Photographer with id % not found', p_id;
END IF;
END;
$$;









CREATE OR REPLACE PROCEDURE create_user(
    IN p_email TEXT,
    IN p_password TEXT,
    IN p_role TEXT,
    OUT new_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1) Υπάρχει ενεργός user με αυτό το email;
    IF EXISTS (
        SELECT 1 FROM users
        WHERE email = p_email
          AND is_deleted = FALSE
    ) THEN
        RAISE EXCEPTION 'User with email % already exists and is active', p_email;
END IF;

    -- 2) Υπάρχει soft-deleted user με αυτό το email;
SELECT id INTO new_id
FROM users
WHERE email = p_email
  AND is_deleted = TRUE
    LIMIT 1;

IF new_id IS NOT NULL THEN
        -- "Restore" + ανανέωση password/role
UPDATE users
SET password   = crypt(p_password, gen_salt('bf', 12)),
    role       = p_role,
    is_deleted = FALSE
WHERE id = new_id;
ELSE
        -- Τελείως νέος user
        INSERT INTO users(email, password, role)
        VALUES (p_email,  crypt(p_password, gen_salt('bf', 12)), p_role)
        RETURNING id INTO new_id;
END IF;
END;
$$;





CREATE OR REPLACE PROCEDURE update_user(
    IN p_id INT,
    IN p_email TEXT,
    IN p_password TEXT,
    IN p_role TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE users
SET email = p_email,
    password = crypt(p_password, gen_salt('bf', 12)),
    role = p_role
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'User with id % not found', p_id;
END IF;
END;
$$;


CREATE OR REPLACE PROCEDURE delete_user(
    IN p_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE  users
SET is_deleted = TRUE
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'User with id % not found', p_id;
END IF;
END;
$$;


---Trigger για αυτοματοποιημενο soft delete του photographer μεσα απο τον user
CREATE OR REPLACE FUNCTION update_users_And_PHOTOGRAFER_soft_delete()
RETURNS trigger AS $$
BEGIN
    -- Ελέγχουμε αν η τιμή του is_deleted άλλαξε από FALSE (OLD) σε TRUE (NEW)
    IF OLD.is_deleted = FALSE AND NEW.is_deleted = TRUE THEN

        -- Κάνουμε Soft Delete (UPDATE) όλες τις σχετικές εγγραφές στο store_house_stock
UPDATE PHOTOGRAPHER
SET is_deleted = TRUE
WHERE user_id = NEW.id;
END IF;

if OLD.is_deleted = TRUE AND NEW.is_deleted = FALSE THEN
UPDATE PHOTOGRAPHER
SET is_deleted = FALSE
WHERE user_id = NEW.id;
END IF;

    -- Επιστρέφουμε τη νέα γραμμή (απαραίτητο για AFTER UPDATE)
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_soft_delete_User_And_PHOTOGRAFER
    AFTER UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_users_And_PHOTOGRAFER_soft_delete();


CREATE OR REPLACE PROCEDURE create_products
(
     IN p_name TEXT,
     IN p_description TEXT,
     IN p_price NUMERIC(10,2),
     OUT new_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
  IF p_price < 0 THEN
    RAISE EXCEPTION 'Price cannot be negative';
END IF;

INSERT INTO products(name, description, price)
VALUES (p_name, p_description, p_price)
    RETURNING id INTO new_id;
END;
$$;








Create OR REPLACE PROCEDURE update_products
(
     IN p_id INT,
     IN p_name TEXT,
     IN p_description TEXT,
     IN p_price NUMERIC(10,2)

)
LANGUAGE plpgsql
AS $$
BEGIN
  IF p_price < 0 THEN
    RAISE EXCEPTION 'Price cannot be negative';
END IF;

UPDATE products
SET name = p_name,
    description = p_description,
    price = p_price
WHERE id = p_id;

IF NOT FOUND THEN
    RAISE EXCEPTION 'Product with id % not found', p_id;
END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_products
(
    IN p_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
update products
SET is_deleted = TRUE
WHERE id = p_id;
IF NOT FOUND THEN
        RAISE EXCEPTION 'Product with id % not found', p_id;
END IF;
END;
$$;



CREATE OR REPLACE PROCEDURE restore_product(
    IN p_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE products
SET is_deleted = FALSE
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Product with id % not found', p_id;
END IF;
END;
$$;


-- κραταμε το report_details  για ιστορικοτητα του προιοντος
CREATE OR REPLACE FUNCTION soft_delete_products_and_storehouse_stock()
RETURNS trigger AS $$
BEGIN
    -- Ελέγχουμε αν η τιμή του is_deleted άλλαξε από FALSE (OLD) σε TRUE (NEW)
    IF OLD.is_deleted = FALSE AND NEW.is_deleted = TRUE THEN

        -- Κάνουμε Soft Delete (UPDATE) όλες τις σχετικές εγγραφές στο store_house_stock
UPDATE store_house_stock
SET is_deleted = TRUE
WHERE product_id = NEW.id;


END IF;

   if OLD.is_deleted = TRUE AND NEW.is_deleted = FALSE THEN
UPDATE store_house_stock
SET is_deleted = FALSE
WHERE product_id = NEW.id;



END IF;
RETURN NEW;
END;
 $$ LANGUAGE plpgsql;

--trigger για αυτοματοποιημενο soft delete του products και του store_house_stock
CREATE TRIGGER trg_soft_delete_products
    AFTER UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION soft_delete_products_and_storehouse_stock();










-- CREATE TABLE work_reports
-- (
--     id        SERIAL PRIMARY KEY,
--     photographer_id INT NOT NULL REFERENCES photographers(id),
--     playground_id INT NOT NULL REFERENCES playgrounds(id),
--     report_date DATE NOT NULL,
--     parties_count INT,
--     customers_count INT,
--     papers_count INT,
--     TotalAmount NUMERIC(10,2)
-- );
--
-- --Δημιουργία πίνακα για τα Report_details
-- CREATE TABLE report_details
-- (
--     id        SERIAL PRIMARY KEY,
--     work_report_id INT NOT NULL REFERENCES work_reports(id) ON DELETE CASCADE,
--     product_id INT NOT NULL REFERENCES products(id),
--     quantity_sold INT NOT NULL CHECK (quantity_sold > 0),
--
--     unit_price NUMERIC(10,2) NOT NULL, -- Kυριως για να βλεπει αν υπαρχει μεταβολη στην τιμη μπορουμε και να το βγαλουμε αν θες
-- );
--




--Δημιουργια του report και του report_details ταυτοχρονα
CREATE OR REPLACE PROCEDURE create_work_report_and_details(
    IN p_photographer_id INT,
    IN p_playground_id INT,
    IN p_report_date DATE,
    IN p_parties INT,
    IN p_customers INT,
    IN p_papers INT,
    IN p_product_ids INT[], -- Array of product IDs
    IN p_quantities INT[],  -- Array of quantities sold
    --IN p_unit_prices NUMERIC(10, 2)[],
    OUT new_report_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE
v_total_amount NUMERIC(10, 2) := 0;
    v_detail_sum NUMERIC(10, 2) := 0;
    v_unit_price NUMERIC(10, 2);
    i INT;
BEGIN
    -- 1. Υπολογισμός συνολικού ποσού από τις λεπτομέρειες
    v_detail_sum := 0;
FOR i IN 1 .. array_upper(p_product_ids, 1)
    LOOP
SELECT price INTO v_unit_price FROM products WHERE id = p_product_ids[i];
v_detail_sum := v_detail_sum + (p_quantities[i] * v_unit_price);
END LOOP;

    v_total_amount := v_detail_sum;

    -- 2. Εισαγωγή στο work_reports (Κύρια εγγραφή)
INSERT INTO work_reports (
    photographer_id, playground_id, report_date, totalamount,
    parties_count, customers_count, papers_count
)
VALUES (
           p_photographer_id, p_playground_id, p_report_date, v_total_amount,
           p_parties, p_customers, p_papers
       )
    RETURNING id INTO new_report_id;

-- 3. Εισαγωγή στο report_details (Λεπτομέρειες πωλήσεων) και Ενημέρωση Stock
FOR i IN 1 .. array_upper(p_product_ids, 1)
    LOOP

SELECT price INTO v_unit_price FROM products WHERE id = p_product_ids[i];
-- Εισαγωγή στη γραμμή αναφοράς
INSERT INTO report_details (
    work_report_id, product_id, quantity_sold, unit_price
)
VALUES (
           new_report_id,
           p_product_ids[i],
           p_quantities[i],
           v_unit_price
       );

-- Ενημέρωση του store_house_stock (αφαίρεση πωληθέντων)
UPDATE store_house_stock
SET
    current_amount = current_amount - p_quantities[i],
    last_update = now()
WHERE
    product_id = p_product_ids[i] AND
    playground_id = p_playground_id;

-- Σημείωση: Αν το current_amount γίνει αρνητικό, η βάση δεδομένων θα
-- πετάξει αυτόματα λάθος (CHECK constraint) και όλη η συναλλαγή θα ακυρωθεί.
END LOOP;

END;
$$;

CREATE OR REPLACE PROCEDURE delete_work_report(
    IN p_report_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Αυτή η εντολή αρκεί
DELETE FROM work_reports
WHERE id = p_report_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Work Report with id % not found', p_report_id;
END IF;
END;
$$;



CREATE OR REPLACE PROCEDURE update_work_report_and_details(
    IN p_report_id INT,
    IN p_photographer_id INT,
    IN p_playground_id INT,
    IN p_report_date DATE,
    IN p_parties INT,
    IN p_customers INT,
    IN p_papers INT,
    IN p_product_ids INT[],
    IN p_quantities INT[]
)
LANGUAGE plpgsql
AS $$
DECLARE
v_total_amount NUMERIC(10, 2) := 0;
    v_unit_price NUMERIC(10, 2);
    v_detail_sum NUMERIC(10, 2);
    old_detail record;
    i INT;
BEGIN
    -- 0. ΕΛΕΓΧΟΣ: Βεβαιωθείτε ότι η αναφορά υπάρχει
    IF NOT EXISTS (SELECT 1 FROM work_reports WHERE id = p_report_id) THEN
        RAISE EXCEPTION 'Work Report with id % not found', p_report_id;
END IF;

    -- 1. ΑΝΤΙΣΤΡΟΦΗ ΠΑΛΙΩΝ ΣΥΝΑΛΛΑΓΩΝ (Stock Compensation)
    -- Προσθέτουμε τις ποσότητες πίσω στο stock πριν διαγράψουμε τις γραμμές.
FOR old_detail IN
SELECT product_id, quantity_sold FROM report_details WHERE work_report_id = p_report_id
    LOOP
UPDATE store_house_stock
SET
    current_amount = current_amount + old_detail.quantity_sold,
    last_update = now()
WHERE
    product_id = old_detail.product_id AND
    playground_id = p_playground_id;
END LOOP;

    -- 2. ΔΙΑΓΡΑΦΗ: Διαγραφή παλιών λεπτομερειών
    -- Αυτό θα πυροδοτήσει τους audit triggers ως DELETE
DELETE FROM report_details WHERE work_report_id = p_report_id;

-- 3. ΕΙΣΑΓΩΓΗ & ΥΠΟΛΟΓΙΣΜΟΣ ΝΕΟΥ ΣΥΝΟΛΟΥ (Εφαρμογή νέας αναφοράς)
v_detail_sum := 0;
FOR i IN 1 .. array_upper(p_product_ids, 1)
    LOOP
        -- Ανάκτηση τιμής από products
SELECT price INTO v_unit_price FROM products WHERE id = p_product_ids[i];

-- Εισαγωγή νέας γραμμής αναφοράς (Details)
INSERT INTO report_details (
    work_report_id, product_id, quantity_sold, unit_price
)
VALUES (
           p_report_id,
           p_product_ids[i],
           p_quantities[i],
           v_unit_price
       );

-- Αφαίρεση της νέας ποσότητας από το stock
UPDATE store_house_stock
SET
    current_amount = current_amount - p_quantities[i],
    last_update = now()
WHERE
    product_id = p_product_ids[i] AND
    playground_id = p_playground_id;

-- Υπολογισμός του νέου TotalAmount
v_detail_sum := v_detail_sum + (p_quantities[i] * v_unit_price);
END LOOP;

    v_total_amount := v_detail_sum;

    -- 4. ΕΝΗΜΕΡΩΣΗ: Ενημέρωση του Work Report Header
UPDATE work_reports
SET
    photographer_id = p_photographer_id,
    playground_id = p_playground_id,
    report_date = p_report_date,
    TotalAmount = v_total_amount,
    parties_count = p_parties,
    customers_count = p_customers,
    papers_count = p_papers
WHERE id = p_report_id;

END;
$$;



CREATE OR REPLACE PROCEDURE update_stock_amount(
    IN p_product_id INT,
    IN p_playground_id INT,
    IN p_new_amount INT -- Η νέα ποσότητα που θέλουμε να έχει
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Ενημερώνουμε την υπάρχουσα ποσότητα και ενεργοποιούμε την εγγραφή αν ήταν soft deleted
UPDATE store_house_stock
SET
    current_amount = p_new_amount,
    last_update = now(),
    is_deleted = FALSE
WHERE
    product_id = p_product_id AND playground_id = p_playground_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Stock entry not found for Product ID % at Playground ID %.', p_product_id, p_playground_id;
END IF;

END;
$$;








CREATE OR REPLACE PROCEDURE adjust_stock(
    IN p_product_id INT,
    IN p_playground_id INT,
    IN p_amount_to_set INT -- Η νέα ποσότητα που θέλουμε να έχει (όχι η αλλαγή)
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1. Προσπαθούμε να ενημερώσουμε το υπάρχον απόθεμα
UPDATE store_house_stock
SET
    current_amount = p_amount_to_set,
    last_update = now(),
    is_deleted = FALSE -- Ενεργοποιούμε την εγγραφή αν ήταν Soft Deleted
WHERE
    product_id = p_product_id AND playground_id = p_playground_id;

-- 2. Αν δεν βρέθηκε εγγραφή (π.χ., πρώτη φορά που εισάγεται το προϊόν σε αυτό το playground), κάνουμε INSERT
IF NOT FOUND THEN
        INSERT INTO store_house_stock (
            product_id, playground_id, current_amount, is_deleted
        )
        VALUES (
            p_product_id, p_playground_id, p_amount_to_set, FALSE
        );
END IF;

    -- Σημείωση: Ο έλεγχος current_amount >= 0 γίνεται αυτόματα από το CHECK constraint του πίνακα.

END;
$$;

CREATE OR REPLACE PROCEDURE create_stock_entry(
    IN p_product_id INT,
    IN p_playground_id INT,
    IN p_initial_amount INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Έλεγχος: Απαγορεύεται η εισαγωγή αν υπάρχει ήδη ενεργή εγγραφή (η Unique Key το καλύπτει, αλλά ο έλεγχος είναι καλύτερος)
    IF EXISTS (
        SELECT 1 FROM store_house_stock
        WHERE product_id = p_product_id AND playground_id = p_playground_id AND is_deleted = FALSE
    ) THEN
        RAISE EXCEPTION 'Stock entry already exists for Product ID % at Playground ID %. Use adjust_stock to update.', p_product_id, p_playground_id;
END IF;

    -- Ρητή εισαγωγή της νέας οντότητας
INSERT INTO store_house_stock (
    product_id, playground_id, current_amount, is_deleted
)
VALUES (
           p_product_id, p_playground_id, p_initial_amount, FALSE
       );
END;
$$;




-- ΝΕΑ PROCEDURE (Πρέπει να την δημιουργήσετε στη βάση σας)
CREATE OR REPLACE PROCEDURE initialize_new_playground_stock(p_playground_id INT)
LANGUAGE plpgsql
AS $$
DECLARE
product_record RECORD;
BEGIN
    -- Κάνουμε loop σε όλα τα ενεργά προϊόντα
FOR product_record IN
SELECT id FROM products WHERE is_deleted = FALSE
    LOOP
        -- Καλούμε την υπάρχουσα create_stock_entry για κάθε προϊόν
        CALL create_stock_entry(
            product_record.id,
            p_playground_id,
            0  -- Αρχικό ποσό = 0
        );
END LOOP;
END;
$$;








CREATE OR REPLACE FUNCTION log_audit_changes()
RETURNS trigger AS $$
DECLARE
v_user INT;
BEGIN
    -- Παίρνουμε το user id από global session variable
BEGIN
        v_user := current_setting('app.current_user_id')::INT;
EXCEPTION WHEN others THEN
        v_user := NULL;
END;

    IF TG_OP = 'INSERT' THEN
        INSERT INTO log_audit(user_id, action_type, table_name, record_id, new_data)
        VALUES (v_user, 'INSERT', TG_TABLE_NAME, NEW.id, to_jsonb(NEW));
RETURN NEW;

ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO log_audit(user_id, action_type, table_name, record_id, new_data, old_data)
        VALUES (v_user, 'UPDATE', TG_TABLE_NAME, NEW.id, to_jsonb(NEW), to_jsonb(OLD));
RETURN NEW;

ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO log_audit(user_id, action_type, table_name, record_id, old_data)
        VALUES (v_user, 'DELETE', TG_TABLE_NAME, OLD.id, to_jsonb(OLD));
RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;








-- Triggers για audit σε βασικούς πίνακες
CREATE TRIGGER trg_playgrounds_audit
    AFTER INSERT OR UPDATE OR DELETE ON playgrounds
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();

CREATE TRIGGER trg_users_audit
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();


CREATE TRIGGER trg_photographers_audit
    AFTER INSERT OR UPDATE OR DELETE ON PHOTOGRAPHER
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();


CREATE TRIGGER trg_products_audit
    AFTER INSERT OR UPDATE OR DELETE ON products
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();

CREATE TRIGGER trg_work_reports_audit
    AFTER INSERT OR UPDATE OR DELETE ON work_reports
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();

CREATE TRIGGER trg_report_details_audit
    AFTER INSERT OR UPDATE OR DELETE ON report_details
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();

CREATE TRIGGER trg_store_house_stock_audit
    AFTER INSERT OR UPDATE OR DELETE ON store_house_stock
    FOR EACH ROW EXECUTE FUNCTION log_audit_changes();


---------------------------------------------------------------------------
---------------------------------------------------------------------------
---------------------------- FUNCTIONS ------------------------------------
---------------------------------------------------------------------------

-- To χρεισιμοποεις καθε φορα που ο αλλος κανει σωστο login
CREATE OR REPLACE FUNCTION set_app_user_id(p_user_id INT)
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    PERFORM set_config('app.current_user_id', p_user_id::TEXT, false);
END;
$$;


-- Απο εδω και περα δεν τα εγω βαλει στην βαση
CREATE OR REPLACE FUNCTION FindUser(p_email TEXT, p_password TEXT)
RETURNS INT
LANGUAGE plpgsql
AS $$
DECLARE
user_id INT;
BEGIN

SELECT id
INTO user_id
FROM users
WHERE email = p_email AND password = crypt(p_password, password) AND is_deleted = FALSE;

IF user_id IS NULL THEN
        -- Ο χρήστης δεν βρέθηκε
        RETURN -1;
ELSE
        -- Ο χρήστης βρέθηκε
        RETURN user_id;
END IF;
END;
$$;

CREATE OR REPLACE FUNCTION GetUserIdByEmail(p_email TEXT)
RETURNS INT
LANGUAGE plpgsql
AS $$
DECLARE
user_id INT;
BEGIN
SELECT id
INTO user_id
FROM users
WHERE email = p_email;

IF user_id IS NULL THEN
        -- Ο χρήστης δεν βρέθηκε
        RETURN -1;
ELSE
        -- Ο χρήστης βρέθηκε
        RETURN user_id;
END IF;
END;
$$;

CREATE OR REPLACE FUNCTION Find_user_role(p_user_id INT)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
user_role TEXT;
BEGIN
    -- 1. Επιλογή του ρόλου
SELECT role INTO user_role
FROM users
WHERE id = p_user_id;

-- 2. Έλεγχος για NULL (Αν το ID δεν βρεθεί)
IF user_role IS NULL THEN
        -- Αν το ID δεν υπάρχει, επιστρέφουμε μια ειδική τιμή (π.χ., NOT_FOUND)
        RETURN 'NOT_FOUND';
ELSE
        -- Επιστρέφουμε τον ρόλο που βρέθηκε (ADMIN ή USER)
        RETURN user_role;
END IF;
END;
$$;






CREATE OR REPLACE FUNCTION InfoOfOnePlaygroynd(p_playground_id INT)
RETURNS TABLE(
    playground_id INT,
    playground_name TEXT,
    playground_address TEXT,
    playground_phone TEXT,
    playground_OpenTime Text
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT id, name, address, phone, OpenTime
    FROM playgrounds
    WHERE id = p_playground_id;
END;
$$;


CREATE OR REPLACE FUNCTION GetAllPlaygroundsSimple()
RETURNS TABLE(id INT, name TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT p.id, p.name
FROM playgrounds p
WHERE p.is_deleted = FALSE;
END;
$$;



CREATE OR REPLACE FUNCTION GetAllProduct()
RETURNS TABLE (
    product_id INT,             -- Όνομα στήλης εξόδου
    product_name TEXT,          -- Όνομα στήλης εξόδου
    product_description TEXT,
    product_price NUMERIC(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    p.id AS product_id,          -- ΣΩΣΤΟ: Χρησιμοποιούμε ALIAS για να ταιριάξουμε την έξοδο
    p.name AS product_name,
    p.description AS product_description,
    p.price AS product_price
FROM
    products p
WHERE
    p.is_deleted = FALSE -- Εδώ το p. είναι σωστό
ORDER BY p.name;
END;
$$;




CREATE OR REPLACE FUNCTION find_photographer_with_user_id(p_user_id INT)
RETURNS INT
LANGUAGE plpgsql
AS $$
DECLARE
photographer_id INT; -- Δηλώνουμε τη μεταβλητή για το αποτέλεσμα
BEGIN
    -- Προσπαθούμε να βρούμε το ID του φωτογράφου
SELECT id
INTO photographer_id
FROM PHOTOGRAPHER -- ΔΙΟΡΘΩΣΗ: Χρησιμοποιούμε το σωστό όνομα πίνακα
WHERE user_id = p_user_id;

-- Ελέγχουμε αν βρέθηκε ο φωτογράφος
IF photographer_id IS NULL THEN
        -- Αν δεν βρεθεί, επιστρέφουμε -1 (ή 0) για να το χειριστεί η εφαρμογή
        RETURN -1;
ELSE
        -- Αν βρεθεί, επιστρέφουμε το ID του φωτογράφου
        RETURN photographer_id;
END IF;
END;
$$;

Create or replace function getallProductsExist()
returns table(product_id int, product_name text, product_description text, product_price numeric(10,2))
language plpgsql
as $$
begin
return query
select p.id as product_id, p.name as product_name, p.description as product_description, p.price  as product_price
from products p
where p.is_deleted = false
order by p.name;
end;
$$;

Create or replace function getallProductsNotExist()
returns table(product_id int, product_name text, product_description text, product_price numeric(10,2))
language plpgsql
as $$
begin
return query
select p.id as product_id, p.name as product_name, p.description as product_description, p.price  as product_price
from products p
where p.is_deleted = true
order by p.name;
end;
$$;


-- SQL FUNCTION (Θα την καλεί ο Trigger)
CREATE OR REPLACE FUNCTION initialize_stock_on_new_product()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
playground_record RECORD;
BEGIN
    -- Κάνουμε loop σε όλα τα ενεργά playgrounds
FOR playground_record IN
SELECT id FROM playgrounds WHERE is_deleted = FALSE
    LOOP
        -- Για κάθε playground, καλούμε την create_stock_entry
        -- (Η NEW.id είναι το ID του προϊόντος που μόλις δημιουργήθηκε)
        CALL create_stock_entry(
            NEW.id,
            playground_record.id,
            0  -- Αρχικό ποσό = 0
        );
END LOOP;

RETURN NEW;
END;
$$;

-- SQL TRIGGER
CREATE TRIGGER trg_initialize_stock_after_product_insert
    AFTER INSERT ON products
    FOR EACH ROW
    EXECUTE FUNCTION initialize_stock_on_new_product();



CREATE OR REPLACE FUNCTION get_store_house_stock(p_playground_id INT)
RETURNS TABLE (
    product_id INT,
    product_name TEXT,
    product_description TEXT,
    quantity INT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    p.id AS product_id,
    p.name AS product_name,
    p.description AS product_description,
    s.current_amount AS quantity
FROM
    products p
        JOIN store_house_stock s ON p.id = s.product_id
WHERE
    s.playground_id = p_playground_id
  AND s.is_deleted = FALSE
  AND p.is_deleted = FALSE
ORDER BY
    p.name;
END;
$$;


-- για το log ετσι γιατι μπερδευει τα id
CREATE OR REPLACE FUNCTION get_log_audit(p_table_name TEXT DEFAULT NULL)
RETURNS TABLE (
    id INT,
    user_id INT,
    full_name TEXT,
    action_type TEXT,
    table_name TEXT,
    record_id INT,
    new_data JSONB,
    old_data JSONB,
    log_time TIMESTAMP
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF p_table_name IS NULL OR p_table_name = '' THEN
        RETURN QUERY
SELECT  l.id,
        l.user_id,
        ph.full_name AS full_name,
        l.action_type,
        l.table_name,
        l.record_id,
        l.new_data,
        l.old_data,
        l.log_time
FROM log_audit AS l
         LEFT JOIN users u
                   ON u.id = l.user_id
         LEFT JOIN photographer ph
                   ON ph.user_id = u.id
ORDER BY l.log_time DESC;
ELSE
        RETURN QUERY
SELECT  l.id,
        l.user_id,
        ph.full_name AS full_name,
        l.action_type,
        l.table_name,
        l.record_id,
        l.new_data,
        l.old_data,
        l.log_time
FROM log_audit AS l
         LEFT JOIN users u
                   ON u.id = l.user_id
         LEFT JOIN photographer ph
                   ON ph.user_id = u.id
WHERE l.table_name = p_table_name
ORDER BY l.log_time DESC;
END IF;
END;
$$;



CREATE OR REPLACE FUNCTION get_work_reports_for_user(
    p_user_id INT
)
RETURNS TABLE (
    report_id        INT,
    playground_id    INT,
    playground_name  TEXT,
    report_date      DATE,
    parties_count    INT,
    customers_count  INT,
    papers_count     INT,
    total_amount     NUMERIC(10,2)

)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    w.id,
    w.playground_id,
    pg.name,
    w.report_date,
    w.parties_count,
    w.customers_count,
    w.papers_count,
    w.totalamount
FROM work_reports w
         JOIN PHOTOGRAPHER ph ON ph.id = w.photographer_id
         JOIN playgrounds pg  ON pg.id = w.playground_id
WHERE ph.user_id = p_user_id          -- 🔥 εδώ φιλτράρεις με user_id
ORDER BY w.report_date DESC;
END;
$$;


CREATE OR REPLACE FUNCTION get_report_details(
    p_report_id INT
)
RETURNS TABLE (
    detail_id     INT,
    work_report_id INT,
    product_id    INT,
    quantity_sold INT,
    unit_price    NUMERIC(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    rd.id,
    rd.work_report_id,
    rd.product_id,
    rd.quantity_sold,
    rd.unit_price
FROM report_details rd
WHERE rd.work_report_id = p_report_id
ORDER BY rd.id;
END;
$$;


CREATE OR REPLACE FUNCTION get_top_products_for_user(
    p_user_id INT,
    p_limit   INT DEFAULT 3
)
RETURNS TABLE (
    product_id   INT,
    product_name TEXT,
    total_sold   BIGINT,
    product_description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    p.id          AS product_id,
    p.name        AS product_name,
    p.description AS product_description,
    SUM(rd.quantity_sold) AS total_sold
FROM report_details rd
         JOIN work_reports wr
              ON wr.id = rd.work_report_id
         JOIN PHOTOGRAPHER ph
              ON ph.id = wr.photographer_id
         JOIN products p
              ON p.id = rd.product_id
WHERE ph.user_id = p_user_id
GROUP BY p.id, p.name
ORDER BY total_sold DESC
    LIMIT p_limit;
END;
$$;


DROP FUNCTION IF EXISTS get_current_user_settings(INT);
CREATE OR REPLACE FUNCTION get_current_user_settings(p_user_id INT)
RETURNS TABLE (
    user_id    INT,
    email      TEXT,
    full_name  TEXT,
    hire_date  DATE,
    role       TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    u.id,
    u.email,
    ph.full_name,
    ph.hiredate,
    u.role
FROM users u
         JOIN PHOTOGRAPHER ph ON ph.user_id = u.id
WHERE u.id = p_user_id
  AND u.is_deleted = FALSE;
END;
$$;


CREATE OR REPLACE FUNCTION updatepassword(p_user_id INT, p_new_password TEXT)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
BEGIN
UPDATE users
SET password = crypt(p_new_password, gen_salt('bf', 12))
WHERE id = p_user_id
  AND is_deleted = FALSE;

RETURN FOUND;
END;
$$;

CREATE OR REPLACE PROCEDURE update_current_user_profile(
    IN p_user_id   INT,
    IN p_password  TEXT,
    IN p_full_name TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- The password changes only when a new one is given (NULL or '' = keep the old one)
    IF p_password IS NOT NULL AND length(p_password) > 0 THEN
UPDATE users
SET password = crypt(p_password, gen_salt('bf', 12))
WHERE id = p_user_id
  AND is_deleted = FALSE;

IF NOT FOUND THEN
            RAISE EXCEPTION 'User with id % not found or is deleted', p_user_id;
END IF;
END IF;

UPDATE PHOTOGRAPHER
SET full_name = p_full_name
WHERE user_id = p_user_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Photographer for user % not found', p_user_id;
END IF;
END;
$$;


CREATE OR REPLACE FUNCTION get_all_photographers_simple()
RETURNS TABLE (
    photographer_id INT,
    full_name       TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    ph.id,
    ph.full_name
FROM PHOTOGRAPHER ph
WHERE ph.is_deleted = FALSE
ORDER BY ph.full_name;
END;
$$;


CREATE OR REPLACE FUNCTION get_work_reports_for_photographer(
    p_photographer_id INT
)
RETURNS TABLE (
    report_id        INT,
    photographer_name TEXT,
    playground_name  TEXT,
    report_date      DATE,
    parties_count    INT,
    customers_count  INT,
    papers_count     INT,
    total_amount     NUMERIC(10,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    w.id                         AS report_id,
    ph.full_name                 AS photographer_name,
    pg.name                      AS playground_name,
    w.report_date,
    w.parties_count,
    w.customers_count,
    w.papers_count,
    w.totalamount                AS total_amount
FROM work_reports w
         JOIN PHOTOGRAPHER ph ON ph.id = w.photographer_id
         JOIN playgrounds pg  ON pg.id = w.playground_id
WHERE ph.id = p_photographer_id
ORDER BY w.report_date DESC;
END;
$$;




CREATE OR REPLACE FUNCTION get_monthly_sales_for_product_year(
    p_product_id INT,
    p_year       INT
)
RETURNS TABLE (
    month_num   INT,     -- 1–12
    month_label TEXT,    -- 'YYYY-MM'
    total_sold  BIGINT   -- πόσα τεμάχια πουλήθηκαν
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
    WITH months AS (
        SELECT generate_series(1, 12) AS month_num
    )
SELECT
    m.month_num,
    to_char(make_date(p_year, m.month_num, 1), 'YYYY-MM') AS month_label,
    COALESCE(SUM(rd.quantity_sold), 0) AS total_sold
FROM months m
         LEFT JOIN work_reports w
                   ON EXTRACT(YEAR  FROM w.report_date) = p_year
                       AND EXTRACT(MONTH FROM w.report_date) = m.month_num
         LEFT JOIN report_details rd
                   ON rd.work_report_id = w.id
                       AND rd.product_id    = p_product_id
GROUP BY m.month_num, month_label
ORDER BY m.month_num;
END;
$$;
