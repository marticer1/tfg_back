#! /usr/bin/Rscript

#########################################################################
# Network Analysis of Search Trajectory Networks (STN)
# Computing metrics of STN networks in a folder
# Input:  Folder name with files containing STN graph objects (.RData)
# Output: CSV file with metrics saved alongside the folder (temp/<hash>-stn-metrics.csv)
#
# NOTA: No instala paquetes. Asegúrate de tener igraph instalado en la
#       librería de usuario antes de ejecutar.
#########################################################################

# ---------------- Helper: load required packages without installing ----------------
ensure_packages <- function(pkgs) {
  # Añade la librería de usuario si existe
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
  invisible(lapply(pkgs, function(p) suppressPackageStartupMessages(library(p, character.only = TRUE))))
}

# ---------- Processing inputs from command line ----------
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) {
  stop("Uso: Rscript metrics-alg.R <folder>", call. = FALSE)
}

infolder <- file.path("temp", args[1])
if (!dir.exists(infolder)) {
  stop("Input folder does not exist: ", infolder, call. = FALSE)
}

# Cargar paquetes requeridos (sin instalación automática)
ensure_packages(c("igraph"))

# Esquema de salida y tipos
col_types <- c("character", "integer", "integer", "integer", "integer", "integer",
               "numeric", "numeric",  "integer")
col_names <- c("instance", "nodes", "edges", "nbest", "nend", "components",
               "strength","plength", "npaths")

metrics <- read.table(text = "", colClasses = col_types, col.names = col_names)

# Listar solo archivos .RData (evita CSV u otros)
instances <- list.files(infolder, pattern = "\\.RData$", ignore.case = TRUE)

if (!length(instances)) {
  stop("No .RData files found in: ", infolder, call. = FALSE)
}

i <- 1
for (inst in instances) {
  message("Procesando: ", inst)
  fname <- file.path(infolder, inst)
  load(fname, verbose = FALSE)

  if (!exists("STN")) {
    warning("Archivo sin objeto STN: ", inst, " (omitido)")
    next
  }

  # Nombre base del archivo sin extensión .RData
  iname <- sub("\\.RData$", "", inst, ignore.case = TRUE)

  # Métricas básicas
  metrics[i, "instance"]   <- iname
  metrics[i, "nodes"]      <- igraph::vcount(STN)
  metrics[i, "edges"]      <- igraph::ecount(STN)
  metrics[i, "components"] <- igraph::components(STN)$no

  best_ids  <- which(igraph::V(STN)$Type == "best")
  start_ids <- which(igraph::V(STN)$Type == "start")
  end_ids   <- which(igraph::V(STN)$Type == "end")

  metrics[i, "nbest"] <- length(best_ids)
  metrics[i, "nend"]  <- length(end_ids)

  # strength: normalizada por nruns si está disponible en el RData
  if (length(best_ids) > 0) {
    best_str <- sum(igraph::strength(STN, vids = best_ids, mode = "in"))
    # Si nruns no existe en el .RData, dejar NA y avisar
    if (!exists("nruns") || is.null(nruns) || is.na(nruns)) {
      warning("nruns ausente en ", inst, "; 'strength' se marcará como NA")
      metrics[i, "strength"] <- NA_real_
    } else {
      metrics[i, "strength"] <- round(best_str / nruns, 4)
    }

    if (length(start_ids) > 0) {
      dg <- igraph::distances(STN, v = start_ids, to = best_ids, mode = "out", weights = NULL)
      d  <- dg[is.finite(dg)]
      if (length(d)) {
        metrics[i, "plength"] <- round(mean(d), 4)
        metrics[i, "npaths"]  <- length(d)
      } else {
        metrics[i, "plength"] <- NA_real_
        metrics[i, "npaths"]  <- 0L
      }
    } else {
      metrics[i, "plength"] <- NA_real_
      metrics[i, "npaths"]  <- 0L
    }
  } else {
    metrics[i, "strength"] <- NA_real_
    metrics[i, "plength"]  <- NA_real_
    metrics[i, "npaths"]   <- 0L
  }

  i <- i + 1
}

# Guardar CSV
ofname <- paste0(infolder, "-metrics.csv")
utils::write.csv(metrics, file = ofname, row.names = FALSE)
message("Métricas guardadas en: ", ofname)