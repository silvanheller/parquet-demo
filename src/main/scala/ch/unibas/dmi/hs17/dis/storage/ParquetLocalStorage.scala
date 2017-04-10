package ch.unibas.dmi.hs17.dis.storage

import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.util.{Failure, Success, Try}

/**
  * Stores [[DataFrame]] to HDFS
  *
  * Created by silvan on 05.04.17.
  */
object ParquetLocalStorage extends FileStorage with Logging{

  def read(filename: String)(implicit ac: AppContext): Try[DataFrame] = {
    try {
      if (!exists(filename).get) {
        throw new IllegalArgumentException("no file found at " + filename)
      }

      Success(ac.sparkSession.read.parquet(filename))
    } catch {
      case e: Exception => Failure(e)
    }
  }

  /**
    * Write a dataframe to the specified file
    *
    * @param mode Default is [[SaveMode.Append]]
    */
  def write(filename: String, df: DataFrame, mode: SaveMode = SaveMode.Append): Try[Unit] = {
    try {
      log.debug("Writing parquet file")
      df.write.mode(mode).parquet(filename)
      Success()
    } catch {
      case e: Exception => Failure(e)
    }
  }
}
