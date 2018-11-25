CREATE TABLE IF NOT EXISTS `players`
(
  player          INT               AUTO_INCREMENT,
  uuid            VARCHAR(36)       NOT NULL,
  username        VARCHAR(17)       NOT NULL,
  container       MEDIUMTEXT        NOT NULL,
  PRIMARY KEY (player)
);
CREATE TABLE IF NOT EXISTS `islands`
(
  island          INT               AUTO_INCREMENT,
  identifier      VARCHAR(52)       NOT NULL,
  ownerId         VARCHAR(36)       NOT NULL,
  ownerName       VARCHAR(17)       NOT NULL,
  members         MEDIUMTEXT        NOT NULL,
  PRIMARY KEY (island)
);
