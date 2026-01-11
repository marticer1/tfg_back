#! /usr/bin/Rscript

#########################################################################
# Network Analysis Search Trajectory Networks (STN) - Visualisation
# Single algorithm plotting (no automatic package installation).
# Input:  Folder with STN .RData graph objects: temp/<hash>-stn
# Output: PDF plots in: temp/<hash>-stn-plot
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
  invisible(lapply(pkgs, function(p) library(p, character.only = TRUE)))
}

# ---------------- Processing inputs ----------------
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 1) {
  stop("Uso: Rscript plot-alg.R <hash>-stn [size_factor]", call. = FALSE)
}

infolder <- file.path("temp", args[1])
if (!dir.exists(infolder)) {
  stop("Input folder does not exist: ", infolder, call. = FALSE)
}

outfolder <- paste0(infolder, "-plot")
if (!dir.exists(outfolder)) dir.create(outfolder, recursive = TRUE)
cat("Output folder: ", outfolder, "\n")

size_factor <- if (length(args) > 1) as.numeric(args[2]) else 1
if (is.na(size_factor)) stop("size_factor inválido (no numérico)", call. = FALSE)

# ---------------- Load required packages ----------------
ensure_packages(c("igraph", "jsonlite"))

# ---------------- Colours & shapes ----------------
best_ncol  <- "red"
std_ncol   <- "gray70"
end_ncol   <- "gray30"
start_ncol <- "gold"

impru_ecol <- "gray50"
equal_ecol <- rgb(0,0,250, max = 255, alpha = 180)
worse_ecol <- rgb(0,250,0, max = 255, alpha = 180)

# Triangle vertex shape definition
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

legend.txt   <- c("Start", "End", "Medium", "Best", "Improve", "Equal", "Worse")
legend.col   <- c(start_ncol, end_ncol, std_ncol, best_ncol, impru_ecol, equal_ecol, worse_ecol)
legend.shape <- c(15, 17, 21, 21, NA, NA, NA)
legend.lty   <- c(NA, NA, NA, NA, 1, 1, 1)

# ---------------- Plot function ----------------
plotNet <- function(N, tit, nsizef, ewidthf, asize, ecurv, mylay) {
  maxns <- max(V(N)$size)
  if (maxns > 100) {
    nsize <- nsizef * sqrt(V(N)$size) + 1
  } else if (maxns > 10) {
    nsize <- nsizef * 0.5 * V(N)$size + 1
  } else {
    nsize <- nsizef * V(N)$size
  }
  ewidth <- ewidthf * E(N)$width
  main_title <- paste(tit, "Nodes:", vcount(N), "Edges:", ecount(N), "Comp:", components(N)$no)
  print(main_title)
  plot(N, layout = mylay, vertex.label = "", vertex.size = nsize, main = main_title,
       edge.width = ewidth, edge.arrow.size = asize, edge.curved = ecurv)
  legend("topleft", legend.txt, pch = legend.shape, col = legend.col,
         pt.bg = legend.col, lty = legend.lty,
         cex = 0.7, pt.cex = 1.35, bty = "n")
}

# ---------------- Decorate STN ----------------
stn_decorate <- function(N, bmin) {
  el    <- as_edgelist(N)
  fits  <- V(N)$Fitness
  names <- V(N)$name
  f1 <- fits[match(el[,1], names)]
  f2 <- fits[match(el[,2], names)]
  if (bmin) {
    # Minimización: mejora si f2 < f1
    E(N)[which(f2 < f1)]$Type <- "improving"
    E(N)[which(f2 > f1)]$Type <- "worsening"
  } else {
    # Maximización: mejora si f2 > f1
    E(N)[which(f2 > f1)]$Type <- "improving"
    E(N)[which(f2 < f1)]$Type <- "worsening"
  }
  E(N)[which(f2 == f1)]$Type <- "equal"

  E(N)$color[E(N)$Type=="improving"] <- impru_ecol
  E(N)$color[E(N)$Type=="equal"]     <- equal_ecol
  E(N)$color[E(N)$Type=="worsening"] <- worse_ecol

  E(N)$width <- E(N)$weight

  V(N)$color <- std_ncol
  V(N)[V(N)$Type == "start"]$color <- start_ncol
  V(N)[V(N)$Type == "end"]$color   <- end_ncol
  V(N)[V(N)$Type == "best"]$color  <- best_ncol

  V(N)$shape <- "circle"
  V(N)[V(N)$Type == "start"]$shape <- "square"
  V(N)[V(N)$Type == "end"]$shape   <- "triangle"

  V(N)$frame.color <- V(N)$color
  V(N)[V(N)$Type == "best"]$frame.color <- "white"

  V(N)$size <- strength(N, mode = "in") + 1
  V(N)[V(N)$Type == "best"]$size <- V(N)[V(N)$Type == "best"]$size + 0.5
  N
}

# ---------------- Export JSON with node/edge data ----------------
export_json <- function(STN, layout_fr, layout_kk, algorithm_name, json_file) {
  # Prepare node data
  nodes_data <- data.frame(
    id = V(STN)$name,
    type = V(STN)$Type,
    size = V(STN)$size,
    color = V(STN)$color,
    shape = V(STN)$shape,
    x_fr = layout_fr[,1],
    y_fr = layout_fr[,2],
    x_kk = layout_kk[,1],
    y_kk = layout_kk[,2],
    stringsAsFactors = FALSE
  )
  
  # Prepare edge data
  edges_list <- as_edgelist(STN, names = TRUE)
  edges_data <- data.frame(
    from = edges_list[,1],
    to = edges_list[,2],
    color = E(STN)$color,
    width = E(STN)$width,
    stringsAsFactors = FALSE
  )
  
  # Create JSON structure
  json_obj <- list(
    algorithm = algorithm_name,
    nodes = nodes_data,
    edges = edges_data,
    stats = list(
      node_count = vcount(STN),
      edge_count = ecount(STN),
      component_count = components(STN)$no
    )
  )
  
  # Write JSON file
  write_json(json_obj, json_file, pretty = TRUE, auto_unbox = TRUE)
  cat("Generado JSON:", json_file, "\n")
}

# ---------------- Single file plot ----------------
stn_plot <- function(inst) {
  cat("Procesando:", inst, "\n")
  fname <- file.path(infolder, inst)
  load(fname, verbose = FALSE)  # Espera objetos: STN, bmin, best, nruns
  STN <- stn_decorate(STN, bmin)

  layout_fr <- layout.fruchterman.reingold(STN)
  layout_kk <- layout.kamada.kawai(STN)

  base <- sub("\\.RData$", "", inst, ignore.case = TRUE)
  pdf_file <- file.path(outfolder, paste0(base, "_stn.pdf"))
  pdf(pdf_file)
  plotNet(STN, "FR Layout.", size_factor, size_factor * 0.5, 0.3, 0.3, layout_fr)
  plotNet(STN, "KK Layout.", size_factor * 0.8, size_factor * 0.4, 0.2, 0.3, layout_kk)
  dev.off()
  cat("Generado PDF:", pdf_file, "\n")
  
  # Export JSON with node data
  json_file <- file.path(outfolder, paste0(base, "_stn.json"))
  export_json(STN, layout_fr, layout_kk, base, json_file)
  
  vcount(STN)
}

# ---------------- List & filter .RData files ----------------
dataf <- list.files(infolder, pattern = "\\.RData$", ignore.case = TRUE)
if (!length(dataf)) {
  stop("No .RData files found in: ", infolder, call. = FALSE)
}
print(infolder)
print(dataf)

nsizes <- lapply(dataf, stn_plot)
cat("Número de nodos en STNs ploted:\n")
print(as.numeric(nsizes))