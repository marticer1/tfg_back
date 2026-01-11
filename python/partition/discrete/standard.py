import math
import operator
from hashlib import sha256
from typing import Dict, Iterable, List, Set


class Stats:
    def __init__(self, pos: int):
        self.pos = pos
        self.one_freq = 0.0
        self.entropy = 0.0


def _safe_split_line(line: str):
    """
    Intenta dividir una línea esperada en 5 campos:
    Run,Fitness1,Solution1,Fitness2,Solution2
    Devuelve None si no encaja.
    """
    parts = line.split(',')
    if len(parts) != 5:
        return None
    return parts


def standard(datainput: Iterable[str], c: float, all_solutions: Iterable[str]):
    """
    Procesamiento estándar (discreto):
    datainput: iterable de líneas "Run,Fitness1,Solution1,Fitness2,Solution2"
    c: porcentaje (0..100) de variables a mantener (coarsening). Si c = 0 se usan soluciones completas.
    all_solutions: iterable adicional con soluciones globales (puede estar vacío).
    Retorna lista CSV con encabezado y líneas resultantes.
    """
    # Validación y conversión defensiva de c
    try:
        c = float(c)
    except (TypeError, ValueError):
        raise ValueError(f"partition_value debe ser numérico, recibido: {c!r}")
    if c < 0:
        raise ValueError(f"partition_value (c) no puede ser negativo: {c}")

    print("Start partitions")

    partitions_one_algorithm: Set[str] = set()
    partitions_global: Set[str] = set()
    partitions_idx_map: Dict[int, List[tuple]] = {}
    fitness_map_global: Dict[str, int] = {}

    # Parse datainput
    for raw in datainput:
        if not raw:
            continue
        parsed = _safe_split_line(raw)
        if not parsed:
            # Línea malformada, se ignora
            continue
        idx, fitness1, bin1, fitness2, bin2 = parsed
        if idx == "Run":
            continue
        try:
            idx_int = int(idx)
            f1 = int(fitness1)
            f2 = int(fitness2)
        except ValueError:
            # Datos mal formados, ignoramos la línea
            continue
        bin1 = bin1.rstrip()
        bin2 = bin2.rstrip()

        if idx_int not in partitions_idx_map:
            partitions_idx_map[idx_int] = []
        partitions_idx_map[idx_int].append((f1, bin1))
        partitions_idx_map[idx_int].append((f2, bin2))

        partitions_one_algorithm.add(bin1)
        partitions_one_algorithm.add(bin2)

    # Parse all_solutions adicional (si viene vacío se rellena luego)
    input_all_solutions = list(all_solutions) if all_solutions else []
    for raw in input_all_solutions:
        if not raw:
            continue
        parsed = _safe_split_line(raw)
        if not parsed:
            continue
        _, fitness1, bin1, fitness2, bin2 = parsed
        try:
            f1 = int(fitness1)
            f2 = int(fitness2)
        except ValueError:
            continue
        partitions_global.add(bin1)
        partitions_global.add(bin2)
        fitness_map_global[bin1] = f1
        fitness_map_global[bin2] = f2

    # Si no hay soluciones globales, derivarlas del propio datainput
    if not partitions_global:
        partitions_global = partitions_one_algorithm.copy()

    # Confirmar que hay al menos una solución
    if not partitions_one_algorithm:
        print("WARNING: No se encontraron soluciones en datainput. Retornando sólo encabezado.")
        return ["Run,Fitness1,Solution1,Fitness2,Solution2"]

    solutions_list = list(partitions_one_algorithm)
    # Comprobar longitud uniforme
    lengths = {len(s) for s in solutions_list}
    if len(lengths) != 1:
        raise ValueError("Las soluciones tienen longitudes distintas; no se puede calcular entropía de forma coherente.")
    total_nodes = lengths.pop()

    # stats
    stats: List[Stats] = [Stats(i) for i in range(total_nodes)]

    # Calcular frecuencia de '1' por posición
    # Usamos partitions_global (el conjunto final de soluciones para entropía)
    for i in range(total_nodes):
        count_ones = sum(1 for sol in partitions_global if sol[i] == '1')
        stats[i].one_freq = count_ones / float(len(partitions_global))

    # Calcular entropía
    for st in stats:
        p1 = st.one_freq
        p0 = 1.0 - p1
        if p1 > 0.0:
            st.entropy += p1 * math.log2(p1)
        if p0 > 0.0:
            st.entropy += p0 * math.log2(p0)
        st.entropy *= -1.0

    # Ordenar por entropía descendente
    stats.sort(key=operator.attrgetter('entropy'), reverse=True)

    # Calcular totalVars una sola vez
    if c == 0:
        total_vars = total_nodes  # usar solución completa
    else:
        total_vars = int(math.floor((total_nodes * c) / 100.0))
        if total_vars <= 0:
            total_vars = 1

    print(f"total nodes: {total_nodes}")
    print(f"total vars (después de aplicar c={c}): {total_vars}")

    # Representaciones coarsen
    repr_map: Dict[str, str] = {}
    reduced_fitness: Dict[str, int] = {}

    # Si no tenemos fitness global (porque venían vacías all_solutions), lo derivamos mínimo desde datainput
    if not fitness_map_global:
        # Construir fitness básico con un valor arbitrario (0) si no estaba
        for sol in partitions_global:
            fitness_map_global.setdefault(sol, 0)

    for sol in partitions_global:
        # Construir la versión reducida
        reduced_bits = [sol[stats[j].pos] for j in range(total_vars)]
        coarse = ''.join(reduced_bits)
        repr_map[sol] = coarse

        # Fitness mínimo para coarse
        current_fit = fitness_map_global.get(sol, 0)
        if coarse not in reduced_fitness:
            reduced_fitness[coarse] = current_fit
        else:
            if current_fit < reduced_fitness[coarse]:
                reduced_fitness[coarse] = current_fit

    results = ["Run,Fitness1,Solution1,Fitness2,Solution2"]

    # Construcción de pares en cada índice (recorre datainput ya agrupado)
    for run_idx, pair_list in partitions_idx_map.items():
        # pair_list = [(fitness, bin), (fitness, bin), ...]
        bins = [b for (_, b) in pair_list]
        # Recorremos de dos en dos
        for i in range(0, len(bins) - 1, 2):
            sol_a = bins[i]
            sol_b = bins[i + 1]
            if c > 0:
                # Comparar representaciones reducidas
                coarse_a = repr_map[sol_a]
                coarse_b = repr_map[sol_b]
                if coarse_a != coarse_b:
                    # Hash de la representación reducida
                    h_a = sha256(coarse_a.encode("utf-8")).hexdigest()
                    h_b = sha256(coarse_b.encode("utf-8")).hexdigest()
                    fa = reduced_fitness[coarse_a]
                    fb = reduced_fitness[coarse_b]
                    results.append(f"{run_idx},{fa},{h_a},{fb},{h_b}")
            else:
                # Sin coarsening: usar soluciones completas hasheadas
                h_a = sha256(sol_a.encode("utf-8")).hexdigest()
                h_b = sha256(sol_b.encode("utf-8")).hexdigest()
                fa = fitness_map_global.get(sol_a, 0)
                fb = fitness_map_global.get(sol_b, 0)
                results.append(f"{run_idx},{fa},{h_a},{fb},{h_b}")

    return results