package com.codacy.roslyn

import com.codacy.analysis.core.model.Issue
import com.codacy.plugins.api.results.Pattern

object Prefixer {
  val toolPrefix = "Roslyn_"

  def withPrefix(issue: Issue): Issue = issue.copy(patternId = Pattern.Id(s"$toolPrefix${issue.patternId.value}"))

}
