#! /usr/bin/Rscript

#########################################################################
# Network Analysis of Search Trajectory Networks (STN)
# Authors: Gabriela Ochoa, Katherine Malan, Christian Blum
# Date: May 2021
# STN construction for single algorithms. 
# Input:  Folder containing text file trace of runs, number of runs
# Output: STN graph objects - saved in output folder 
#########################################################################

# ---------- Helper: ensure required packages without installing automatically ----------
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
        paste(missing, collapse = "\",\""), "\"))"
      ),
      call. = FALSE
    )
  }
  invisible(lapply(pkgs, function(p) library(p, character.only = TRUE)))
}

# ---------- Processing inputs from command line ----------
args = commandArgs(trailingOnly=TRUE)
if (length(args) < 1) {
  stop("The first argument is required, arguments 2 to 4 are optional: \
       1) Name of the input folder
       2) Boolean indicating minimisation (1) or maximisation (0). If no argument is given, minimisation (i.e 1) is assumed.
       3) The evaluation of the global optimum (or best-known solution). For continuous optimisation a desired precision can be given.
       4) The number of runs from the data files to be used.", call.=FALSE)
}

infolder <- file.path("temp", args[1])
if (!dir.exists(infolder)) {
  stop("Error: Input folder does not exist", call.=FALSE)
}

# Defaults
bmin <- 1
best <- NA
nruns <- NA

if (length(args) > 1) {
  bmin <- as.integer(args[2]); if (is.na(bmin)) stop("Error: 2nd argument is not a number", call.=FALSE)
}
if (length(args) > 2) {
  best <- as.numeric(args[3]); if (is.na(best)) stop("Error: 3rd argument is not a number", call.=FALSE)
}
if (length(args) > 3) {
  nruns <- as.integer(args[4]); if (is.na(nruns)) stop("Error: 4th argument is not a number", call.=FALSE)
}

# Output folder
outfolder <- paste0(infolder, "-stn")
if (!dir.exists(outfolder)) dir.create(outfolder, recursive = TRUE)
cat("Output folder: ", outfolder, "\n")

# Paquetes requeridos (sin instalación automática)
ensure_packages(c("igraph", "plyr"))

# --------- Funciones principales ----------
stn_create <- function(instance)  {
  fname <- file.path(infolder, instance)
  print(fname)
  file_ext <- substr(fname, nchar(fname)-3, nchar(fname))
  mysep <- ifelse(file_ext == ".csv", ",","")
  trace_all <- read.table(fname, header=TRUE, sep = mysep,
                          colClasses=c("integer", "numeric", "character", "numeric", "character"),
                          stringsAsFactors = FALSE)
  # Si nruns no se especifica, infiérelo luego (fuera) y aquí no filtres todavía
  if (!is.na(nruns)) {
    trace_all <- trace_all[trace_all$Run <= nruns,]
  } else {
    nruns_local <- max(trace_all$Run)
  }

  lnodes <- vector("list", ifelse(is.na(nruns), nruns_local, nruns))
  ledges <- vector("list", ifelse(is.na(nruns), nruns_local, nruns))

  # Detectar nodos start/end
  k <- 1
  end_ids <- vector()
  start_ids <- vector()
  n <- nrow(trace_all)-1
  start_ids[1] <- trace_all$Solution1[1]
  for (j in 1:n) {
    if (trace_all$Run[j] != trace_all$Run[j+1]) {
      end_ids[k] <- trace_all$Solution2[j]
      start_ids[k+1] <- trace_all$Solution1[j+1]
      k <- k + 1
    }
  }
  end_ids[k] <- trace_all$Solution2[j+1]
  end_ids <- unique(end_ids)
  start_ids <- unique(start_ids)

  runs_max <- ifelse(is.na(nruns), nruns_local, nruns)
  for (i in 1:runs_max) {
    trace <- trace_all[which(trace_all$Run==i),c(-1)]
    colnames(trace) <- c("fit1", "node1", "fit2", "node2")
    lnodes[[i]] <- rbind(setNames(trace[,c("node1","fit1")], c("Node", "Fitness")),
                         setNames(trace[,c("node2","fit2")], c("Node", "Fitness")))
    ledges[[i]] <- trace[,c("node1", "node2")]
  }

  nodes <- plyr::ddply((do.call("rbind", lnodes)), .(Node,Fitness), nrow)
  colnames(nodes) <- c("Node", "Fitness", "Count")
  nodesu <- nodes[!duplicated(nodes$Node), ]

  edges <- plyr::ddply(do.call("rbind", ledges), .(node1,node2), nrow)
  colnames(edges) <- c("Start","End", "weight")

  STN <- igraph::graph_from_data_frame(d = edges, directed = TRUE, vertices = nodesu)
  STN <- igraph::simplify(STN, remove.multiple = FALSE, remove.loops = TRUE)

  if (bmin) {
    best_ids <- which(igraph::V(STN)$Fitness <= best)
  } else {
    best_ids <- which(igraph::V(STN)$Fitness >= best)
  }

  igraph::V(STN)$Type <- "medium"
  igraph::V(STN)[end_ids]$Type <- "end"
  igraph::V(STN)[start_ids]$Type <- "start"
  igraph::V(STN)[best_ids]$Type <- "best"

  base <- sub("\\.[^.]+$", "", instance)
  rdata <- file.path(outfolder, paste0(base, "_stn.RData"))
  save(STN, nruns, bmin, best, file=rdata)
  return(igraph::vcount(STN))
}

# ---- Cargar datos de entrada ----
data_files <- list.files(infolder)

# Rellenar best/nruns si faltan
if (is.na(best) || is.na(nruns))  {
  get_data <- function(instance) {
    file_ext <- substr(instance, nchar(instance)-3, nchar(instance))
    mysep <- ifelse(file_ext == ".csv", ",", "")
    read.table(file.path(infolder, instance), header=TRUE, sep = mysep,
               colClasses=c("integer", "numeric", "character", "numeric", "character"),
               stringsAsFactors = FALSE)
  }
  dfs <- lapply(data_files, get_data)
  if (is.na(best)) {
    v <- unlist(lapply(dfs, function(x) x[["Fitness2"]]), recursive=TRUE)
    best <- ifelse(bmin, min(v), max(v))
    cat("Best value in data:", best, "\n")
  }
  if (is.na(nruns)) {
    nruns <- max(unlist(lapply(dfs, function(x) x[["Run"]]), recursive=TRUE))
    cat("Number of runs in data:", nruns, "\n")
  }
  rm(dfs)
}

nsizes <- lapply(data_files, stn_create)
print("Number of nodes in the STNs created:")
print(as.numeric(nsizes))