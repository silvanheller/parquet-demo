package ch.unibas.dmi.hs17.dis.main

import org.apache.spark.sql.SparkSession

import scala.annotation.implicitNotFound

/**
  * Created by silvan on 06.04.17.
  */
@implicitNotFound("Cannot find an implicit Context, either import SparkStartup.Implicits._ or use a custom one")
trait AppContext {
  def sparkSession: SparkSession

}