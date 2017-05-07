package ch.unibas.dmi.hs17.dis.datagen

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.storage
import ch.unibas.dmi.hs17.dis.storage.StorageMode._
import ch.unibas.dmi.hs17.dis.storage._
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row}

import scala.util.{Failure, Random, Success, Try}

/**
  * Created by silvan on 06.04.17.
  */
object Generator extends Logging with Config with Serializable {

  case class Adam(name: String, age: Int)

  case class Grandmum(adam: Adam, name: String, age: Int)

  case class Granddad(adam: Adam, name: String, age: Int)

  case class Father(granddad: Granddad, grandmum: Grandmum, name: String, age: Int)

  case class Mother(granddad: Granddad, grandmum: Grandmum, name: String, age: Int)

  case class PersonCC(father: Father, mother: Mother, personName: String, personAge: Int)

  //Automatically implements the product interface

  def genAndWritePersonDF(rows: Int, filepath: String, storageMode: storage.StorageMode.Value)(implicit ac: AppContext): Try[Unit] = {
    try {
      val adam = Adam("adam", 2000)
      //data
      val limit = math.min(rows, MAX_TUPLES_PER_BATCH)

      val spark = ac.sparkSession
      import spark.implicits._

      (0 until rows).sliding(limit, limit).foreach { seq =>
        val data: IndexedSeq[PersonCC] = seq.map(idx => {
          val grandmummum = Grandmum(adam, generateRandomString(10), (Random.nextDouble() * 30 + 70).toInt)
          val grandmumdad = Grandmum(adam, generateRandomString(10), (Random.nextDouble() * 30 + 70).toInt)
          val grandadmum = Granddad(adam, generateRandomString(10), (Random.nextDouble() * 30 + 70).toInt)
          val grandaddad = Granddad(adam, generateRandomString(10), (Random.nextDouble() * 30 + 70).toInt)
          val dad = Father(grandaddad, grandmumdad, generateRandomString(10), (Random.nextDouble() * 30 + 20).toInt)
          val mum = Mother(grandaddad, grandmummum, generateRandomString(10), (Random.nextDouble() * 30 + 20).toInt)
          PersonCC(dad, mum, generateRandomString(10), (Random.nextDouble() * 5).toInt)
        })
        log.debug("Finished generating person")
        val rdd: RDD[PersonCC] = ac.sc.parallelize(data)
        log.debug("Finished generating rdd")

        val df = ac.sparkSession.createDataFrame(rdd)

        val status = writeDF(filepath, df, storageMode)
        df.unpersist() //Remove cache
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


  val MAX_TUPLES_PER_BATCH = 10000

  /**
    * Writes rows*[[DataFrame]] to the specified filepath, each with the specified amount of columns
    */
  def genAndWriteStringDF(rows: Int, cols: Int, stringLength: Int, filepath: String, storageMode: StorageMode)(implicit ac: AppContext): Try[Unit] = {
    try {
      //data
      val limit = math.min(rows, MAX_TUPLES_PER_BATCH)
      (0 until rows).sliding(limit, limit).foreach { seq =>
        val data: IndexedSeq[Row] = seq.map(idx => {
          val data = Seq.fill(cols)(generateRandomString(stringLength))
          Row(Seq(idx) ++ data: _*)
        })
        val rdd: RDD[Row] = ac.sc.parallelize(data)

        //In the end, everything is a string
        val schema = StructType(Seq(StructField("id", IntegerType)) ++ Seq.tabulate(cols)(el => StructField(el.toString + "c", StringType)))
        val df = ac.sparkSession.createDataFrame(rdd, schema)

        val status = writeDF(filepath, df, storageMode)
        df.unpersist() //Remove cache
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
    StorageMode.fromString(storageMode.toString).write(filepath, df)
  }

}
