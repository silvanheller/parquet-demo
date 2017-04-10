package ch.unibas.dmi.hs17.dis.ops

import ch.unibas.dmi.hs17.dis.datagen.Generator
import ch.unibas.dmi.hs17.dis.main.SparkTest._
import ch.unibas.dmi.hs17.dis.storage.StorageMode
import ch.unibas.dmi.hs17.dis.utils.{EvaluationResultLogger, Logging}

/**
  * Created by silvan on 10.04.17.
  */
class QueryOp(rows: Seq[Int], cols: Seq[Int], stringlens: Seq[Int]) extends Logging {


  def execute(): Unit = {
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
            val start = System.currentTimeMillis()
            Generator.genAndWriteDF(_row, _col, _stringlen, LOCAL_DATAPATH + getFileName(_row, _col, _stringlen, storageMode), storageMode)
            val stop = System.currentTimeMillis()
            EvaluationResultLogger.write(Map("rows" -> _row, "cols" -> _col, "stringlen" -> _stringlen, "storageMode" -> storageMode, "operation" -> OperationType.Write, "time" -> (stop - start)))
          })
        })
      })
    })
  }
}
