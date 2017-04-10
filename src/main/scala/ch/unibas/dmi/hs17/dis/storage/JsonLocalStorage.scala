package ch.unibas.dmi.hs17.dis.storage

import java.io.File

import ch.unibas.dmi.hs17.dis.main.AppContext
import org.apache.commons.io.FileUtils
import org.apache.spark.sql.{SaveMode, _}

import scala.util.{Failure, Success, Try}

/**
  * Writes / Stores data using JSON
  *
  * Created by silvan on 10.04.17.
  */
object JsonLocalStorage {

  def read(filename: String)(implicit ac: AppContext): Try[DataFrame] = {
    try {
      if (!exists(filename).get) {
        throw new IllegalArgumentException("no file found at " + filename)
      }

      Success(ac.sparkSession.read.json(filename))
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

  /**
    * Write a dataframe to the specified file
    *
    * @param mode Default is [[SaveMode.Append]]
    */
  def write(filename: String, df: DataFrame, mode: SaveMode = SaveMode.Append): Try[Unit] = {
    try {
      df.write.mode(mode).json(filename)
      Success()
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
}
