package com.codacy.roslyn

import com.codacy.plugins.api.results.Pattern.Category
import com.codacy.plugins.api.results.Result.Level

object Utils {

  def ruleToCategoryAndLevel(rule: String): (Category, Level) = {
    rule match {
      case patternId if patternId.startsWith("SA9003") =>
        (Category.UnusedCode, Level.Info)
      case patternId if patternId.startsWith("SA1") =>
        (Category.CodeStyle, Level.Info)
      case patternId if patternId.startsWith("SA2") =>
        (Category.ErrorProne, Level.Err)
      case patternId if patternId.startsWith("SA3") =>
        (Category.ErrorProne, Level.Warn)
      case patternId if patternId.startsWith("SA4") =>
        (Category.UnusedCode, Level.Info)
      case patternId if patternId.startsWith("SA5") =>
        (Category.CodeStyle, Level.Warn)
      case patternId if patternId.startsWith("SA6") =>
        (Category.Performance, Level.Warn)
      case patternId if patternId.startsWith("SA9") =>
        (Category.CodeStyle, Level.Err)
      case _ =>
        (Category.CodeStyle, Level.Info)
    }
  }
}
