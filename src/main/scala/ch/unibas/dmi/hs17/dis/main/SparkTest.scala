package ch.unibas.dmi.hs17.dis.main

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.datagen.Generator
import ch.unibas.dmi.hs17.dis.utils.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by silvan on 05.04.17.
  */
object SparkTest extends Config {

  def main(args: Array[String]) {
    val mainContext = createContext()

    //implicit def ac: AppContext = mainContext
    implicit val ac = mainContext

    Generator.writeDF(20, 10, 10, LOCAL_DATAPATH+"toy.parquet")
  }

  /**
    * Switch for local execution / remote execution
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
