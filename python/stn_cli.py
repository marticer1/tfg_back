#!/usr/bin/env python3
r"""
CLI for STN (Search Trajectory Network) generation without Flask dependencies.
This allows direct command-line execution of STN processing.

Nota: Ejemplo de rutas Windows se escribe como C:\\... para evitar warnings.
"""

import argparse
import os
import sys
import re

from stn_core import (
    Params, AgglomerativeClusteringParams, StandardParams,
    process_partition, generate_from_files, generate_from_file
)

SCRIPTS_DIR = os.path.dirname(os.path.abspath(__file__))
COLOR_RE = re.compile(r"^#[0-9a-fA-F]{6}$")


class InMemoryUploadedFile:
    """Wrapper to simulate file upload interface for local files."""
    def __init__(self, path):
        self._path = path
        self.filename = os.path.basename(path)

    def read(self):
        with open(self._path, 'rb') as f:
            return f.read()


def parse_file_arg(farg: str):
    r"""
    Parse a --file argument.
    Formatos soportados:
      path:name:#RRGGBB
      path|name|#RRGGBB

    Si la ruta contiene ':' (como en Windows: C:\\...), se reensambla correctamente.
    """
    if not farg:
        raise ValueError("Empty --file argument")

    sep = None
    colon_count = farg.count(":")
    pipe_count = farg.count("|")
    if colon_count >= 2:
        sep = ":"
        parts = farg.split(":")
    elif pipe_count >= 2:
        sep = "|"
        parts = farg.split("|")
    else:
        raise ValueError(
            f"--file must contain at least two separators (':' or '|'). Received: {farg}"
        )

    if len(parts) < 3:
        raise ValueError(
            f"--file must have format path{sep}name{sep}#color. Received: {farg}"
        )

    path = sep.join(parts[:-2]).strip()
    name = parts[-2].strip()
    color = parts[-1].strip()

    if not path:
        raise ValueError("Parsed empty path in --file argument.")
    if not name:
        raise ValueError("Parsed empty algorithm name in --file argument.")
    if not COLOR_RE.match(color):
        raise ValueError(f"Color '{color}' does not match format #RRGGBB.")

    path = os.path.normpath(path)
    if not os.path.exists(path):
        raise FileNotFoundError(f"File does not exist: {path}")

    safe_name = re.sub(r"[^\w \-]", "_", name)
    return path, safe_name, color


def main():
    parser = argparse.ArgumentParser(
        description="CLI for generating STN without HTTP server",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=r"""
Examples:
  %(prog)s --typeproblem discrete --strategy standard \
    --bmin 1 --best 100 --nruns 30 --nodesize 1 --arrowsize 0.15 \
    --treelayout false --hash-file mytest \
    --partition-factor 3 --min-bound 0 --max-bound 100 \
    --file "C:\\ruta\\alg1.txt:Algorithm1:#FF0000" \
    --file "C:\\ruta\\alg2.txt:Algorithm2:#00FF00"
        """
    )

    parser.add_argument("--typeproblem", required=True, choices=["discrete", "continuous"])
    parser.add_argument("--strategy", required=True, choices=["standard", "agglomerative"])
    parser.add_argument("--bmin", type=int, default=1)
    parser.add_argument("--best", type=int, default=0)
    parser.add_argument("--nruns", type=int, default=0)
    parser.add_argument("--nodesize", required=True, type=float,
                        help="Node size for visualization")
    parser.add_argument("--arrowsize", required=False, type=float, default=0.15,
                        help="Arrow size for visualization")
    parser.add_argument("--treelayout", required=True, type=str, choices=["true", "false"])
    parser.add_argument("--hash-file", required=True, dest="hash_file")

    parser.add_argument("--partition-factor", type=int, default=0)
    parser.add_argument("--min-bound", type=int, default=0)
    parser.add_argument("--max-bound", type=int, default=0)
    parser.add_argument("--partition-value", type=float, default=0.0)

    parser.add_argument("--cluster-size", type=float, default=50.0)
    parser.add_argument("--volume-size", type=float, default=50.0)
    parser.add_argument("--num-clusters", type=int, default=-1)
    parser.add_argument("--distance-method", type=str, default="euclidean",
                        choices=["euclidean", "manhattan", "hamming"])

    parser.add_argument("--file", action="append", default=[],
                        help="Algorithm file in format path:name:#color (can be repeated). Supports '|' as alternative separator.")

    args = parser.parse_args()

    file_paths, names, colors = [], [], []
    for farg in args.file:
        try:
            path, name, color = parse_file_arg(farg)
        except (ValueError, FileNotFoundError) as e:
            print(f"ERROR: {e}", file=sys.stderr)
            return 2
        file_paths.append(path)
        names.append(name)
        colors.append(color)

    if not file_paths:
        print("ERROR: At least one --file argument is required", file=sys.stderr)
        return 2

    files = [InMemoryUploadedFile(p) for p in file_paths]
    treelayout = args.treelayout.lower() == "true"

    aggl = AgglomerativeClusteringParams(
        args.cluster_size,
        args.volume_size,
        None if args.num_clusters == -1 else int(args.num_clusters),
        args.distance_method
    )
    standard = StandardParams(args.partition_factor, args.partition_value,
                              args.min_bound, args.max_bound)

    params = Params(
        bmin=args.bmin,
        best=args.best,
        nruns=args.nruns,
        partition_value=args.partition_value,
        nodesize=args.nodesize,
        arrowsize=args.arrowsize,
        treelayout=treelayout,
        files=files,
        names=names,
        colors=colors,
        hash_file=args.hash_file,
        typeproblem=args.typeproblem,
        strategy_partition=args.strategy,
        agglomerative_clustering=aggl,
        standard_configuration=standard
    )

    os.chdir(SCRIPTS_DIR)

    if params.agglomerative_clustering.number_of_clusters:
        params.hash_file = f"{params.hash_file}-{params.agglomerative_clustering.number_of_clusters}"
    else:
        process_partition(params)

    if len(params.files) > 1:
        pdf_path_rel, json_path_rel = generate_from_files(params)
    else:
        pdf_path_rel, json_path_rel = generate_from_file(params)

    print(pdf_path_rel)
    print(json_path_rel)
    return 0


if __name__ == "__main__":
    sys.exit(main())