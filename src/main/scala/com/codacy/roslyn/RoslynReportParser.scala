package com.codacy.roslyn

import java.nio.file.{Path, Paths}

import com.codacy.analysis.core.model.{Issue, LineLocation}
import com.codacy.plugins.api.results.Pattern
import com.codacy.plugins.api.results.Result.Level

object RoslynReportParser {

  def parse(report: String, relativizeTo: Path): Seq[Issue] = {
    val json = ujson.read(report)
    val res = for {
      run <- json("runs").arr
      result <- run("results").arr
      if result.obj.contains("locations")
      location <- result("locations").arr
      rule = result("ruleId").str
      resultFile = location("resultFile")
      uri = resultFile("uri").str
      region = resultFile("region")
      filename = relativizeTo.relativize(Paths.get(uri.stripPrefix("file://")).toAbsolutePath())
    } yield Issue(
      patternId = Pattern.Id(rule),
      filename = filename,
      message = Issue.Message(result("message").str),
      level = result("level").str match {
        case "error" => Level.Err
        case "warning" => Level.Warn
        case _ => Level.Info
      },
      category = Some(Utils.ruleToCategoryAndLevel(rule)._1),
      location = LineLocation(region("startLine").num.toInt)
    )

    res.toSeq
  }
}
