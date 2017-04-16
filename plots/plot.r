rm(list = ls())
library(ggplot2)
library(plyr)

##Ctrl + L clears console of Rstudio

#Be careful with the command below, it only works in rstudio
setwd(dirname(rstudioapi::getActiveDocumentContext()$path))
getwd()
###############################################
## Enter your Filename here
###############################################
fn <- "0416_1844"
###############################################
path <- paste("plots/", fn, sep = '')
dir.create(path, recursive = TRUE,  mode = "0777", showWarnings = FALSE)
df <- read.table(paste("../results/results_", fn, ".tsv", sep = ''),
                 header = TRUE,
                 sep = "\t")

qt <- "Execution Time in ms"

#saves a plot under the given filename
save <- function(fun, name) {
  eval(fun)
  if(file.exists(toString(paste(getwd(), "/plots/",fn,"/",name,"",sep='')))){
    #Semi-caches
    return() 
  }
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
      title = paste(op,"time for",rowCount,"rows,",colCount,"cols","and ",slen,"String Length\n"),
      x = "Storage Mode",
      y = qt,
      fill = "Storage Mode"
    )
}

#time versus colcount
timeCol <- function(rowCount = 10000, slen  = 100, op = "Write") {
  plot <- ggplot(df, aes(cols, time))
  plot + geom_line(
    data = subset(df, rows == rowCount &
                    stringlen == slen & 
                    operation == op),
    aes(colour = factor(storageMode))
  )+
    labs(
      title = paste(op,"time for",rowCount,"rows","and ",slen,"String Length\n"),
      x = "Number of columns",
      y = qt,
      colour = "Storage Mode"
    )
}

#time versus rowcount
timeRow <- function(colCount=100, slen  = 100, op = "Write") {
  plot <- ggplot(df, aes(rows, time))
  plot + geom_line(
    data = subset(df, cols == colCount &
                    stringlen == slen & 
                    operation == op),
    aes(colour = factor(storageMode))
  )+
    labs(
      title = paste(op,"time for",colCount,"cols","and",slen,"String Length\n"),
      x = "Number of rows",
      y = qt,
      colour = "Storage Mode"
    )
}

for (rows in unique(df$rows)) {
  for (cols in unique(df$cols)) {
    for (stringlen in unique(df$stringlen)) {
      for(operation in unique(df$operation)){
        ##Simple comparison of storage modes
        ##save(
          ##storagePlot(rowCount = rows, colCount = cols, slen = stringlen, op = operation),
          ##paste(operation,"Time_", rows, "r_", cols, "c_",stringlen, "sl.png", sep = '')
        ##)
        
        ##Exploded for col count
        save(
          timeCol(rowCount = rows, slen = stringlen, op = operation),
          paste(operation,"Colplot_", rows, "r_",stringlen, "sl.png", sep = '')
        )
        save(
          timeRow(colCount = cols, slen = stringlen, op = operation),
          paste(operation,"Rowplot_", rows, "r_",stringlen, "sl.png", sep = '')
        )
      }
    }
  }
}