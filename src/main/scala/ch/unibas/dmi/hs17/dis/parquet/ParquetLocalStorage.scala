package ch.unibas.dmi.hs17.dis.parquet

import java.io.File

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.commons.io.FileUtils
import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.util.{Failure, Success, Try}

/**
  * Stores [[DataFrame]] to HDFS
  *
  * Created by silvan on 05.04.17.
  */
object ParquetLocalStorage extends Logging with Serializable with Config {

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
  def write(filename: String, df: DataFrame, mode: SaveMode = SaveMode.Append): Try[Void] = {
    try {
      df.write.mode(mode).parquet(filename)
      Success(null)
    } catch {
      case e: Exception => Failure(e)
    }
  }

  def drop(filename: String)(implicit ac: AppContext): Try[Unit] = {
    try {
      val file = new File(filename)
      if (file.isFile) {
        val delete = file.delete()
        if (delete) return Success()
        return Failure(new Exception("Unknown failure while deleting file " + filename))
      }
      FileUtils.deleteDirectory(new File(filename))
      Success()
    } catch {
      case e: Exception => Failure(e)
    }
  }


  def exists(filename: String)(implicit ac: AppContext): Try[Boolean] = {
    try {
      val file = new File(filename)
      Success(file.exists())
    } catch {
      case e: Exception => Failure(e)
    }
  }
}
