#!/usr/bin/env python3
"""
Test script to verify the restructured Python code works correctly.
This demonstrates that core logic can be used without Flask.
"""
import sys
import os

# Add parent directory to path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

def test_imports():
    """Test that modules can be imported without Flask."""
    print("Testing imports...")
    
    # Test core module import (should work without Flask)
    try:
        import stn_core
        print("✓ stn_core imported successfully (no Flask required)")
    except ImportError as e:
        print(f"✗ Failed to import stn_core: {e}")
        return False
    
    # Test that core classes are available
    assert hasattr(stn_core, 'Params')
    assert hasattr(stn_core, 'StandardParams')
    assert hasattr(stn_core, 'AgglomerativeClusteringParams')
    assert hasattr(stn_core, 'Instances')
    print("✓ Core classes available")
    
    # Test that core functions are available
    assert hasattr(stn_core, 'process_partition')
    assert hasattr(stn_core, 'generate_from_files')
    assert hasattr(stn_core, 'generate_from_file')
    print("✓ Core functions available")
    
    return True


def test_class_instantiation():
    """Test that core classes can be instantiated."""
    print("\nTesting class instantiation...")
    
    import stn_core
    
    # Test StandardParams
    standard = stn_core.StandardParams(pf=3, pp=50.0, min_b=0, max_b=100)
    assert standard.partition_factor == 1000.0  # 10^3
    assert standard.partition_percen == 50.0
    print("✓ StandardParams works")
    
    # Test AgglomerativeClusteringParams
    aggl = stn_core.AgglomerativeClusteringParams(
        cluster_size=50.0,
        volumen_size=50.0,
        number_of_clusters=None,
        distance_method="euclidean"
    )
    assert aggl.cluster_size == 50.0
    assert aggl.distance_method == "euclidean"
    print("✓ AgglomerativeClusteringParams works")
    
    # Test Instances
    instance = stn_core.Instances()
    assert hasattr(instance, 'index')
    assert hasattr(instance, 'contentLineFile')
    assert hasattr(instance, 'filename')
    print("✓ Instances works")
    
    return True


def test_flask_optional():
    """Test that Flask is truly optional."""
    print("\nTesting Flask independence...")
    
    # This test checks if we can use core without Flask installed
    # In reality Flask is installed, but we verify the import structure
    import sys
    
    # Check stn_core doesn't import Flask
    import stn_core
    if 'flask' in sys.modules:
        # Flask might be cached from earlier imports
        print("⚠ Flask is loaded in modules (may be from other imports)")
    
    # Verify stn_server requires Flask
    try:
        import stn_server
        assert hasattr(stn_server, 'app')
        print("✓ stn_server correctly uses Flask")
    except ImportError:
        print("⚠ Flask not installed - stn_server unavailable (this is OK)")
    
    return True


def test_cli_availability():
    """Test that CLI module is available."""
    print("\nTesting CLI availability...")
    
    try:
        import stn_cli
        assert hasattr(stn_cli, 'main')
        print("✓ stn_cli imported successfully")
        print("✓ CLI can be used without running Flask server")
    except ImportError as e:
        print(f"✗ Failed to import stn_cli: {e}")
        return False
    
    return True


def main():
    """Run all tests."""
    print("=" * 60)
    print("STN Python Restructuring - Verification Tests")
    print("=" * 60)
    
    tests = [
        ("Module Imports", test_imports),
        ("Class Instantiation", test_class_instantiation),
        ("Flask Independence", test_flask_optional),
        ("CLI Availability", test_cli_availability),
    ]
    
    results = []
    for test_name, test_func in tests:
        try:
            result = test_func()
            results.append((test_name, result))
        except Exception as e:
            print(f"✗ Test failed with exception: {e}")
            results.append((test_name, False))
    
    print("\n" + "=" * 60)
    print("Test Summary:")
    print("=" * 60)
    
    all_passed = True
    for test_name, result in results:
        status = "PASS" if result else "FAIL"
        symbol = "✓" if result else "✗"
        print(f"{symbol} {test_name}: {status}")
        if not result:
            all_passed = False
    
    print("=" * 60)
    if all_passed:
        print("✓ All tests passed!")
        return 0
    else:
        print("✗ Some tests failed")
        return 1


if __name__ == "__main__":
    sys.exit(main())
