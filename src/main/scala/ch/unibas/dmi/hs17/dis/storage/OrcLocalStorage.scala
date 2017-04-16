package ch.unibas.dmi.hs17.dis.storage

import ch.unibas.dmi.hs17.dis.main.AppContext
import org.apache.spark.sql.{SaveMode, _}

import scala.util.{Failure, Success, Try}

/**
  * Created by silvan on 10.04.17.
  */
object OrcLocalStorage extends FileStorage {

  def read(filename: String)(implicit ac: AppContext): Try[DataFrame] = {
    try {
      if (!exists(filename).get) {
        throw new IllegalArgumentException("no file found at " + filename)
      }
      Success(ac.sparkSession.read.option("header", "true").orc(filename))
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
      df.write.mode(mode).option("header", "true").orc(filename)
      Success()
    } catch {
      case e: Exception => Failure(e)
    }
  }
}
