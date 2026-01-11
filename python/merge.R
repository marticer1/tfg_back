#! /usr/bin/Rscript

#########################################################################
# Network Analysis of Search Trajectory Networks (STN)
# Construction of merged STN network of several algorithms
# Authors: Gabriela Ochoa, Katherine Malan, Christian Blum
# Date: May 2021
#
# Input:  temp/<hash>-stn  (folder containing one *_stn.RData per algorithm)
# Output: temp/<hash>-stn-merged.RData (merged STN graph object)
#########################################################################

# ---------------- Helper: load required packages without installing automatically ----------
ensure_packages <- function(pkgs) {
  user_lib <- file.path(Sys.getenv("USERPROFILE"), "R", "win-library", getRversion())
  if (dir.exists(user_lib)) {
    .libPaths(c(user_lib, .libPaths()))
  }
  missing <- setdiff(pkgs, rownames(installed.packages()))
  if (length(missing)) {
    stop(
      paste0(
        "Faltan paquetes: ", paste(missing, collapse = ", "),
        ". Instálalos previamente con: install.packages(c(\"",
        paste(missing, collapse="\",\""), "\"))"
      ),
      call. = FALSE
    )
  }
  invisible(lapply(pkgs, function(p) library(p, character.only = TRUE)))
}

# ---------------- Argumentos ----------------
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) {
  stop("Uso: Rscript merge.R <hash>-stn", call. = FALSE)
}

infolder <- file.path("temp", args[1])
if (!dir.exists(infolder)) {
  stop("Input folder does not exist: ", infolder, call. = FALSE)
}

# ---------------- Cargar paquetes requeridos (sin instalación automática) ----------------
ensure_packages(c("igraph", "dplyr", "tidyr", "gtools"))

# ---------------- Leer ficheros RData ----------------
data_files <- gtools::mixedsort(list.files(infolder, pattern = "_stn\\.RData$", ignore.case = TRUE),
                                decreasing = TRUE)

cat("Input folder: ", infolder, "\n")
num_alg <- length(data_files)

if (num_alg < 2) {
  stop("Se necesitan al menos 2 algoritmos para merge (encontrados: ", num_alg, ")", call. = FALSE)
}
if (num_alg > 3) {
  warning("El script está pensado para 2 o 3 algoritmos; detectados: ", num_alg)
}

alg_list <- vector("list", num_alg)
alg_names <- character(num_alg)

i <- 1
for (f in data_files) {
  alg_name <- sub("_stn\\.RData$", "", f, ignore.case = TRUE)
  fname <- file.path(infolder, f)
  load(fname, verbose = FALSE) # Espera objeto STN + (bmin, best, nruns) de cada archivo
  if (!exists("STN")) stop("Archivo ", f, " no contiene objeto STN", call. = FALSE)

  # Añadir atributo Alg a nodos y aristas antes de guardar en lista
  V(STN)$Alg <- alg_name
  E(STN)$Alg <- alg_name

  alg_names[i] <- alg_name
  alg_list[[i]] <- STN
  i <- i + 1
}

# ---------------- Unión de grafos ----------------
# graph.union con lista (versiones modernas de igraph permiten Reduce con union)
stnm <- graph.union(alg_list)

# ---------------- Combinar atributos numerados (Fitness, Count) ----------------
# Tras la unión, igraph numera atributos repetidos: Fitness_1, Fitness_2, ...
# Los combinamos usando coalesce y rowSums.
fitness_expr <- paste0("coalesce(", paste(paste0("V(stnm)$Fitness_", 1:num_alg), collapse = ","), ")")
count_expr   <- paste0("cbind(", paste(paste0("V(stnm)$Count_",   1:num_alg), collapse = ","), ")")

V(stnm)$Fitness <- eval(parse(text = fitness_expr))
count_mat <- eval(parse(text = count_expr))
V(stnm)$Count <- rowSums(count_mat, na.rm = TRUE)

# ---------------- Combinar atributos de tipo texto (Type, Alg) ----------------
type_df_expr <- paste0("data.frame(", paste(paste0("V(stnm)$Type_", 1:num_alg), collapse = ","), ")")
alg_df_expr  <- paste0("data.frame(", paste(paste0("V(stnm)$Alg_",  1:num_alg), collapse = ","), ")")

type_df <- eval(parse(text = type_df_expr))
alg_df  <- eval(parse(text = alg_df_expr))

# Unir columnas (concatenación directa). Se eliminan 'NA' tras la unión.
type_df <- tidyr::unite(type_df, "Type", sep = "", remove = TRUE)
alg_df  <- tidyr::unite(alg_df,  "Alg",  sep = "", remove = TRUE)

type_df <- data.frame(lapply(type_df, function(x) gsub("NA", "", x)), stringsAsFactors = FALSE)
alg_df  <- data.frame(lapply(alg_df,  function(x) gsub("NA", "", x)), stringsAsFactors = FALSE)

V(stnm)$Type <- type_df$Type
V(stnm)$Alg  <- alg_df$Alg

# ---------------- Limpiar atributos de vértice antiguos ----------------
old_vattr <- unlist(lapply(c("Fitness","Count","Type","Alg"),
                           function(a) paste0(a, "_", 1:num_alg)))
for (att in old_vattr) {
  if (att %in% vertex_attr_names(stnm)) {
    stnm <- delete_vertex_attr(stnm, att)
  }
}

# ---------------- Combinar atributos de arista (weight, Alg) ----------------
weight_df_expr <- paste0("cbind(", paste(paste0("E(stnm)$weight_", 1:num_alg), collapse = ","), ")")
weight_mat <- eval(parse(text = weight_df_expr))
E(stnm)$weight <- rowSums(weight_mat, na.rm = TRUE)

edge_alg_expr <- paste0("data.frame(", paste(paste0("E(stnm)$Alg_", 1:num_alg), collapse = ","),
                        ", stringsAsFactors = FALSE)")
edge_alg_df <- eval(parse(text = edge_alg_expr))
edge_alg_df <- tidyr::unite(edge_alg_df, "Alg", sep = "", remove = TRUE)
edge_alg_df <- data.frame(lapply(edge_alg_df, function(x) gsub("NA", "", x)), stringsAsFactors = FALSE)
E(stnm)$Alg <- edge_alg_df$Alg

# Limpiar atributos de arista antiguos
old_eattr <- unlist(lapply(c("weight","Alg"),
                           function(a) paste0(a, "_", 1:num_alg)))
for (att in old_eattr) {
  if (att %in% edge_attr_names(stnm)) {
    stnm <- delete_edge_attr(stnm, att)
  }
}

# ---------------- Etiqueta de nodos compartidos ----------------
V(stnm)$Shared <- TRUE
for (nm in alg_names) {
  # Nodos visitados sólo por un algoritmo (cadena exacta del nombre de algoritmo)
  V(stnm)[V(stnm)$Alg == nm]$Shared <- FALSE
}

# ---------------- Guardar salida ----------------
# Asumimos (bmin, best, nruns) existentes en el entorno del último load;
# si falta alguno, se pone NA con aviso.
if (!exists("bmin"))  bmin  <- NA
if (!exists("best"))  best  <- NA
if (!exists("nruns")) nruns <- NA

ofname <- paste0(infolder, "-merged.RData")
cat("Output file: ", ofname, "\n")
save(stnm, nruns, num_alg, alg_names, bmin, best, file = ofname)