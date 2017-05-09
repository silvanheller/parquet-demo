package ch.unibas.dmi.hs17.dis.main

import java.io.File

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.ops.{PersonQueryOp, PersonWriteOp, QueryOp, WriteOp}
import ch.unibas.dmi.hs17.dis.storage.StorageMode.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{Logging, ParquetDemoUtils}
import org.apache.commons.io.FileUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by silvan on 05.04.17.
  */
object EvaluationRunner extends Config with Logging with ParquetDemoUtils {

  def cleanup() = {
    log.debug("Deleting old files")
    verifyInput("cleanup")
    FileUtils.deleteDirectory(new File(LOCAL_DATAPATH))
    new File(LOCAL_DATAPATH).mkdirs()
  }

  def main(args: Array[String]) {
    val mainContext = createContext()

    //implicit def ac: AppContext = mainContext
    implicit val ac = mainContext

    var rows = Seq(1000000)
    val cols = Seq(1, 10, 50, 100)
    val stringlens = Seq(5, 50, 100)

    //cleanup()

    val writeOp = new WriteOp(rows, cols, stringlens)
    //writeOp.execute()

    val queryOp = new QueryOp(rows, cols, stringlens)
    //queryOp.execute()

    val personWriteOp = new PersonWriteOp(rows, stringlens)
    personWriteOp.execute()
    val personQueryOp = new PersonQueryOp(rows, stringlens)
    personQueryOp.execute()
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

    }

    Implicits.ac
  }

  /**
    * Returns a filename for a given configuration with cols + stringlength
    */
  def getFileName(rows: Int, cols: Int, stringlen: Int, storageMode: StorageMode): String = {
    rows + "_" + cols + "_" + stringlen + "." + storageMode.toString
  }


}
