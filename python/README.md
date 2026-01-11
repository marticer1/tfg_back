# STN (Search Trajectory Network) Python Module

This directory contains the Python implementation for STN generation and visualization, cleanly separated into different concerns.

## Directory Structure

```
python/
├── stn_core.py              # Pure business logic (no Flask dependencies)
├── stn_server.py            # Flask HTTP server (optional)
├── stn_cli.py               # Command-line interface
├── partition/               # Partition algorithms
│   ├── continuous/
│   │   ├── standard.py      # Standard continuous partitioning
│   │   └── agglomerative.py # Agglomerative clustering
│   └── discrete/
│       └── standard.py      # Standard discrete partitioning
└── *.R                      # R scripts for visualization
```

## Modules

### stn_core.py
Pure business logic module that can be used without Flask. Contains:
- **Classes**: `Params`, `StandardParams`, `AgglomerativeClusteringParams`, `Instances`
- **Core functions**: `process_partition()`, `generate_from_files()`, `generate_from_file()`
- **Utility functions**: `writing_file_discrete()`, `writing_file_continuous()`, `change_old_format()`

This module can be imported and used in any Python application without loading Flask.

### stn_server.py
Flask HTTP server providing REST endpoints:
- `POST /stn` - Generate STN visualization
- `POST /stn-metrics` - Retrieve metrics CSV
- `POST /agglomerative-info` - Get agglomerative clustering information

**Run the server:**
```bash
cd python
python3 stn_server.py
# Server will start on http://0.0.0.0:5000
```

**Enable debug mode (development only):**
```bash
cd python
FLASK_DEBUG=true python3 stn_server.py
```

**Note:** Debug mode is disabled by default for security. Only enable it in development environments.

### stn_cli.py
Command-line interface for STN generation without needing the Flask server.

**Examples:**

Discrete problem with standard partitioning:
```bash
python3 stn_cli.py \
  --typeproblem discrete \
  --strategy standard \
  --bmin 1 \
  --best 100 \
  --nruns 30 \
  --nodesize 1 \
  --arrowsize 0.15 \
  --treelayout false \
  --hash-file mytest \
  --partition-factor 3 \
  --min-bound 0 \
  --max-bound 100 \
  --file "/path/to/alg1.txt:Algorithm1:#FF0000" \
  --file "/path/to/alg2.txt:Algorithm2:#00FF00"
```

Continuous problem with agglomerative clustering:
```bash
python3 stn_cli.py \
  --typeproblem continuous \
  --strategy agglomerative \
  --bmin 1 \
  --best 100 \
  --nruns 30 \
  --nodesize 1 \
  --treelayout false \
  --hash-file mytest \
  --cluster-size 50 \
  --volume-size 50 \
  --distance-method euclidean \
  --file "/path/to/alg1.txt:Algorithm1:#FF0000"
```

## Dependencies

### Core dependencies (required for all modules):
- Python 3.x
- numpy
- scipy
- R (with required packages)

### Server-only dependencies (only for stn_server.py):
- flask
- flask-cors

### CLI dependencies:
- Only core dependencies (no Flask needed)

## Integration with Java/Spring Boot

The Java service `StnGeneratorService` has been updated to use the new `python/` directory structure. It calls `stn_cli.py` to generate STN visualizations.

Configuration in `application.properties`:
```properties
# Python executable (default: python3)
stn.python=python3

# Scripts directory (default: python)
stn.scriptsDir=python
```

## Migration from Old Structure

The old structure had Python scripts in `src/main/java/com/tfg/backend/api/`:
- `stn.py` - Monolithic file with Flask + business logic
- `stn_cli.py` - CLI that imported from stn.py (causing Flask to load)
- `partition/` - Partition modules

These have been reorganized:
1. Business logic extracted to `python/stn_core.py`
2. Flask server isolated in `python/stn_server.py`
3. CLI rewritten to use `stn_core.py` directly
4. All files moved to dedicated `python/` directory

## Benefits of New Structure

1. **Separation of Concerns**: Flask server is completely separate from business logic
2. **No Unnecessary Dependencies**: CLI and core logic don't require Flask
3. **Better Organization**: Python code in dedicated directory, not mixed with Java
4. **Easier Testing**: Pure functions in stn_core.py can be tested without Flask
5. **Flexible Deployment**: Can run server, CLI, or use as library independently
