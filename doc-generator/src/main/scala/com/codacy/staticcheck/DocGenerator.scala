package com.codacy.roslyn

import better.files._

import scala.sys.process._

case class PatternInfo(patternId: String, title: String)

object DocGenerator {

  def main(args: Array[String]): Unit = {

    val rules = Seq(
      "S1000",
      "S1001",
      "S1002",
      "S1003",
      "S1004",
      "S1005",
      "S1006",
      "S1007",
      "S1008",
      "S1009",
      "S1010",
      "S1011",
      "S1012",
      "S1016",
      "S1017",
      "S1018",
      "S1019",
      "S1020",
      "S1021",
      "S1023",
      "S1024",
      "S1025",
      "S1028",
      "S1029",
      "S1030",
      "S1031",
      "S1032",
      "S1033",
      "S1034",
      "S1035",
      "S1036",
      "S1037",
      "S1038",
      "S1039",
      "SA1000",
      "SA1001",
      "SA1002",
      "SA1003",
      "SA1004",
      "SA1005",
      "SA1006",
      "SA1007",
      "SA1008",
      "SA1010",
      "SA1011",
      "SA1012",
      "SA1013",
      "SA1014",
      "SA1015",
      "SA1016",
      "SA1017",
      "SA1018",
      "SA1019",
      "SA1020",
      "SA1021",
      "SA1023",
      "SA1024",
      "SA1025",
      "SA1026",
      "SA1027",
      "SA1028",
      "SA1029",
      "SA2000",
      "SA2001",
      "SA2002",
      "SA2003",
      "SA3000",
      "SA3001",
      "SA4000",
      "SA4001",
      "SA4003",
      "SA4004",
      "SA4006",
      "SA4008",
      "SA4009",
      "SA4010",
      "SA4011",
      "SA4012",
      "SA4013",
      "SA4014",
      "SA4015",
      "SA4016",
      "SA4017",
      "SA4018",
      "SA4019",
      "SA4020",
      "SA4021",
      "SA4022",
      "SA5000",
      "SA5001",
      "SA5002",
      "SA5003",
      "SA5004",
      "SA5005",
      "SA5007",
      "SA5008",
      "SA5009",
      "SA5010",
      "SA5011",
      "SA6000",
      "SA6001",
      "SA6002",
      "SA6003",
      "SA6005",
      "SA9001",
      "SA9002",
      "SA9003",
      "SA9004",
      "SA9005",
      "ST1000",
      "ST1001",
      "ST1003",
      "ST1005",
      "ST1006",
      "ST1008",
      "ST1011",
      "ST1012",
      "ST1013",
      "ST1015",
      "ST1016",
      "ST1017",
      "ST1018",
      "ST1019",
      "ST1020",
      "ST1021",
      "ST1022"
    )

    val docs = file"docs"
    val patternsJson = docs / "patterns.json"
    val description = docs / "description"
    val descriptionJson = description / "description.json"

    val patterns =
      ujson
        .Obj("name" -> "staticcheck", "version" -> Versions.staticcheckVersion, "patterns" -> rules.map(generateRule))

    val explainations = rules.map(rule => rule -> getExplanation(rule))
    val descriptions = explainations.map { case (rule, explaination) => generateDescription(rule, explaination) }
    patternsJson.write(ujson.write(patterns, indent = 2) + "\n")
    descriptionJson.write(ujson.write(descriptions, indent = 2) + "\n")
    for ((rule, explaination) <- explainations) {
      (description / s"$rule.md").write("# " + explaination.trim() + "\n")
    }
  }

  private def generateRule(rule: String) = {
    val (category, level) = Utils.ruleToCategoryAndLevel(rule)
    ujson.Obj("patternId" -> rule, "level" -> level.toString(), "category" -> category.toString())
  }

  def getExplanation(rule: String) = {
    Seq("staticcheck", "-explain", rule).!!
  }

  def generateDescription(rule: String, explaination: String) = {
    val title = explaination.split("\n").head
    Map("patternId" -> rule, "title" -> title, "description" -> title)
  }

}
