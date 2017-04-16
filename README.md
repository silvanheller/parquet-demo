# parquet-demo
Parquet demo project for the Workshop in the Course DIS

## What's this?
This is intended to benchmark read and write performances of different file formats.
The goal is to show both cases where Parquet is superior and inferior.

## Runtime Dependencies
* JVM

## Development Dependencies
* Install R
* Install RStudio
* Install ggplot2 with _install.packages("ggplot2")_
* Install RStudioAPI with _install.packages("rstudioapi")_

## Configuration
Set your desired benchmark values @ main/EvaluationRunner

Set your Spark Environment @ main/EvaluationRunner

Show your plots by running plots/plot.r and changing the filename to the timestamp of your results/results_*.tsv file

## Possible Conclusions
* ORC is best for flat structures
* Parquet could be better for nested objects (?)
* ORC creates per default a light index with each file, [See Orc Documentation](https://orc.apache.org/docs/indexes.html)

## Possible Additional queries
* Row count operation
* Sum of column
* Selecting 4 columns from a given range using where clause