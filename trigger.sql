DELIMITER // 
CREATE TRIGGER insert_fulltext 
AFTER INSERT ON message_entity 
FOR EACH ROW 
BEGIN 
INSERT INTO messages_fts 
VALUES (NEW.id, NEW.subject, NEW.message_body); 
END; 