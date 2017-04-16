# parquet-demo
Parquet demo project for the Workshop in the Course DIS

##What's this?
This is intended to benchmark read and write performances of different file formats.
The goal is to show both cases where Parquet is superior and inferior.

##Runtime Dependencies
* JVM

##Development Dependencies
* Install R
* Install RStudio
* Install ggplot2 with _install.packages("ggplot2")_
* Install RStudioAPI with _install.packages("rstudioapi")_

##Configuration
Set your desired benchmark values @ main/EvaluationRunner

Set your Spark Environment @ main/EvaluationRunner

Show your plots by running plots/plot.r and changing the filename to the timestamp of your results/results_*.tsv file