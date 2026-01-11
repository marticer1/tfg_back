CREATE TABLE agglomerative_clustering
(
    id           UUID             NOT NULL,
    cluster_size DOUBLE PRECISION NOT NULL,
    volume_size  DOUBLE PRECISION NOT NULL,
    distance     VARCHAR(255),
    CONSTRAINT pk_agglomerativeclustering PRIMARY KEY (id)
);

CREATE TABLE algorithm
(
    id         UUID  NOT NULL,
    name       VARCHAR(255),
    color      BYTEA NOT NULL,
    file_id    UUID,
    problem_id UUID  NOT NULL,
    CONSTRAINT pk_algorithm PRIMARY KEY (id)
);

CREATE TABLE file
(
    id        UUID NOT NULL,
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    CONSTRAINT pk_file PRIMARY KEY (id)
);

CREATE TABLE node
(
    id           UUID         NOT NULL,
    type         VARCHAR(255) NOT NULL,
    algorithm_id UUID         NOT NULL,
    x            DOUBLE PRECISION,
    y            DOUBLE PRECISION,
    z            DOUBLE PRECISION,
    CONSTRAINT pk_node PRIMARY KEY (id)
);

CREATE TABLE problem
(
    id                          UUID             NOT NULL,
    problem_type                VARCHAR(31),
    name                        VARCHAR(255),
    color                       BYTEA            NOT NULL,
    value_best_known_solution   INTEGER          NOT NULL,
    number_runs                 INTEGER          NOT NULL,
    vertex_size                 DOUBLE PRECISION NOT NULL,
    arrow_size                  DOUBLE PRECISION NOT NULL,
    tree_layout                 BOOLEAN          NOT NULL,
    problem_collection_id       UUID             NOT NULL,
    standard_partitioning_id    UUID,
    agglomerative_clustering_id UUID,
    shannon_entropy_id          UUID,
    CONSTRAINT pk_problem PRIMARY KEY (id)
);

CREATE TABLE problem_collection
(
    id    UUID  NOT NULL,
    name  VARCHAR(255),
    color BYTEA NOT NULL,
    CONSTRAINT pk_problemcollection PRIMARY KEY (id)
);

CREATE TABLE shannon_entropy
(
    id           UUID             NOT NULL,
    partitioning DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_shannonentropy PRIMARY KEY (id)
);

CREATE TABLE standard_partitioning
(
    id               UUID    NOT NULL,
    hypercube        INTEGER NOT NULL,
    min_bound        INTEGER NOT NULL,
    max_bound        INTEGER NOT NULL,
    number_dimension INTEGER NOT NULL,
    CONSTRAINT pk_standardpartitioning PRIMARY KEY (id)
);

ALTER TABLE algorithm
    ADD CONSTRAINT FK_ALGORITHM_ON_FILE FOREIGN KEY (file_id) REFERENCES file (id);

ALTER TABLE algorithm
    ADD CONSTRAINT FK_ALGORITHM_ON_PROBLEM FOREIGN KEY (problem_id) REFERENCES problem (id);

ALTER TABLE node
    ADD CONSTRAINT FK_NODE_ON_ALGORITHM FOREIGN KEY (algorithm_id) REFERENCES algorithm (id);

ALTER TABLE problem
    ADD CONSTRAINT FK_PROBLEM_ON_AGGLOMERATIVE_CLUSTERING FOREIGN KEY (agglomerative_clustering_id) REFERENCES agglomerative_clustering (id);

ALTER TABLE problem
    ADD CONSTRAINT FK_PROBLEM_ON_PROBLEM_COLLECTION FOREIGN KEY (problem_collection_id) REFERENCES problem_collection (id);

ALTER TABLE problem
    ADD CONSTRAINT FK_PROBLEM_ON_SHANNON_ENTROPY FOREIGN KEY (shannon_entropy_id) REFERENCES shannon_entropy (id);

ALTER TABLE problem
    ADD CONSTRAINT FK_PROBLEM_ON_STANDARD_PARTITIONING FOREIGN KEY (standard_partitioning_id) REFERENCES standard_partitioning (id);