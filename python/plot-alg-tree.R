#! /usr/bin/Rscript

#########################################################################
# STN Visualisation (single algorithm)
# Input:  Folder with STN .RData graph objects (temp/<hash>-stn)
# Output: Network plots (pdf) saved in folder - using a tree graph layout
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

# ---------- Inputs ----------
args = commandArgs(trailingOnly=TRUE)
if (length(args) < 1) {
  stop("One argument is required: the input folder with stn objects. A 2nd argument is an optional numeric size factor.", call.=FALSE)
}
infolder <- file.path("temp", args[1])
if (!dir.exists(infolder)) {
  stop("Input folder does not exist", call.=FALSE)
}

outfolder <- paste0(infolder,"-plot-tree")
if (!dir.exists(outfolder)) dir.create(outfolder, recursive = TRUE)
cat("Output folder: ", outfolder, "\n")

size_factor <- if (length(args) > 1) as.numeric(args[2]) else 1
if (is.na(size_factor)) stop("2nd argument is not a number", call.=FALSE)

# Paquetes requeridos (sin instalación automática)
ensure_packages(c("igraph", "jsonlite"))

# ---- Colores y formas ----
best_ncol  <-  "red"
std_ncol   <-  "gray70"
end_ncol   <-  "gray30"
start_ncol <-  "gold"

impru_ecol <- "gray50"
equal_ecol <- rgb(0,0,250, max = 255, alpha = 180)
worse_ecol <- rgb(0,250,0, max = 255, alpha = 180)

mytriangle <- function(coords, v=NULL, params) {
  vertex.color <- params("vertex", "color")
  if (length(vertex.color) != 1 && !is.null(v)) vertex.color <- vertex.color[v]
  vertex.size <- 1/200 * params("vertex", "size")
  if (length(vertex.size) != 1 && !is.null(v)) vertex.size <- vertex.size[v]
  symbols(x=coords[,1], y=coords[,2], bg=vertex.color, col = vertex.color,
          stars=cbind(vertex.size, vertex.size, vertex.size),
          add=TRUE, inches=FALSE)
}
add_shape("triangle", clip=shapes("circle")$clip, plot=mytriangle)

legend.txt   <- c("Start", "End", "Medium", "Best", "Improve", "Equal", "Worse")
legend.col   <- c(start_ncol, end_ncol, std_ncol, best_ncol,impru_ecol,equal_ecol,worse_ecol)
legend.shape <- c(15,17,21,21,NA,NA,NA)
legend.lty   <- c(NA,NA,NA,NA,1,1,1)

plotNet <-function(N, tit, nsizef, ewidthf, asize, ecurv, mylay) {
  maxns <-  max(V(N)$size)
  if (maxns  > 100) {
    nsize <-  nsizef * sqrt(V(N)$size)  + 1
  } else if (maxns  > 10) {
    nsize <- nsizef * 0.5*V(N)$size   + 1
  } else {
    nsize <-  nsizef * V(N)$size
  }
  ewidth <- ewidthf * E(N)$width
  print(paste(tit,'Nodes:',vcount(N), 'Edges:',ecount(N), 'Comp:', components(N)$no))
  plot(N, layout = mylay, vertex.label = "", vertex.size = nsize, main = tit,
       edge.width = ewidth, edge.arrow.size = asize, edge.curved = ecurv)
  legend("bottomleft", legend.txt, pch = legend.shape, col = legend.col,
         pt.bg=legend.col, lty = legend.lty, horiz = TRUE,
         cex = 0.7, pt.cex=1.35, bty = "n")
}

stn_decorate <- function(N, bmin)  {
  el<-as_edgelist(N)
  fits<-V(N)$Fitness
  names<-V(N)$name
  f1<-fits[match(el[,1],names)]
  f2<-fits[match(el[,2],names)]
  if (bmin) {
    E(N)[which(f2<f1)]$Type = "improving"
    E(N)[which(f2>f1)]$Type = "worsening"
  } else {
    E(N)[which(f2>f1)]$Type = "improving"
    E(N)[which(f2<f1)]$Type = "worsening"
  }
  E(N)[which(f2==f1)]$Type = "equal"
  E(N)$color[E(N)$Type=="improving"] = impru_ecol
  E(N)$color[E(N)$Type=="equal"]     = equal_ecol
  E(N)$color[E(N)$Type=="worsening"] = worse_ecol
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
  V(N)$size <- strength(N, mode="in") + 1
  V(N)[V(N)$Type == "best"]$size <- V(N)[V(N)$Type == "best"]$size + 0.5
  return(N)
}

stn_plot <- function(inst)  {
  print(inst)
  fname <- file.path(infolder, inst)
  load(fname, verbose = FALSE)  # carga STN, nruns, bmin, best
  STN <- stn_decorate(STN, bmin)
  rt <- which(V(STN)$Type == "start")
  lt <- layout_as_tree(STN, root=rt, circular = FALSE)
  base <- sub("\\.RData$", "", inst, ignore.case = TRUE)
  pdf_file <- file.path(outfolder, paste0(base, ".pdf"))
  pdf(pdf_file)
  print(pdf_file)
  plotNet(N = STN, tit="Tree Layout", nsizef=size_factor, ewidthf=size_factor *.5, asize=0.3,
          ecurv=0.3, mylay=lt)
  dev.off()
  
  # Export JSON with node data
  json_file <- file.path(outfolder, paste0(base, ".json"))
  nodes_data <- data.frame(
    id = V(STN)$name,
    type = V(STN)$Type,
    size = V(STN)$size,
    color = V(STN)$color,
    shape = V(STN)$shape,
    x_fr = lt[,1],
    y_fr = lt[,2],
    x_kk = lt[,1],  # Tree layout uses same coordinates
    y_kk = lt[,2],
    stringsAsFactors = FALSE
  )
  
  edges_list <- as_edgelist(STN, names = TRUE)
  edges_data <- data.frame(
    from = edges_list[,1],
    to = edges_list[,2],
    color = E(STN)$color,
    width = E(STN)$width,
    stringsAsFactors = FALSE
  )
  
  json_obj <- list(
    algorithm = base,
    nodes = nodes_data,
    edges = edges_data,
    stats = list(
      node_count = vcount(STN),
      edge_count = ecount(STN),
      component_count = components(STN)$no
    )
  )
  
  write_json(json_obj, json_file, pretty = TRUE, auto_unbox = TRUE)
  cat("Generado JSON:", json_file, "\n")
  
  return(vcount(STN))
}

# ---- Procesar sólo los .RData ----
dataf <- list.files(infolder, pattern = "\\.RData$", ignore.case = TRUE)
if (length(dataf) == 0) {
  stop("No .RData files found in: ", infolder, call. = FALSE)
}
print(infolder)
print(dataf)
nsizes <- lapply(dataf, stn_plot)
print("Number of nodes in STNs plotted:")
print(as.numeric(nsizes))