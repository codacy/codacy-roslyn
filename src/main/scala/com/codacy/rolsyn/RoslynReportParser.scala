package com.codacy.rolsyn

import java.nio.file.{Path, Paths}

import com.codacy.analysis.core.model.{Issue, LineLocation}
import com.codacy.plugins.api.results.Pattern
import com.codacy.plugins.api.results.Result.Level

object RoslynReportParser {

  def parse(report: String, relativizeTo: Path): Seq[Issue] = {
    val json = ujson.read(report).arr
    val res = for {
      result <- json
      fileChanges = result("FileChanges").arr
      fileChange <- fileChanges
      rule = fileChange("DiagnosticId").str
      line = fileChange("LineNumber").num.toInt
      uri = result("FilePath").str
      filename = relativizeTo.relativize(Paths.get(uri.stripPrefix("file://")).toAbsolutePath())
      issueDescription = fileChange("FormatDescription").str.split(s"$rule:") if issueDescription.size == 2
      level = issueDescription(0).trim
      message = issueDescription(1).trim
    } yield Issue(
      patternId = Pattern.Id(rule),
      filename = filename,
      message = Issue.Message(message),
      level = level match {
        case "error" => Level.Err
        case "warning" => Level.Warn
        case _ => Level.Info
      },
      category = None,
      location = LineLocation(line)
    )
    res.toSeq
  }
}
