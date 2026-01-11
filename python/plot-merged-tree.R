#! /usr/bin/Rscript

#########################################################################
# Visualisation (tree layout) of merged STN network (several algorithms)
# Authors: Gabriela Ochoa, Katherine Malan, Christian Blum
# Date: May 2021 (refactor sin instalación automática de paquetes)
#
# Uso:
#   Rscript plot-merged-tree.R <merged_file.RData> [size_factor] [color_alg_1] [color_alg_2] ...
#
# Argumentos:
#   merged_file.RData   Nombre del archivo .RData del STN merged (puede venir con o sin prefijo temp/).
#   size_factor         Escala para nodos y aristas (opcional, numérico, default 1).
#   color_alg_i         Colores hex (#RRGGBB) para cada algoritmo (opcional, uno por algoritmo en orden).
#
# Salida:
#   temp/<hash>-stn-merged-plot-tree.pdf
#
# Requisitos:
#   Paquete igraph debe estar instalado previamente en la librería de usuario.
#########################################################################

# ---------------- Helper: cargar paquetes sin instalar ----------------
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
        ". Instálalos con: install.packages(c(\"",
        paste(missing, collapse="\",\""), "\"))"
      ),
      call. = FALSE
    )
  }
  invisible(lapply(pkgs, function(p) library(p, character.only = TRUE)))
}

ensure_packages(c("igraph", "jsonlite"))

# ---------------- Procesar argumentos ----------------
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) {
  stop("Uso: Rscript plot-merged-tree.R <merged_file.RData> [size_factor] [color_alg_1] ...",
       call. = FALSE)
}

raw_infile <- args[1]

# Permitir pasar sólo el nombre o la ruta completa
if (file.exists(raw_infile)) {
  infile <- raw_infile
} else if (file.exists(file.path("temp", raw_infile))) {
  infile <- file.path("temp", raw_infile)
} else {
  stop("No se encontró el archivo: ", raw_infile, "; probado también temp/", raw_infile, call.=FALSE)
}

size_factor <- if (length(args) > 1) suppressWarnings(as.numeric(args[2])) else 1
if (is.na(size_factor)) stop("size_factor inválido (no numérico)", call.=FALSE)

# Colores de algoritmos (opcional) a partir del 3er argumento
alg_col_raw <- if (length(args) > 2) args[3:length(args)] else character()
if (length(alg_col_raw)) {
  valid_hex <- grepl("^#[0-9A-Fa-f]{6}$", alg_col_raw)
  if (any(!valid_hex)) {
    stop("Colores inválidos: ", paste(alg_col_raw[!valid_hex], collapse=", "),
         ". Formato requerido: #RRGGBB", call.=FALSE)
  }
}
alg_col <- alg_col_raw

# ---------------- Configuración de colores base ----------------
best_ncol     <- "red"
end_run_ncol  <- "gray30"
start_ncol    <- "gold"
shared_col    <- "gray70"

#################################################################
# Forma triángulo personalizada
mytriangle <- function(coords, v=NULL, params) {
  vertex.color <- params("vertex", "color")
  if (length(vertex.color) != 1 && !is.null(v)) vertex.color <- vertex.color[v]
  vertex.size <- 1/200 * params("vertex", "size")
  if (length(vertex.size) != 1 && !is.null(v)) vertex.size <- vertex.size[v]
  symbols(x=coords[,1], y=coords[,2], bg=vertex.color, col=vertex.color,
          stars=cbind(vertex.size, vertex.size, vertex.size),
          add=TRUE, inches=FALSE)
}
add_shape("triangle", clip=shapes("circle")$clip, plot=mytriangle)

# ---------------- Función genérica de pintado ----------------
plotNet <- function(N, tit, nsizef, ewidthf, asize, ecurv, mylay,
                    legend.txt=NULL, legend.col=NULL, legend.shape=NULL, bleg=TRUE) {
  nsize  <- nsizef  * V(N)$size
  ewidth <- ewidthf * E(N)$width
  plot(N, layout=mylay, vertex.label="", vertex.size=nsize,
       edge.width=ewidth, main=tit,
       edge.arrow.size=asize, edge.curved=ecurv)
  if (bleg && !is.null(legend.txt)) {
    legend("bottomleft", legend.txt, pch=legend.shape, col=legend.col, horiz=TRUE,
           cex=0.7, pt.cex=1.4, bty="n")
  }
}

# ---------------- Decoración del STN merged ----------------
decorate_merged_stn <- function(N, alg_colors, alg_names) {
  # Nodos inicialmente "shared"
  V(N)$color <- shared_col

  # Nodos visitados por un solo algoritmo exacto
  for (i in seq_along(alg_names)) {
    sel <- V(N)$Alg == alg_names[i]
    if (length(alg_colors) >= i) {
      V(N)[sel]$color <- alg_colors[i]
    }
  }

  start_nodes <- grepl("start", V(N)$Type, fixed=TRUE)
  end_nodes   <- grepl("end",   V(N)$Type, fixed=TRUE)
  best_nodes  <- grepl("best",  V(N)$Type, fixed=TRUE)

  V(N)[start_nodes]$color <- start_ncol
  V(N)[end_nodes]$color   <- end_run_ncol
  V(N)[best_nodes]$color  <- best_ncol

  V(N)$frame.color <- V(N)$color
  V(N)[V(N)$color == shared_col]$frame.color <- "gray40"
  V(N)[best_nodes]$frame.color <- "white"

  V(N)$shape <- "circle"
  V(N)[start_nodes]$shape <- "square"
  V(N)[end_nodes]$shape   <- "triangle"

  V(N)$size <- strength(N, mode="in") + 1
  V(N)[end_nodes]$size  <- V(N)[end_nodes]$size  + 0.3
  V(N)[best_nodes]$size <- V(N)[best_nodes]$size + 0.6

  E(N)$Alg <- gsub("NA", "", E(N)$Alg)
  E(N)$color <- shared_col
  for (i in seq_along(alg_names)) {
    sel_e <- E(N)$Alg == alg_names[i]
    if (length(alg_colors) >= i) {
      E(N)[sel_e]$color <- alg_colors[i]
    }
  }
  E(N)$width <- E(N)$weight
  N
}

# ---------------- Cargar el objeto merged ----------------
load(infile, verbose=FALSE) # Debe cargar: stnm, algn, etc.
if (!exists("stnm")) stop("El archivo no contiene objeto 'stnm'", call.=FALSE)

# Compatibilidad si el merge guardó alg_names y no algn
if (!exists("algn")) {
  if (exists("alg_names")) {
    algn <- alg_names
  } else {
    algn <- character()
    warning("No se encontró 'algn' en el RData; se usará vector vacío.")
  }
}

# Decorar
stnm <- decorate_merged_stn(stnm, alg_col, algn)

# Layout tipo árbol (root = nodos 'start')
roots <- which(V(stnm)$Type %in% c("start", "startmedium", "startend"))
if (!length(roots)) {
  roots <- which(grepl("start", V(stnm)$Type, fixed=TRUE))
}
lt <- layout_as_tree(stnm, root=roots, circular=FALSE)

# Leyenda dinámica
legend.txt <- c("Start", "End", "Best", algn, "Shared")
legend.col <- c(start_ncol, end_run_ncol, best_ncol,
                if (length(alg_col)) alg_col else character(),
                shared_col)
legend.shape <- c(15, 17, 16,
                  rep(16, length(algn)),
                  16)

# Nombre salida PDF
ofname <- sub("\\.RData$", "-plot-tree.pdf", infile, ignore.case=TRUE)
# Asegurar que va dentro de temp/ incluso si infile venía con ruta relativa
if (!grepl("^temp(/|\\\\)", ofname)) {
  ofname <- file.path("temp", ofname)
}

pdf(ofname)
plotNet(stnm, tit="Tree layout", nsizef=size_factor, ewidthf=size_factor*0.7,
        asize=0.16, ecurv=0.3, mylay=lt,
        legend.txt=legend.txt, legend.col=legend.col, legend.shape=legend.shape, bleg=TRUE)
dev.off()

cat("PDF generado: ", ofname, "\n")
cat("Nodos merged STN:", vcount(stnm), "\n")

# ---------------- Export JSON with node/edge data ----------------
json_file <- sub("\\.RData$", "-plot-tree.json", infile, ignore.case = TRUE)
if (!grepl("^temp(/|\\\\)", json_file)) {
  json_file <- file.path("temp", json_file)
}

# Prepare node data with algorithm assignments
nodes_data <- data.frame(
  id = V(stnm)$name,
  type = V(stnm)$Type,
  size = V(stnm)$size,
  color = V(stnm)$color,
  shape = V(stnm)$shape,
  fitness = V(stnm)$Fitness,
  algorithm = V(stnm)$Alg,
  x_fr = lt[,1],
  y_fr = lt[,2],
  x_kk = lt[,1],  # Tree layout uses same coordinates
  y_kk = lt[,2],
  stringsAsFactors = FALSE
)

# Prepare edge data
edges_list <- as_edgelist(stnm, names = TRUE)
edges_data <- data.frame(
  from = edges_list[,1],
  to = edges_list[,2],
  color = E(stnm)$color,
  width = E(stnm)$width,
  stringsAsFactors = FALSE
)

# Create JSON structure
json_obj <- list(
  algorithms = algn,
  algorithmColors = if (length(alg_col)) alg_col else list(),
  nodes = nodes_data,
  edges = edges_data,
  stats = list(
    node_count = vcount(stnm),
    edge_count = ecount(stnm),
    component_count = components(stnm)$no
  )
)

# Write JSON file
write_json(json_obj, json_file, pretty = TRUE, auto_unbox = TRUE)
cat("JSON generado: ", json_file, "\n")