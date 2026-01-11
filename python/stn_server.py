"""
Flask server for STN (Search Trajectory Network) generation.
This module provides HTTP endpoints for STN processing.
"""
from flask import Flask, send_file, request, jsonify
from flask_cors import CORS
import os
import re

# Import core business logic
from stn_core import (
    Params, StandardParams, AgglomerativeClusteringParams,
    process_partition, generate_from_files, generate_from_file
)

import logging

logging.basicConfig(level=logging.DEBUG)

app = Flask(__name__)
CORS(app)


def get_params() -> Params:
    """Extract parameters from Flask request."""
    bmin = request.form.get('bmin', "1") 
    best = request.form.get('best', "")
    nruns = request.form.get('nruns', "")
    hash_file = request.form.get('hash_file', "")
    partition_value = request.form.get('zvalue', "0.0")
    nodesize = request.form.get('nodesize', "1")
    arrowsize = request.form.get('arrowsize', "0.15")
    typeproblem = request.form.get('typeproblem', "")
    strategy_partition = request.form.get('strategy_partition', "standard")
    partition_factor = request.form.get('standard_configuration_partition_factor', 0)
    min_bound = request.form.get('standard_configuration_min_bound', 0)
    max_bound = request.form.get('standard_configuration_max_bound', 0)
    cluster_size = request.form.get('agglomerative_configuration_cluster_size', 50)
    volumen_size = request.form.get('agglomerative_configuration_volumen_size', 50)
    number_of_clusters = request.form.get('agglomerative_configuration_number_of_cluster', -1)
    distance_method = request.form.get('agglomerative_configuration_distance_method', "euclidean")

    print(number_of_clusters)
    if int(number_of_clusters) == -1:
        number_of_clusters = None
    else:
        number_of_clusters = int(number_of_clusters)
    
    print(bmin)
    print(typeproblem)
    print(strategy_partition)
    print(number_of_clusters)
    print(distance_method)
    print(partition_factor)
    print("volume: ", volumen_size)
    print("cluster_size: ", cluster_size)
    _files = request.files.getlist('file')
    _names = request.form.getlist('name[]')
    _colors = request.form.getlist('color[]')
    treelayout = True if request.form.get('treelayout', False) == "true" else False
    names = [e[0] for e in sorted(list(zip(_names, _colors)))]
    print("names:", _names, names)

    colors = [e[1] for e in sorted(list(zip(_names, _colors)))]
    print("colors:", _colors, colors)

    files = [e[1] for e in sorted(list(zip(_names, _files)))]
    print("files:", _files, files)
    agglomerative_clustering = AgglomerativeClusteringParams(cluster_size, volumen_size, 
                                                             number_of_clusters, distance_method)
    standard_configuration = StandardParams(partition_factor, partition_value, min_bound, max_bound)
    params = Params(bmin, best, nruns, partition_value, nodesize, arrowsize, treelayout, files, names, 
                   colors, hash_file, typeproblem, strategy_partition, agglomerative_clustering, 
                   standard_configuration)

    if params.bmin != "":
        params.bmin = int(params.bmin)
    
    if params.partition_value != "":
        params.partition_value = float(params.partition_value)

    if params.nruns != "":
        if params.best != "":
            params.best = int(params.best)
        else: 
            params.best = 12
        params.nruns = int(params.nruns)
    return params


@app.route("/agglomerative-info", methods=['POST'])
def get_agglomerative_info():
    """Get information about agglomerative clustering results."""
    hash_file = request.form.get('hash_file', "")
    print(hash_file)
    clusters = [int(e.split('-')[-1]) for e in list(filter(re.compile(f"{hash_file}-[0-9]+$").match, os.listdir("temp")))]

    if clusters:
        return jsonify(
            limit_init=min(clusters),
            limit_end=max(clusters)
        )
    else:
        number_one_cluster = [int(e.split('-')[1]) for e in list(filter(re.compile(f"{hash_file}-[0-9]+").match, os.listdir("temp")))][0]
        return jsonify(
            limit_init=number_one_cluster,
            limit_end=number_one_cluster
        )


@app.route("/stn-metrics", methods=['POST'])
def get_metrics():
    """Get metrics CSV file for an STN."""
    hash_file = "temp/" + request.form.get('hash_file', "")
    print(hash_file)
    return send_file(hash_file, mimetype='text/csv', as_attachment=True)


@app.route("/stn", methods=['POST'])
def generate():
    """Main endpoint to generate STN visualization."""
    params: Params = get_params()
    if params.agglomerative_clustering.number_of_clusters:
        params.hash_file = f"{params.hash_file}-{params.agglomerative_clustering.number_of_clusters}" 
    else:
        process_partition(params)
    
    if len(params.files) > 1:
        return send_file(generate_from_files(params), mimetype='application/pdf', as_attachment=True)
    else:
        return send_file(generate_from_file(params), mimetype='application/pdf', as_attachment=True)


if __name__ == '__main__':
    # Note: debug=True should only be used in development
    # In production, set debug=False or use environment variable
    import os
    debug_mode = os.getenv('FLASK_DEBUG', 'False').lower() == 'true'
    app.run(host='0.0.0.0', port=5000, threaded=True, debug=debug_mode)
