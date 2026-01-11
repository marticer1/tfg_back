# Migration Guide: Python Code Restructuring

This document explains the migration from the old structure to the new Python directory structure.

## Summary of Changes

The Python code has been reorganized to separate concerns:
- **Business logic** (stn_core.py) is now independent of Flask
- **Flask server** (stn_server.py) is now optional
- **CLI** (stn_cli.py) works without Flask dependencies
- All Python code moved from `src/main/java/com/tfg/backend/api/` to `python/`

## Old vs New Structure

### Old Structure
```
src/main/java/com/tfg/backend/api/
├── stn.py                    # Monolithic: Flask + business logic
├── stn_cli.py                # Imported from stn.py (caused Flask to load)
├── partition/
│   ├── continuous/
│   │   ├── standard.py
│   │   └── agglomerative.py
│   └── discrete/
│       └── standard.py
└── *.R                       # R scripts
```

### New Structure
```
python/
├── stn_core.py               # Pure business logic (no Flask)
├── stn_server.py             # Flask HTTP server (optional)
├── stn_cli.py                # CLI (no Flask dependency)
├── requirements.txt          # Python dependencies
├── partition/
│   ├── continuous/
│   │   ├── standard.py
│   │   └── agglomerative.py
│   └── discrete/
│       └── standard.py
├── *.R                       # R scripts
└── README.md                 # Documentation
```

## Migration Steps

### 1. Install Dependencies

```bash
cd python
pip install -r requirements.txt
```

### 2. Update Java Configuration (if needed)

The default path has been changed in `StnGeneratorService.java`:
- Old: `${stn.scriptsDir:src/main/java/com/tfg/backend/api}`
- New: `${stn.scriptsDir:python}`

If you have custom configuration in `application.properties`, update it:
```properties
# Old
stn.scriptsDir=src/main/java/com/tfg/backend/api

# New
stn.scriptsDir=python
```

### 3. Update Python Imports (if you have custom scripts)

If you have custom scripts that import from the old structure:

**Old:**
```python
from stn import Params, process_partition
```

**New:**
```python
from stn_core import Params, process_partition
```

### 4. Running the Flask Server

**Old:**
```bash
cd src/main/java/com/tfg/backend/api
python3 stn.py
```

**New:**
```bash
cd python
python3 stn_server.py
```

### 5. Using the CLI

**Old:**
```bash
cd src/main/java/com/tfg/backend/api
python3 stn_cli.py [arguments]
```

**New:**
```bash
cd python
python3 stn_cli.py [arguments]
```

The CLI now has better help documentation:
```bash
python3 stn_cli.py --help
```

## Cleanup Old Files (Optional)

After verifying the new structure works, you can remove the old files:

```bash
# Remove old Python files (keep Java files)
rm src/main/java/com/tfg/backend/api/stn.py
rm src/main/java/com/tfg/backend/api/stn_cli.py
rm src/main/java/com/tfg/backend/api/encoding.py
rm src/main/java/com/tfg/backend/api/stn.wsgi

# Remove old R scripts
rm src/main/java/com/tfg/backend/api/*.R

# Remove old partition directory
rm -rf src/main/java/com/tfg/backend/api/partition
```

**Note:** Keep `StnGeneratorService.java` - it has been updated to use the new structure.

## Benefits of New Structure

1. **No Unnecessary Dependencies**: The CLI and core logic don't require Flask anymore
2. **Better Organization**: Python code in dedicated directory instead of mixed with Java
3. **Easier Testing**: Pure functions in stn_core.py can be tested independently
4. **Flexible Usage**: Can use as library, CLI, or server independently
5. **Clear Separation**: Business logic, server, and CLI are cleanly separated

## Testing the Migration

### Test Core Module (without Flask)
```bash
cd python
python3 -c "import stn_core; print('Success')"
```

### Test CLI
```bash
cd python
python3 stn_cli.py --help
```

### Test Flask Server
```bash
cd python
python3 stn_server.py
# Server should start on http://0.0.0.0:5000
```

### Test Java Integration
```bash
# Requires Java 21
./mvnw clean test
```

## Troubleshooting

### Import Errors
If you see `ModuleNotFoundError`, install dependencies:
```bash
pip install -r python/requirements.txt
```

### Java Build Issues
The project requires Java 21. Check your version:
```bash
java -version
```

### Path Issues
Make sure you're running commands from the correct directory:
- Python commands: run from `python/` directory
- Maven commands: run from project root

## Support

For questions or issues with the migration, please refer to:
- `python/README.md` - Detailed documentation of new structure
- This file - Migration steps and troubleshooting
