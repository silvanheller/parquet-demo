package ch.unibas.dmi.hs17.dis.main

import java.io.File

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.datagen.Generator
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by silvan on 05.04.17.
  */
object SparkTest extends Config with Logging {

  def cleanup() = {
    log.debug("Deleting old files")
    StorageMode.values.foreach(storageMode => {
      new File(LOCAL_DATAPATH + "data." + storageMode.toString).delete()
    })
  }

    def main(args: Array[String]) {
    val mainContext = createContext()

    //implicit def ac: AppContext = mainContext
    implicit val ac = mainContext

    val rows = Seq(10, 10000)
    val cols = Seq(1, 100)
    val stringlens = Seq(5, 100)

    cleanup()

    //Iterate over all permutations
    StorageMode.values.foreach(storageMode => {
      log.debug("====================================")
      log.debug("Evaluating Storage Mode {}\n", storageMode)
      log.debug("====================================")

      rows.foreach(_row => {
        log.debug("Evaluating for row-count {}", _row)
        cols.foreach(_col => {
          log.debug("Evaluating for col-count {}", _col)
          stringlens.foreach(_stringlen => {
            log.debug("Evaluating for string-leng {}", _stringlen)
            //Cache current storage method
            Generator.genAndWriteDF(rows = 10, cols = 10, stringLength = 10, filepath = LOCAL_DATAPATH + "toy_data." + storageMode.toString, storageMode = storageMode)
            cleanup()
            val start = System.currentTimeMillis()
            //TODO Operation switch
            Generator.genAndWriteDF(rows = _row, cols = _col, stringLength = _stringlen, filepath = LOCAL_DATAPATH + "data." + storageMode.toString, storageMode = storageMode)
            val stop = System.currentTimeMillis()
            EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> _col, "stringlen" -> _stringlen, "storageMode" -> storageMode, "operation" -> OperationType.Write, "time" -> (stop - start)))
          })
        })
      })
    })
  }

  /**
    * Switch for local execution / remote execution
    *
    * @return Context for the execution
    */
  def createContext(): AppContext = {
    //Config
    val sparkConfig = new SparkConf()
    sparkConfig.setAppName("Parquet workshop demo")
    if (EXECUTE_LOCAL) {
      sparkConfig.setMaster("local[2]")
    } else {
      System.setProperty("HADOOP_USER_NAME", HADOOP_USER)
      sparkConfig.setMaster(SPARK_MASTER)
    }
    object Implicits extends AppContext with Logging {
      implicit lazy val ac = this

      @transient implicit lazy val sparkSession = SparkSession
        .builder()
        .config(sparkConfig)
        .enableHiveSupport()
        .getOrCreate()

      @transient implicit lazy val sc = new SparkContext(sparkConfig)
      @transient implicit lazy val sqlContext = new HiveContext(sc)
    }

    Implicits.ac
  }


}
