create schema customer;
create schema session;
create schema subscription;
create schema status;
create schema measurement;

-- INSERT INTO session.device(uuid,customer_id) VALUES ('ooooo','1')
-- select * from session.device
-- insert into CUSTOMER.CUSTOMER (username) values ('ربى خوري')

CREATE TABLE IF NOT EXISTS customer.customer (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL,
  hashed_password CHAR(60) NULL,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE INDEX id_UNIQUE (id ASC),
  UNIQUE INDEX username_UNIQUE (username ASC));

  CREATE TABLE IF NOT EXISTS customer.personal_details (
  email VARCHAR(256) NOT NULL,
  first_name VARCHAR(45) NOT NULL,
  last_name VARCHAR(45) NOT NULL,
  birthday DATE NULL,
  customer_id BIGINT NOT NULL,
  PRIMARY KEY (customer_id),
  UNIQUE INDEX email_UNIQUE (email ASC),
  foreign key (customer_id) REFERENCES customer.customer(id)
  );  
  
  create INDEX `last_name_INDEX` on `customer`.`personal_details`(`last_name` ASC);
  create INDEX `first_name_INDEX` on `customer`.`personal_details`(`first_name` ASC);


  CREATE TABLE IF NOT EXISTS `session`.`device` (
  `uuid` CHAR(43) NOT NULL,
  `customer_id` BIGINT NOT NULL,
    PRIMARY KEY (customer_id),
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC),
    FOREIGN KEY (`customer_id`) REFERENCES `customer`.`customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION);

  CREATE TABLE IF NOT EXISTS `session`.`session` (
  `sid` CHAR(43) NOT NULL,
  `customer_id` BIGINT NOT NULL,
    PRIMARY KEY (customer_id),
  UNIQUE INDEX `sid_UNIQUE` (`sid` ASC),
    FOREIGN KEY (`customer_id`) REFERENCES `customer`.`customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION);

    CREATE TABLE IF NOT EXISTS `subscription`.`subscription` (
	`start` TIMESTAMP NULL,
  `end` TIMESTAMP NULL,
  `id` BIGINT NOT NULL,
  `customer_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
    FOREIGN KEY (`customer_id`) REFERENCES `customer`.`customer` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION);
  
CREATE TABLE IF NOT EXISTS `measurement`.`measurement` (
  `id` BIGINT NOT NULL,
  `type` VARCHAR(45) NULL,
  `value` VARCHAR(45) NULL,
  `time` TIMESTAMP NULL,
  `customer_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
    FOREIGN KEY (`customer_id`)
    REFERENCES `customer`.`customer` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
    
  create INDEX `type_INDEX` on `measurement`.`measurement`(`type` ASC);
