"""
Core business logic for STN (Search Trajectory Network) processing.
This module contains pure logic without Flask dependencies.
"""
from subprocess import PIPE, Popen
from partition.discrete.standard import standard as discrete_standard
from partition.continuous.standard import continuous_standard
from partition.continuous.agglomerative import continuous_agglomerative
from io import BytesIO
import os
import time
import tarfile
import shutil
import re
import functools


class Instances(object):
    """Represents instances/algorithms data."""
    index = ""
    contentLineFile = []
    filename = ""


class StandardParams(object):
    """Configuration for standard partitioning strategy."""
    def __init__(self, pf, pp, min_b, max_b) -> None:
        self.partition_factor = 10.0**int(pf)
        self.partition_percen = float(pp)
        self.min_bound = int(min_b)
        self.max_bound = int(max_b)


class AgglomerativeClusteringParams(object):
    """Configuration for agglomerative clustering strategy."""
    def __init__(self, cluster_size, volumen_size, number_of_clusters, distance_method) -> None:
        self.cluster_size = float(cluster_size)
        self.volumen_size = float(volumen_size)
        self.number_of_clusters = number_of_clusters
        self.distance_method = distance_method


class Params(object):
    """Main parameters for STN generation."""
    treelayout = False
    files = []
    names = []
    colors = []
    hash_file = ""
    
    def __init__(self, bmin, best, nruns, partition_value, nodesize, arrowsize, 
                 treelayout, files, names, colors, hash_file, typeproblem, 
                 strategy_partition, agglomerative_clustering, standard_configuration):
        self.bmin = bmin
        self.best = best
        self.nruns = nruns
        self.partition_value = partition_value
        self.nodesize = nodesize
        self.arrowsize = arrowsize
        self.treelayout = treelayout
        self.files = files
        self.names = names
        self.colors = colors
        self.hash_file = hash_file
        self.typeproblem = typeproblem
        self.strategy_partition = strategy_partition
        self.agglomerative_clustering = agglomerative_clustering
        self.standard_configuration = standard_configuration


def change_old_format(a):
    """Convert old file format to new format."""
    a = [re.sub(r"\s+", ",", a[i].strip()) for i in range(0, len(a))]

    new_content = [','.join(a[i].split(',')[:3]) + ',' + ','.join(a[i+1].split(',')[1:3]) 
                   for i in range(0, len(a)-1) 
                   if len(a[i-1].split(',')) <= 3 and a[i].split(',')[0] == a[i+1].split(',')[0]]

    return new_content or a


def is_int(str):
    """Check if string is an integer."""
    try: 
        int(str)
    except ValueError: 
        return False
    return True


def is_discrete(f):
    """Check if the problem is discrete based on file content."""
    info = f[2].split(',')
    return is_int(info[2])


def writing_file_discrete(filename, contentLineFile, partition_value, strategy_partition, cfiles):
    """Write discrete partition results to file."""
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    start = time.time()

    file = discrete_standard(contentLineFile, partition_value, cfiles)

    end = time.time()
    print("Time: {}".format(end - start))
    if file[0] != "Run,Fitness1,Solution1,Fitness2,Solution2":
        file = ["Run,Fitness1,Solution1,Fitness2,Solution2"] + file[:]
    with open(filename, "w") as f:
        for line in file:
            f.write("{}\n".format(line))


def writing_file_continuous(content_files, params, cfiles):
    """Write continuous partition results to file."""
    
    if params.strategy_partition == "standard":
        results = continuous_standard(params, cfiles)
        for algo, results in results.items():
            for i, file in enumerate(results):
                if file[0] != "Run,Fitness1,Solution1,Fitness2,Solution2":
                    _file = ["Run,Fitness1,Solution1,Fitness2,Solution2"] + file[:]
                else:
                    _file = file
                filename = "temp/{}/{}.csv".format(params.hash_file, algo)
                os.makedirs(os.path.dirname(filename), exist_ok=True)
                with open(filename, "w") as f:
                    for line in _file:
                        f.write("{}\n".format(line))
                        
    elif params.strategy_partition == "agglomerative":
        results, min_clusters = continuous_agglomerative(params, cfiles)

        for algo, results_clustering in results.items():

            for i, file in enumerate(results_clustering.clustering):
                if file[0] != "Run,Fitness1,Solution1,Fitness2,Solution2":
                    _file = ["Run,Fitness1,Solution1,Fitness2,Solution2"] + file[:]
                else:
                    _file = file
                filename = "temp/{}-{}/{}.csv".format(params.hash_file, results_clustering.number_of_clusters[i], algo)
                os.makedirs(os.path.dirname(filename), exist_ok=True)
                with open(filename, "w") as f:
                    for line in _file:
                        f.write("{}\n".format(line))
                        
        params.hash_file = f"{params.hash_file}-{min_clusters}"


def process_partition(params):
    """
    Process partitions for multiple algorithm files.
    
    Args:
        params: Params object containing configuration and files
        
    Returns:
        str: hash_file identifier for the processed data
    """
    content_files = []
    for _ in range(len(params.files)):
        content_files.append(Instances())

    for index, f in enumerate(params.files):
        if ''.join(f.filename.split('.')[1:]).find("tar") > -1:
            with tarfile.open(name=None, fileobj=BytesIO(f.read())) as f_compress:
                for entry in f_compress:  
                    file_compress = f_compress.extractfile(entry)
                    content_files[index].contentLineFile = change_old_format(
                        list(filter(None, file_compress.read().decode().split('\n')))
                    )
        else:
            content_files[index].contentLineFile = change_old_format(
                list(filter(None, f.read().decode().split('\n')))
            )
        filename = "temp/{}/{}.csv".format(params.hash_file, params.names[index])
        if params.strategy_partition == "agglomerative" or params.typeproblem == "continuous":
            content_files[index].contentLineFile = [
                params.names[index] + ',' + e for e in content_files[index].contentLineFile
            ]
        content_files[index].filename = filename
        content_files[index].index = index

    all_solutions = functools.reduce(lambda i, j: i + j, [e.contentLineFile for e in content_files])
    
    if params.typeproblem == "discrete" and params.strategy_partition == 'standard':
        for file in content_files:
            writing_file_discrete(file.filename, file.contentLineFile, 
                                params.partition_value, params.strategy_partition, all_solutions)
    elif params.typeproblem == "discrete" and params.strategy_partition == 'agglomerative':
        writing_file_continuous(content_files, params, all_solutions)
    elif params.typeproblem == "continuous":
        writing_file_continuous(content_files, params, all_solutions)

    return params.hash_file


def generate_from_files(params: Params):
    """
    Generate STN visualization from multiple algorithm files (merged).
    
    Args:
        params: Params object with configuration
        
    Returns:
        tuple: (pdf_path, json_path) - Paths to the generated PDF and JSON files
    """
    print("--> Rscript create.R {} {} {} {}".format(params.hash_file, params.bmin, params.best, params.nruns))
    with Popen("Rscript create.R {} {} {} {}".format(params.hash_file, params.bmin, params.best, params.nruns), 
               stdout=PIPE, stderr=None, shell=True) as process:
        output = process.communicate()[0]
        print("OK: {}".format(output))

    print("--> Rscript merge.R {}".format("{}-stn".format(params.hash_file)))
    with Popen("Rscript merge.R {}".format("{}-stn".format(params.hash_file)), 
               stdout=PIPE, stderr=None, shell=True) as process:
        output = process.communicate()[0]
        print("OK: {}".format(output))

    print("--> Rscript plot-merged.R {} {} {} {}".format(
        "{}-stn-merged.RData".format(params.hash_file), params.nodesize, params.arrowsize, " ".join(params.colors)))
    pdf_path = ""
    json_path = ""
    if params.treelayout:
        with Popen("Rscript plot-merged-tree.R {} {} {}".format(
                "{}-stn-merged.RData".format(params.hash_file), params.nodesize, 
                " ".join(["{}{}{}".format("\"", c, "\"") for c in params.colors])), 
                stdout=PIPE, stderr=None, shell=True) as process:
            output = process.communicate()[0]
            print("OK: {}".format(output))
            pdf_path = "temp/{}-stn-merged-plot-tree.pdf".format(params.hash_file)
            json_path = "temp/{}-stn-merged-plot-tree.json".format(params.hash_file)
    else:
        with Popen("Rscript plot-merged.R {} {} {} {}".format(
                "{}-stn-merged.RData".format(params.hash_file), params.nodesize, params.arrowsize, 
                " ".join(["{}{}{}".format("\"", c, "\"") for c in params.colors])), 
                stdout=PIPE, stderr=None, shell=True) as process:
            output = process.communicate()[0]
            print("OK: {}".format(output))
            pdf_path = "temp/{}-stn-merged-plot.pdf".format(params.hash_file)
            json_path = "temp/{}-stn-merged-plot.json".format(params.hash_file)

    with Popen("Rscript metrics-merged.R {}".format("{}-stn-merged.RData".format(params.hash_file)), 
               stdout=PIPE, stderr=None, shell=True) as process:
        output = process.communicate()[0]
        print("OK: {}".format(output))  
        shutil.rmtree("temp/" + params.hash_file)
        shutil.rmtree("temp/{}-stn".format(params.hash_file))

    return pdf_path, json_path


def generate_from_file(params: Params):
    """
    Generate STN visualization from single algorithm file.
    
    Args:
        params: Params object with configuration
        
    Returns:
        tuple: (pdf_path, json_path) - Paths to the generated PDF and JSON files
    """
    print("--> Rscript create.R {} {} {} {}".format(params.hash_file, params.bmin, params.best, params.nruns))
    with Popen("Rscript create.R {} {} {} {}".format(params.hash_file, params.bmin, params.best, params.nruns), 
               stdout=PIPE, stderr=None, shell=True) as process:
        output = process.communicate()[0]
        print("OK: {}".format(output))
    pdf_path = ""
    json_path = ""
    if params.treelayout:
        print("--> Rscript plot-alg-tree.R {} {}".format("{}-stn".format(params.hash_file), params.nodesize))
        with Popen("Rscript plot-alg-tree.R {} {}".format("{}-stn".format(params.hash_file), params.nodesize), 
                   stdout=PIPE, stderr=None, shell=True) as process:
            output = process.communicate()[0]
            print("OK: {}".format(output))
            pdf_path = "temp/{}-stn-plot-tree/{}_stn.pdf".format(params.hash_file, params.names[0])
            json_path = "temp/{}-stn-plot-tree/{}_stn.json".format(params.hash_file, params.names[0])
    else:
        print("--> Rscript plot-alg.R {} {}".format("{}-stn".format(params.hash_file), params.nodesize))
        with Popen("Rscript plot-alg.R {} {}".format("{}-stn".format(params.hash_file), params.nodesize), 
                   stdout=PIPE, stderr=None, shell=True) as process:
            output = process.communicate()[0]
            print("OK: {}".format(output))
            pdf_path = "temp/{}-stn-plot/{}_stn.pdf".format(params.hash_file, params.names[0])
            json_path = "temp/{}-stn-plot/{}_stn.json".format(params.hash_file, params.names[0])

    with Popen("Rscript metrics-alg.R {}".format("{}-stn".format(params.hash_file)), 
               stdout=PIPE, stderr=None, shell=True) as process:
        output = process.communicate()[0]
        print("OK: {}".format(output))
        
    return pdf_path, json_path
