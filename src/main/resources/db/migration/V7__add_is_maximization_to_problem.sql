-- Add is_maximization field to problem table
ALTER TABLE problem
    ADD COLUMN is_maximization BOOLEAN NOT NULL DEFAULT false;
