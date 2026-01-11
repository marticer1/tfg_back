# Python Code Restructuring - Summary

## Overview

This restructuring separates Flask server logic from pure business logic in the Python codebase, enabling the core functionality to be used without loading Flask dependencies.

## What Changed

### 1. New Directory Structure

All Python and R code moved from `src/main/java/com/tfg/backend/api/` to a dedicated `python/` directory:

```
python/
├── stn_core.py                 # Pure business logic (no Flask)
├── stn_server.py               # Flask HTTP server (optional)
├── stn_cli.py                  # Command-line interface
├── requirements.txt            # Python dependencies
├── test_restructure.py         # Verification tests
├── README.md                   # Complete documentation
├── partition/
│   ├── __init__.py
│   ├── continuous/
│   │   ├── __init__.py
│   │   ├── standard.py         # Continuous standard partitioning
│   │   └── agglomerative.py    # Agglomerative clustering
│   └── discrete/
│       ├── __init__.py
│       └── standard.py         # Discrete standard partitioning
├── create.R
├── merge.R
├── metrics-alg.R
├── metrics-merged.R
├── plot-alg.R
├── plot-alg-tree.R
├── plot-merged.R
└── plot-merged-tree.R
```

### 2. Code Separation

**stn_core.py** (Pure Business Logic)
- All data classes: `Params`, `StandardParams`, `AgglomerativeClusteringParams`, `Instances`
- Core processing: `process_partition()`, `generate_from_files()`, `generate_from_file()`
- Utilities: `writing_file_discrete()`, `writing_file_continuous()`, `change_old_format()`
- **No Flask dependencies** - can be imported and used independently

**stn_server.py** (Flask Server)
- HTTP endpoints: `/stn`, `/stn-metrics`, `/agglomerative-info`
- Request parameter extraction: `get_params()`
- Delegates all business logic to `stn_core`
- **Optional** - only needed if running as HTTP server

**stn_cli.py** (Command Line Interface)
- Comprehensive argument parser with help
- File-based input (no HTTP)
- Uses only `stn_core` - no Flask dependency
- Can be used independently of server

### 3. Java Integration Update

**StnGeneratorService.java** updated:
- Default scripts directory changed from `src/main/java/com/tfg/backend/api` to `python`
- No functional changes - still calls Python CLI the same way

### 4. Documentation

- **python/README.md**: Complete documentation of structure, usage, dependencies
- **MIGRATION.md**: Step-by-step migration guide
- **python/requirements.txt**: Separated dependencies (core vs Flask)
- **python/test_restructure.py**: Automated verification tests

### 5. Configuration

**.gitignore** updated to exclude:
- Python cache files (`__pycache__/`, `*.pyc`)
- Python build artifacts
- STN temporary files (`temp/`)

## Benefits

1. **Separation of Concerns**
   - Business logic completely independent of web framework
   - Can test and use core logic without Flask

2. **Reduced Dependencies**
   - CLI and core logic don't require Flask/Flask-CORS
   - Only install what you need for your use case

3. **Better Organization**
   - Python code in dedicated directory
   - Not mixed with Java source files
   - Clear module boundaries

4. **Flexibility**
   - Use as library: `import stn_core`
   - Use as CLI: `python3 stn_cli.py`
   - Use as server: `python3 stn_server.py`
   - All three modes work independently

5. **Maintainability**
   - Easier to test individual components
   - Clear import hierarchy
   - Better code organization

## Usage Examples

### As a Library (Python)
```python
from stn_core import Params, StandardParams, process_partition

# Create parameters
standard_config = StandardParams(pf=3, pp=50.0, min_b=0, max_b=100)
params = Params(...)

# Process partitions
hash_file = process_partition(params)
```

### As CLI
```bash
cd python
python3 stn_cli.py \
  --typeproblem discrete \
  --strategy standard \
  --nodesize 1 \
  --treelayout false \
  --hash-file mytest \
  --file "data.txt:Algorithm1:#FF0000"
```

### As HTTP Server
```bash
cd python
python3 stn_server.py
# Server runs on http://0.0.0.0:5000
```

### From Java (Spring Boot)
The existing `StnGeneratorService` continues to work with updated paths:
```java
@Service
public class StnGeneratorService {
    // Automatically uses python/ directory
    public Path generateFromRegistration(RegistrationProblemDTO dto, UUID problemId)
}
```

## Migration Path

For existing users:

1. **Install dependencies**: `pip install -r python/requirements.txt`
2. **Update configuration** (if customized): Change `stn.scriptsDir` in `application.properties`
3. **Update imports** (if any): Replace `from stn import` with `from stn_core import`
4. **Verify**: Run `python3 python/test_restructure.py`

See **MIGRATION.md** for detailed steps.

## Verification

All automated tests pass:
```
✓ Module Imports: PASS
✓ Class Instantiation: PASS  
✓ Flask Independence: PASS
✓ CLI Availability: PASS
```

Run tests: `python3 python/test_restructure.py`

## Files Removed

Old files removed from `src/main/java/com/tfg/backend/api/`:
- `stn.py` (replaced by `python/stn_core.py` + `python/stn_server.py`)
- `stn_cli.py` (replaced by improved `python/stn_cli.py`)
- `encoding.py` (empty file)
- `stn.wsgi`
- `*.R` files (moved to `python/`)
- `partition/` directory (moved to `python/partition/`)

Only `StnGeneratorService.java` remains, updated to use new structure.

## Next Steps

The restructuring is complete and verified. The new structure:
- ✅ Separates concerns properly
- ✅ Reduces unnecessary dependencies
- ✅ Improves organization
- ✅ Maintains backward compatibility (Java integration)
- ✅ Passes all verification tests

For questions or issues, see:
- `python/README.md` - Usage documentation
- `MIGRATION.md` - Migration guide
- `python/test_restructure.py` - Verification tests
