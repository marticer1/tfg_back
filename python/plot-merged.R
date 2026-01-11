#! /usr/bin/Rscript

#########################################################################
# Visualisation of merged STN network (several algorithms)
# Input:  temp/<hash>-stn-merged.RData   (merged STN object file)
# Args:
#   1) <hash>-stn-merged.RData   (obligatorio)
#   2) size_factor               (opcional, numérico, escala nodos/aristas)  [default 1]
#   3) size_arrow                (opcional, tamaño flechas)                  [default 0.3]
#   4..) colores hex (#RRGGBB) para cada algoritmo en orden alfabet. (opcional)
# Output: temp/<hash>-stn-merged-plot.pdf
#
# NOTA: No instala paquetes. Debes tener igraph ya instalado en tu librería de usuario.
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
  stop("Uso: Rscript plot-merged.R <hash>-stn-merged.RData [size_factor] [size_arrow] [color_alg_1] [color_alg_2] ...",
       call. = FALSE)
}

infile <- file.path("temp", args[1])
if (!file.exists(infile)) {
  stop("No existe el fichero de entrada: ", infile, call. = FALSE)
}

size_factor <- if (length(args) > 1) suppressWarnings(as.numeric(args[2])) else 1
size_arrow  <- if (length(args) > 2) suppressWarnings(as.numeric(args[3])) else 0.3
if (is.na(size_factor)) stop("Argumento size_factor inválido (no numérico)", call.=FALSE)
if (is.na(size_arrow))  stop("Argumento size_arrow inválido (no numérico)", call.=FALSE)

# Colores opcionales para algoritmos (a partir del 4º argumento)
alg_col_raw <- if (length(args) > 3) args[4:length(args)] else character()
# Validar colores hex (#RRGGBB)
valid_hex <- grepl("^#[0-9A-Fa-f]{6}$", alg_col_raw)
if (length(alg_col_raw) && any(!valid_hex)) {
  stop("Colores inválidos: ", paste(alg_col_raw[!valid_hex], collapse = ", "),
       ". Formato esperado: #RRGGBB", call.=FALSE)
}
alg_col <- alg_col_raw

# ---------------- Parámetros de color base ----------------
best_ncol     <- "red"
end_run_ncol  <- "gray30"
start_ncol    <- "gold"
shared_col    <- "gray70"

# Construir vectores para leyenda (algoritmos se añaden tras cargar el objeto con sus nombres reales)
legend.shape.base <- c(15, 17, 16)  # Start, End, Best
# Cada algoritmo será dibujado como punto (pch 16), Shared también como punto
# Se completará después de conocer 'algn' (nombres cargados)

#################################################################
# Forma triángulo (añadir shape personalizado)
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

# ---------------- Función de pintado ----------------
plotNet <- function(N, tit, nsizef, ewidthf, asize, ecurv, mylay, bleg = TRUE,
                    legend.txt = NULL, legend.col = NULL, legend.shape = NULL) {
  nsize  <- nsizef  * V(N)$size
  ewidth <- ewidthf * E(N)$width
  plot(N, layout = mylay, vertex.label = "", vertex.size = nsize,
       edge.width = ewidth, main = tit,
       edge.arrow.size = asize, edge.curved = ecurv)
  if (bleg && !is.null(legend.txt)) {
    legend("topleft", legend.txt, pch = legend.shape, col = legend.col,
           cex = 0.7, pt.cex = 1.4, bty = "n")
  }
}

# ---------------- Decoración del STN merged ----------------
stn_decorate <- function(N, alg_colors, alg_names) {
  # Por defecto nodos 'shared'
  V(N)$color <- shared_col

  # Nodos visitados por un único algoritmo: color del algoritmo
  # Un nodo se considera "single" si su atributo Alg coincide exactamente con un nombre de algoritmo
  for (i in seq_along(alg_names)) {
    sel <- V(N)$Alg == alg_names[i]
    V(N)[sel]$color <- if (length(alg_colors) >= i) alg_colors[i] else V(N)[sel]$color
  }

  start_nodes <- grepl("start", V(N)$Type, fixed = TRUE)
  end_nodes   <- grepl("end",   V(N)$Type, fixed = TRUE)
  best_nodes  <- grepl("best",  V(N)$Type, fixed = TRUE)

  V(N)[start_nodes]$color <- start_ncol
  V(N)[end_nodes]$color   <- end_run_ncol
  V(N)[best_nodes]$color  <- best_ncol

  # Marcos
  V(N)$frame.color <- V(N)$color
  V(N)[V(N)$color == shared_col]$frame.color <- "gray40"
  V(N)[best_nodes]$frame.color <- "white"

  # Formas
  V(N)$shape <- "circle"
  V(N)[end_nodes]$shape   <- "triangle"
  V(N)[start_nodes]$shape <- "square"

  # Tamaños
  V(N)$size <- strength(N, mode = "in") + 1
  V(N)[end_nodes]$size  <- V(N)[end_nodes]$size  + 0.3
  V(N)[best_nodes]$size <- V(N)[best_nodes]$size + 0.6

  # Aristas
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

# ---------------- Subgrafo por fitness (cuantiles) ----------------
subFit <- function(N, fvalue) {
  induced.subgraph(N, V(N)$Fitness <= fvalue)
}

# ---------------- Cargar objeto merged ----------------
load(infile, verbose = FALSE)  # Carga: stnm, alg_names?, algn, num_alg, bmin, best ...
if (!exists("stnm")) stop("El archivo no contiene objeto 'stnm'", call.=FALSE)
if (!exists("algn")) {
  # Compatibilidad: si el merge guardó 'alg_names'
  if (exists("alg_names")) {
    algn <- alg_names
  } else {
    warning("No se encontró 'algn' en el RData; se asume vector vacío.")
    algn <- character()
  }
}

# Decorar grafo
stnm <- stn_decorate(stnm, alg_col, algn)

# ---------------- Layouts ----------------
l_fr <- layout.fruchterman.reingold(stnm)
l_kk <- layout.kamada.kawai(stnm)

# Zoom (top 25% por fitness - primer cuartil)
q25 <- as.numeric(quantile(V(stnm)$Fitness, probs = 0.25, na.rm = TRUE))
zoom <- subFit(stnm, q25)
zoom <- delete.vertices(zoom, degree(zoom) == 0)
lz_fr <- layout.fruchterman.reingold(zoom)
lz_kk <- layout.kamada.kawai(zoom)

# ---------------- Leyenda dinámica ----------------
legend.txt <- c("Start", "End", "Best", algn, "Shared")
legend.col <- c(start_ncol, end_run_ncol, best_ncol,
                if (length(alg_col)) alg_col else character(),
                shared_col)
legend.shape <- c(15, 17, 16,
                  rep(16, length(algn)),
                  16)

# ---------------- Salida PDF ----------------
ofname <- sub("\\.RData$", "-plot.pdf", infile, ignore.case = TRUE)

pdf(ofname)
nf <- size_factor
ef <- size_factor * 0.7
plotNet(stnm, "FR layout", nf, ef, size_arrow, 0.3, l_fr,
        bleg = TRUE, legend.txt = legend.txt, legend.col = legend.col, legend.shape = legend.shape)
plotNet(stnm, "KK layout", nf * 0.8, ef * 0.8, size_arrow * 0.8, 0.3, l_kk,
        bleg = FALSE, legend.txt = legend.txt, legend.col = legend.col, legend.shape = legend.shape)

# Zoom plots (más grandes al tener menos nodos)
nfz <- size_factor * 1.5
efz <- size_factor
plotNet(zoom, "Zoom (top 25%) FR", nfz, efz, size_arrow, 0.3, lz_fr,
        bleg = TRUE, legend.txt = legend.txt, legend.col = legend.col, legend.shape = legend.shape)
plotNet(zoom, "Zoom (top 25%) KK", nfz * 0.8, efz * 0.8, size_arrow, 0.3, lz_kk,
        bleg = TRUE, legend.txt = legend.txt, legend.col = legend.col, legend.shape = legend.shape)
dev.off()

cat("PDF generado: ", ofname, "\n")
cat("Nodos totales merged STN:", vcount(stnm), "\n")

# ---------------- Export JSON with node/edge data ----------------
json_file <- sub("\\.RData$", "-plot.json", infile, ignore.case = TRUE)

# Prepare node data with algorithm assignments
nodes_data <- data.frame(
  id = V(stnm)$name,
  type = V(stnm)$Type,
  size = V(stnm)$size,
  color = V(stnm)$color,
  shape = V(stnm)$shape,
  fitness = V(stnm)$Fitness,
  algorithm = V(stnm)$Alg,
  x_fr = l_fr[,1],
  y_fr = l_fr[,2],
  x_kk = l_kk[,1],
  y_kk = l_kk[,2],
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