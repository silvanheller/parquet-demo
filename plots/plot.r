rm(list = ls())
library(ggplot2)
library(plyr)

##Ctrl + L clears console of Rstudio

#Be careful with the command below, it only works in rstudio
setwd(dirname(rstudioapi::getActiveDocumentContext()$path))
getwd()
fn <- "0410_1649"
path <- paste("plots/", fn, sep = '')
dir.create(path, recursive = TRUE,  mode = "0777", showWarnings = FALSE)
df <- read.table(paste("../results/results_", fn, ".tsv", sep = ''),
                 header = TRUE,
                 sep = "\t")

qt <- "Execution Time in ms"

#saves a plot under the given filename
save <- function(fun, name) {
  eval(fun)
  ggsave(
    filename = toString(name),
    width = 8,
    height = 6,
    dpi = 300,
    path = toString(paste(getwd(), "/plots/", fn, "", sep = '')),
    plot = last_plot()
  )
}

#time by storage mode
storagePlot <- function(rowCount = 10000, colCount = 100, slen  = 100, op = "Write") {
  plot <- ggplot(df, aes(storageMode, time))
  plot + geom_bar(
    stat="identity",
    data = subset(df, rows == rowCount &
                    cols == colCount & 
                    stringlen == slen & 
                    operation == op),
    aes(fill = storageMode)
  )+
    labs(
      title = paste("Write time for",rowCount,"rows,",colCount,"cols","and ",slen,"Content Length\n"),
      x = "Storage Mode",
      y = qt,
      fill = "Storage Mode"
    )
}

for (rows in unique(df$rows)) {
  for (cols in unique(df$cols)) {
    for (stringlen in unique(df$stringlen)) {
      for(operation in unique(df$operation)){
        save(
          storagePlot(rowCount = rows, colCount = cols, slen = stringlen, op = operation),
          paste("optime_", rows, "_", cols, "_",stringlen, "_", operation, ".png", sep = '')
        )  
      }
    }
  }
}