package com.codacy.roslyn

import java.nio.file.{Path, Paths}
import com.codacy.analysis.core.model.Issue.Message
import com.codacy.analysis.core.model.{Issue, LineLocation}
import com.codacy.plugins.api.results.Pattern
import com.codacy.plugins.api.results.Result.Level
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import better.files.Resource
import com.codacy.rolsyn.RoslynReportParser

class RoslynReportParserSpecs extends AnyWordSpec with Matchers {
  val pwd: Path = Paths.get("/src")

  "RoslynReportParser::parse" should {
    "parse a report" in {
      val input = Resource.asString("report.json").get

      val result = RoslynReportParser.parse(input, relativizeTo = pwd)
      val expected = List(
        Issue(
          patternId = Pattern.Id(value = "CA1822"),
          filename = Paths.get("MyScript.cs"),
          message = Message(text = "Member 'FixedUpdate' does not access instance data and can be marked as static"),
          level = Level.Err,
          category = None,
          location = LineLocation(line = 7)
        ),
        Issue(
          patternId = Pattern.Id(value = "UNT0001"),
          filename = Paths.get("MyScript.cs"),
          message = Message(text = "The Unity message 'FixedUpdate' is empty."),
          level = Level.Warn,
          category = None,
          location = LineLocation(line = 7)
        ),
        Issue(
          patternId = Pattern.Id(value = "UNT0002"),
          filename = Paths.get("MyScript.cs"),
          message = Message(text = "Comparing tags using == is inefficient."),
          level = Level.Warn,
          category = None,
          location = LineLocation(line = 13)
        ),
        Issue(
          patternId = Pattern.Id(value = "UNT0013"),
          filename = Paths.get("SerializedAttributes.cs"),
          message =
            Message(text = "SerializeField attribute is invalid or redundant for property or field: 'publicField'."),
          level = Level.Err,
          category = None,
          location = LineLocation(line = 9)
        ),
        Issue(
          patternId = Pattern.Id(value = "UNT0013"),
          filename = Paths.get("SerializedAttributes.cs"),
          message = Message(text =
            "SerializeField attribute is invalid or redundant for property or field: 'PrivateProperty'."
          ),
          level = Level.Err,
          category = None,
          location = LineLocation(line = 12)
        ),
        Issue(
          patternId = Pattern.Id(value = "UNT0013"),
          filename = Paths.get("SerializedAttributes.cs"),
          message =
            Message(text = "SerializeField attribute is invalid or redundant for property or field: 'staticField'."),
          level = Level.Err,
          category = None,
          location = LineLocation(line = 15)
        ),
        Issue(
          patternId = Pattern.Id(value = "UNT0013"),
          filename = Paths.get("SerializedAttributes.cs"),
          message =
            Message(text = "SerializeField attribute is invalid or redundant for property or field: 'readonlyField'."),
          level = Level.Err,
          category = None,
          location = LineLocation(line = 18)
        )
      )

      result should be(expected)
    }
  }

}
