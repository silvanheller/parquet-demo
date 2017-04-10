package ch.unibas.dmi.hs17.dis.main

import java.io.File

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.ops.{QueryOp, WriteOp}
import ch.unibas.dmi.hs17.dis.storage.StorageMode.StorageMode
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.commons.io.FileUtils
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by silvan on 05.04.17.
  */
object SparkTest extends Config with Logging {

  def cleanup() = {
    log.debug("Deleting old files")
    FileUtils.deleteDirectory(new File(LOCAL_DATAPATH))
    new File(LOCAL_DATAPATH).mkdirs()
  }

  def main(args: Array[String]) {
    val mainContext = createContext()

    //implicit def ac: AppContext = mainContext
    implicit val ac = mainContext

    val rows = Seq(10, 10000)
    val cols = Seq(1, 100)
    val stringlens = Seq(5, 100)

    cleanup()

    val writeOp = new WriteOp(rows, cols, stringlens)
    writeOp.execute()

    val queryOp = new QueryOp(rows, cols, stringlens)
    queryOp.execute()
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

  /**
    * Returns a filename for a given configuration
    */
  def getFileName(rows: Int, cols: Int, stringlen: Int, storageMode: StorageMode): String ={
    rows+"_"+cols+"_"+stringlen+"."+storageMode.toString
  }


}
