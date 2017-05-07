package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.AppContext
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging, ParquetDemoUtils}
import org.apache.spark.sql.functions.{avg, desc, max}

/**
  * Created by silvan on 07.05.17.
  */
class PersonQueryOp(rows: Seq[Int]) extends Logging with Config with ParquetDemoUtils {

  def execute()(implicit ac: AppContext): Unit = {
    verifyInput("queryOp")
    val spark = ac
    //Iterate over all permutations
    StorageMode.values.foreach(storageMode => {
      log.debug("====================================")
      log.debug("Evaluating Person QUERIES: Storage Mode {}\n", storageMode)
      log.debug("====================================")

      rows.foreach(_row => {
        if (!storageMode.equals(StorageMode.csv)) {
          //log.debug("Evaluating for string-leng {}", _stringlen)
          //TODO Check for CSV -> don't log
          val start = System.currentTimeMillis()
          val df = StorageMode.fromString(storageMode.toString).read(LOCAL_DATAPATH + PersonWriteOp.getFileName(_row, storageMode)).get
          val maxDF = df.groupBy("father.granddad", "personName").agg(max("father.granddad.age").as("max")).orderBy(desc("max"))
          val count = df.count()
          log.debug("Number of tuples is {}", count)
          val avgAdam = df.agg(avg("father.granddad.adam.age").as("adamage"))
          log.debug("Adam is on average {} years old", avgAdam.first().getAs[Long]("adamage"))
          val stop = System.currentTimeMillis()
          EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> -1, "stringlen" -> -1, "storageMode" -> storageMode, "operation" -> OperationType.PeopleQuery, "time" -> (stop - start)))

        }
      })

    })
  }
}
