package com.codacy.roslyn

import java.nio.file.{Path, Paths}

import com.codacy.analysis.core.model.Issue.Message
import com.codacy.analysis.core.model.{Issue, LineLocation}
import com.codacy.plugins.api.results.Pattern
import com.codacy.plugins.api.results.Result.Level
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import better.files.Resource

class RoslynReportParserSpecs extends AnyWordSpec with Matchers {
  val pwd: Path = Paths.get("/src")

  "RoslynReportParser::parse" should {
    "parse a report" in {
      val input = Resource.asString("report.json").get

      val result = RoslynReportParser.parse(input, relativizeTo = pwd)
      val expected = List(
        Issue(
          patternId = Pattern.Id(value = "SA1633"),
          filename = Paths.get("dotnet-example/Program.cs"),
          message = Message(text = "The file header is missing or not located at the top of the file."),
          level = Level.Warn,
          category = Some(value = Pattern.Category.CodeStyle),
          location = LineLocation(line = 1)
        ),
        Issue(
          patternId = Pattern.Id(value = "SA1200"),
          filename = Paths.get("dotnet-example/Program.cs"),
          message = Message(text = "Using directive should appear within a namespace declaration"),
          level = Level.Warn,
          category = Some(value = Pattern.Category.CodeStyle),
          location = LineLocation(line = 1)
        ),
        Issue(
          patternId = Pattern.Id(value = "SA1300"),
          filename = Paths.get("dotnet-example/Program.cs"),
          message = Message(text = "Element 'dotnet_example' should begin with an uppercase letter"),
          level = Level.Warn,
          category = Some(value = Pattern.Category.CodeStyle),
          location = LineLocation(line = 3)
        ),
        Issue(
          patternId = Pattern.Id(value = "SA1400"),
          filename = Paths.get("dotnet-example/Program.cs"),
          message = Message(text = "Element 'Program' should declare an access modifier"),
          level = Level.Warn,
          category = Some(value = Pattern.Category.CodeStyle),
          location = LineLocation(line = 5)
        ),
        Issue(
          patternId = Pattern.Id(value = "SA1400"),
          filename = Paths.get("dotnet-example/Program.cs"),
          message = Message(text = "Element 'Main' should declare an access modifier"),
          level = Level.Warn,
          category = Some(value = Pattern.Category.CodeStyle),
          location = LineLocation(line = 7)
        )
      )
      result should be(expected)
    }
  }

}
