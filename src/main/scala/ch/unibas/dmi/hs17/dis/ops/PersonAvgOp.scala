package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging, ParquetDemoUtils}
import org.apache.spark.sql.functions.avg

/**
  * Created by silvan on 10.05.17.
  */
class PersonAvgOp(rows: Seq[Int], stringlens: Seq[Int]) extends Logging with Config with ParquetDemoUtils {

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
            val start = System.currentTimeMillis()
            val avgdf = StorageMode.fromString(storageMode.toString).read(LOCAL_DATAPATH + PersonWriteOp.getFileName(_row, _stringlen, storageMode)).get
            val avgAdam = avgdf.agg(avg("father.granddad.adam.age").as("adamage"))
            log.debug("Adam is on average {} years old", avgAdam.first().getAs[Long]("adamage"))
            val stop = System.currentTimeMillis()
            EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> -1, "stringlen" -> _stringlen, "storageMode" -> storageMode, "operation" -> OperationType.PeopleMaxQuery, "time" -> (stop - start)))
            avgdf.unpersist()
          }
        })
      })
    })
  }

}
