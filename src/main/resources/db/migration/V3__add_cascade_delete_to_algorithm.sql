-- Drop the existing foreign key constraint
ALTER TABLE algorithm
    DROP CONSTRAINT IF EXISTS FK_ALGORITHM_ON_PROBLEM;

-- Add the foreign key constraint with ON DELETE CASCADE
ALTER TABLE algorithm
    ADD CONSTRAINT FK_ALGORITHM_ON_PROBLEM 
    FOREIGN KEY (problem_id) 
    REFERENCES problem (id) 
    ON DELETE CASCADE;
