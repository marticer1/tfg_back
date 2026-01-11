-- Add statistics columns to algorithm table
ALTER TABLE algorithm
    ADD COLUMN node_count INTEGER DEFAULT 0,
    ADD COLUMN edge_count INTEGER DEFAULT 0,
    ADD COLUMN component_count INTEGER DEFAULT 0;

-- Create edge table
CREATE TABLE edge
(
    id             UUID NOT NULL,
    source_node_id UUID NOT NULL,
    target_node_id UUID NOT NULL,
    algorithm_id   UUID NOT NULL,
    CONSTRAINT pk_edge PRIMARY KEY (id)
);

-- Add foreign keys for edge table
ALTER TABLE edge
    ADD CONSTRAINT FK_EDGE_ON_SOURCE_NODE FOREIGN KEY (source_node_id) REFERENCES node (id);

ALTER TABLE edge
    ADD CONSTRAINT FK_EDGE_ON_TARGET_NODE FOREIGN KEY (target_node_id) REFERENCES node (id);

ALTER TABLE edge
    ADD CONSTRAINT FK_EDGE_ON_ALGORITHM FOREIGN KEY (algorithm_id) REFERENCES algorithm (id);
