package ch.unibas.dmi.hs17.dis.datagen

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.parquet.ParquetLocalStorage
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

import scala.util.{Failure, Success, Try}

/**
  * Created by silvan on 06.04.17.
  */
object Generator extends Logging with Config {

  val MAX_TUPLES_PER_BATCH = 20

  /**
    * Writes rows*[[DataFrame]] to the specified filepath, each with the specified amount of columns
    */
  def generateDF(rows: Int, cols: Int, stringLength: Int, filepath: String)(implicit ac: AppContext): Try[Unit] = {
    try {
      val sparkSession = ac.sparkSession

      //data
      val limit = math.min(rows, MAX_TUPLES_PER_BATCH)
      (0 until rows).sliding(limit, limit).foreach { seq =>
        log.debug("starting generating data")
        val rdd = ac.sc.parallelize(
          seq.map(idx => {
            val data = Seq.fill(cols)(generateRandomString(stringLength))
            Row(data: _*)
          })
        )
        log.debug("Generated rdd")
        //In the end, everything is a string
        val schema = StructType(Seq.tabulate(cols)(el => StructField(el.toString, StringType)))
        val data = ac.sparkSession.createDataFrame(rdd, schema)
        data.show()

        log.debug("inserting generated data")
        val status = ParquetLocalStorage.write(filepath, data, SaveMode.Overwrite)

        if (status.isFailure) {
          log.error("batch contained error, aborting random data insertion")
          throw status.failed.get
        }
      }

      Success()
    } catch {
      case t: Throwable => Failure(t)
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

}
