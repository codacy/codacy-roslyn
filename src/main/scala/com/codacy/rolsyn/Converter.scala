package com.codacy.rolsyn

import java.nio.file.Path

import com.codacy.analysis.core.model.IssuesAnalysis.FileResults
import com.codacy.analysis.core.model.{IssuesAnalysis, ToolResults}
import com.codacy.analysis.core.serializer.IssuesReportSerializer

object Converter {

  val toolName = "roslyn"

  def convert(input: String, relativizeTo: Path): String = {
    val parsed = RoslynReportParser.parse(input, relativizeTo)

    val grouped = parsed
      .map(Prefixer.withPrefix)
      .groupBy(_.filename)
      .view
      .map { case (path, res) => FileResults(path, res.to(Set)) }
      .to(Set)

    val toolResults = ToolResults(toolName, IssuesAnalysis.Success(grouped))
    IssuesReportSerializer.toJsonString(Set(toolResults))
  }

}
