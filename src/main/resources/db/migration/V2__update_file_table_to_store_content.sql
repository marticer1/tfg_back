-- Update file table to store content instead of file path
ALTER TABLE file DROP COLUMN file_path;
ALTER TABLE file ADD COLUMN file_type VARCHAR(255);
ALTER TABLE file ADD COLUMN content TEXT;
