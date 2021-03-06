DROP TABLE IF EXISTS ACCOUNT;

CREATE TABLE ACCOUNT(
	id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    password VARCHAR(250) NOT NULL,
    email VARCHAR(250),
    account_balance float,
    updatedDate DATETIME,
    status VARCHAR(250) NOT NULL
);