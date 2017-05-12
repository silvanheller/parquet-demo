# parquet-demo
Parquet demo project for the Workshop in the Course DIS

## What's this?
This is intended to benchmark read and write performances of different file formats.
The goal is to show both cases where Parquet is superior.

## Runtime Dependencies
* JVM

## Development Dependencies
* Install IntelliJ (which comes bundled with sbt)

For Plotting:
* Install R
* Install RStudio
* Install ggplot2 with _install.packages("ggplot2")_
* Install RStudioAPI with _install.packages("rstudioapi")_

## Configuration / Running the Application
Set your desired benchmark values @ main/EvaluationRunner

Press 'run'. That's it.

The created data will be stored in the workshop/ folder.
Benchmark values will be in the /results folder.

Create your plots by running plots/plot.r and changing the filename to the timestamp of your results/results_*.tsv file