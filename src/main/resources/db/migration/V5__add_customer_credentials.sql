ALTER TABLE customers ADD COLUMN username VARCHAR(80);
ALTER TABLE customers ADD COLUMN password_hash VARCHAR(100);

ALTER TABLE customers
ADD CONSTRAINT uk_customers_username UNIQUE (username);
