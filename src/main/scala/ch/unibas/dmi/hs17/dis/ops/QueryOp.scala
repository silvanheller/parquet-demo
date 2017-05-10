package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.config.Config
import ch.unibas.dmi.hs17.dis.main.{AppContext, EvaluationRunner}
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging, ParquetDemoUtils}
import org.apache.spark.sql.functions.avg
import org.apache.spark.sql.functions.max



/**
  * Created by silvan on 10.04.17.
  */
class QueryOp(rows: Seq[Int], cols: Seq[Int], stringlens: Seq[Int]) extends Logging with Config with ParquetDemoUtils {


  def execute()(implicit ac: AppContext): Unit = {
    verifyInput("queryOp")
    //Iterate over all permutations
    StorageMode.values.foreach(storageMode => {
      log.debug("====================================")
      log.debug("Evaluating QUERIES: Storage Mode {}\n", storageMode)
      log.debug("====================================")

      rows.foreach(_row => {
        //log.debug("Evaluating for row-count {}", _row)
        cols.foreach(_col => {
          //log.debug("Evaluating for col-count {}", _col)
          stringlens.foreach(_stringlen => {
            //log.debug("Evaluating for string-leng {}", _stringlen)
            val start = System.currentTimeMillis()
            val df = StorageMode.fromString(storageMode.toString).read(LOCAL_DATAPATH + EvaluationRunner.getFileName(_row, _col, _stringlen, storageMode)).get
            val selected = df.agg(avg("id"))
            log.debug("ID average is {}", selected.first().toString())
            val stop = System.currentTimeMillis()
            EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> _col, "stringlen" -> _stringlen, "storageMode" -> storageMode, "operation" -> OperationType.Query, "time" -> (stop - start)))
          })
        })
      })
    })
  }
}
