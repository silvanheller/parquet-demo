package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging, ParquetDemoUtils}
import org.apache.spark.sql.functions.{desc, max}

/**
  * Created by silvan on 07.05.17.
  */
class PersonQueryOp(rows: Seq[Int], stringlens: Seq[Int]) extends Logging with Config with ParquetDemoUtils {

  def execute()(implicit ac: AppContext): Unit = {
    verifyInput("queryOp")
    //Iterate over all permutations
    StorageMode.values.foreach(storageMode => {
      log.debug("====================================")
      log.debug("Evaluating Person QUERIES: Storage Mode {}\n", storageMode)
      log.debug("====================================")

      rows.foreach(_row => {
        stringlens.foreach(_stringlen => {
          if (!storageMode.equals(StorageMode.csv)) {
            var start = System.currentTimeMillis()
            val df = StorageMode.fromString(storageMode.toString).read(LOCAL_DATAPATH + PersonWriteOp.getFileName(_row, _stringlen, storageMode)).get
            val maxDF = df.groupBy("father.granddad", "personName").agg(max("father.granddad.age").as("max")).orderBy(desc("max"))
            log.debug("Max granddad age is {}", maxDF.first().getAs[Int]("max"))
            var stop = System.currentTimeMillis()
            EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> -1, "stringlen" -> _stringlen, "storageMode" -> storageMode, "operation" -> OperationType.PeopleGroupingQuery, "time" -> (stop - start)))
            df.unpersist(true)
          }
        })
      })
    })
  }
}
