-- Add color and shape fields to problem table
ALTER TABLE problem
    ADD COLUMN color_start BYTEA,
    ADD COLUMN color_end BYTEA,
    ADD COLUMN start_shape VARCHAR(255),
    ADD COLUMN end_shape VARCHAR(255),
    ADD COLUMN default_shape VARCHAR(255);
