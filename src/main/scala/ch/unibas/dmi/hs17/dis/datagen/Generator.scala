package ch.unibas.dmi.hs17.dis.datagen

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.storage.StorageMode.{StorageMode, _}
import ch.unibas.dmi.hs17.dis.storage._
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

import scala.util.{Failure, Success, Try}

/**
  * Created by silvan on 06.04.17.
  */
object Generator extends Logging with Config {

  val MAX_TUPLES_PER_BATCH = 5000

  /**
    * Writes rows*[[DataFrame]] to the specified filepath, each with the specified amount of columns
    */
  def genAndWriteDF(rows: Int, cols: Int, stringLength: Int, filepath: String, storageMode: StorageMode)(implicit ac: AppContext): Try[Unit] = {
    try {
      //data
      val limit = math.min(rows, MAX_TUPLES_PER_BATCH)
      (0 until rows).sliding(limit, limit).foreach { seq =>
        //val broadcastSeq = ac.sc.broadcast(seq)
        //val broadcastCols = ac.sc.broadcast(cols)
        //val broadcastStringLength = ac.sc.broadcast(stringLength)
        val data: IndexedSeq[Row] = seq.map(idx => {
          val data = Seq.fill(cols)(generateRandomString(stringLength))
          Row(data: _*)
        })

        val rdd: RDD[Row] = ac.sc.parallelize(data)

        //In the end, everything is a string
        val schema = StructType(Seq.tabulate(cols)(el => StructField(el.toString, StringType)))
        val df = ac.sparkSession.createDataFrame(rdd, schema)

        val status = writeDF(filepath, df, storageMode)
        if (status.isFailure) {
          log.error("batch contained error, aborting random data insertion")
          throw status.failed.get
        }
      }
      Success()
    } catch {
      case t: Throwable => {
        t.printStackTrace()
        Failure(t)
      }
    }
  }

  def generateRandomString(length: Int): String = {
    val r = new scala.util.Random
    val sb = new StringBuilder
    for (i <- 1 to length) {
      sb.append(r.nextPrintableChar)
    }
    sb.toString()
  }

  def writeDF(filepath: String, df: DataFrame, storageMode: StorageMode): Try[Unit] = {
    storageMode match {
      case JSON => JsonLocalStorage.write(filepath, df, SaveMode.Append)
      case Parquet => ParquetLocalStorage.write(filepath, df, SaveMode.Append)
      case ORC => OrcLocalStorage.write(filepath, df, SaveMode.Append)
      case CSV => CSVLocalStorage.write(filepath, df, SaveMode.Append)
    }
  }

}
