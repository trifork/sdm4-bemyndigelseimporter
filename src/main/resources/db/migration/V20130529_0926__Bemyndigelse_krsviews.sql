
-- -----------------------------------------------------
-- Someone has to create the SKRS tables first time
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SKRSViewMapping` (
  `idSKRSViewMapping` BIGINT(15) NOT NULL AUTO_INCREMENT ,
  `register` VARCHAR(255) NOT NULL ,
  `datatype` VARCHAR(255) NOT NULL ,
  `version` INT NOT NULL ,
  `tableName` VARCHAR(255) NOT NULL ,
  `createdDate` TIMESTAMP NOT NULL ,
  PRIMARY KEY (`idSKRSViewMapping`) ,
  INDEX `idx` (`register` ASC, `datatype` ASC, `version` ASC) ,
  UNIQUE INDEX `unique` (`register` ASC, `datatype` ASC, `version` ASC) )
  ENGINE = InnoDB;
CREATE  TABLE IF NOT EXISTS `SKRSColumns` (
  `idSKRSColumns` BIGINT(15) NOT NULL AUTO_INCREMENT ,
  `viewMap` BIGINT(15) NOT NULL ,
  `isPID` TINYINT NOT NULL ,
  `tableColumnName` VARCHAR(255) NOT NULL ,
  `feedColumnName` VARCHAR(255) NULL ,
  `feedPosition` INT NOT NULL ,
  `dataType` INT NOT NULL ,
  `maxLength` INT NULL ,
  PRIMARY KEY (`idSKRSColumns`) ,
  INDEX `viewMap_idx` (`viewMap` ASC) ,
  UNIQUE INDEX `viewColumn` (`tableColumnName` ASC, `viewMap` ASC) ,
  CONSTRAINT `viewMap`
  FOREIGN KEY (`viewMap` )
  REFERENCES `SKRSViewMapping` (`idSKRSViewMapping` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
  ENGINE = InnoDB;

-- ---------------------------------------------------------------------------------------------------------------------
-- Bemyndigelse

INSERT IGNORE INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES ('bemyndigelsesservice', 'bemyndigelse', 1, 'Bemyndigelse', NOW());
INSERT IGNORE INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength) VALUES
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 1, 'PID',                              NULL, 0, -5, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'kode',                           'kode', 1, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'bemyndigende_cpr',   'bemyndigende_cpr', 2, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'bemyndigede_cpr',     'bemyndigede_cpr', 3, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'bemyndigede_cvr',     'bemyndigede_cvr', 4, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'system',                       'system', 5, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'arbejdsfunktion',     'arbejdsfunktion', 6, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'rettighed',                 'rettighed', 7, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'status',                       'status', 8, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'godkendelses_dato', 'godkendelses_dato', 9, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'oprettelses_dato',   'oprettelses_dato',10, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'modificeret_dato',   'modificeret_dato',11, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'gyldig_fra_dato',     'gyldig_fra_dato',12, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'gyldig_til_dato',     'gyldig_til_dato',13, 12, NULL),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'ModifiedDate',                     NULL, 0, 93, 12),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'ValidFrom',                 'ValidFrom',14, 93, 12),
((SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register='bemyndigelsesservice' AND datatype='bemyndigelse' AND version=1), 0, 'ValidTo',                     'ValidTo',15, 93, 12);
