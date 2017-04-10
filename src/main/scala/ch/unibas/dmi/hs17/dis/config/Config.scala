package ch.unibas.dmi.hs17.dis.config

/**
  * Created by silvan on 05.04.17.
  */
trait Config {

  //Distributed
  val HADOOP_USER = "root"
  val HADOOP_BASEPATH = "hdfs://172.19.0.2:9000/"
  val HADOOP_DATAPATH = "/workshop/"
  val SPARK_MASTER = "spark://172.19.0.2:7077"
  //Local
  val EXECUTE_LOCAL = true
  val LOCAL_DATAPATH = "workshop/"
}
