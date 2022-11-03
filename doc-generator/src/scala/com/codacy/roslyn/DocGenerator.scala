package scala.com.codacy.roslyn

import scala.sys.process._
import better.files._
import com.codacy.roslyn.Versions

case class PatternInfo(patternId: String, title: String)

case class RoslynRule(name: String, description: String, category: String)

object DocGenerator {

  def main(args: Array[String]): Unit = {

    val docs = file"docs"
    val patternsJson = docs / "patterns.json"
    val description = docs / "description"
    val descriptionJson = description / "description.json"

    val roslynVersion = Versions.roslynVersion

    val index = "index.md"

    def buildUrl(fileName: String) =
      s"https://raw.githubusercontent.com/microsoft/Microsoft.Unity.Analyzers/$roslynVersion/doc/$fileName"

    val rules = fetchRules(buildUrl(index))

    val descriptions = rules.map(buildDescription)
    val patterns =
      ujson
        .Obj("name" -> "roslyn", "version" -> roslynVersion, "patterns" -> rules.map(buildPattern))

    descriptionJson.createFileIfNotExists(createParents = true).write(ujson.write(descriptions, indent = 2) + "\n")
    patternsJson.createFileIfNotExists(createParents = true).write(ujson.write(patterns, indent = 2) + "\n")

    rules.foreach { rule =>
      val explanation = Seq("curl", buildUrl(s"${rule.name}.md")).!!
      val explanationFile = description / s"${rule.name}.md"

      explanationFile.createFileIfNotExists(createParents = true).write(explanation.trim)
    }
  }

  private def fetchRules(url: String): List[RoslynRule] = {
    val unparsedRules = Seq("curl", url).!!

    val unparsedRulesSplit = unparsedRules.split("# Diagnostic Suppressors")

    val diagnosticAnalyzers = unparsedRulesSplit.head

    buildDiagnosticAnalyzersRules(diagnosticAnalyzers).flatten
  }

  private def buildDiagnosticAnalyzersRules(unparsedRules: String): List[Option[RoslynRule]] = {
    unparsedRules.split("\n").filter(_.startsWith("[")).map(parseRulesLine).toList
  }

  private def parseRulesLine(line: String): Option[RoslynRule] = {
    val splitLine = line.split(" \\| ")

    val unparsedName = splitLine(0)
    val description = splitLine(1).trim
    val category = splitLine(2).trim

    val name = unparsedName.split("\\(")(1).split("\\)")(0).trim

    val nameWithoutExtension = name.split("\\.")(0)

    Some(RoslynRule(nameWithoutExtension, description, category))
  }

  private def buildDescription(rule: RoslynRule): Map[String, String] = {
    Map("patternId" -> rule.name, "title" -> rule.description, "description" -> rule.description)
  }

  private def buildPattern(rule: RoslynRule): Map[String, String] = {
    Map(
      "patternId" -> rule.name,
      "level" -> toCodacyLevel(rule.category),
      "category" -> toCodacyCategory(rule.category)
    )
  }

  private def toCodacyCategory(category: String): String = {
    category match {
      case "Correctness" => "ErrorProne"
      case "Performance" => "Performance"
      case "Type Safety" => "ErrorProne"
    }
  }

  private def toCodacyLevel(category: String): String = {
    category match {
      case _ => "Warning"
    }
  }
}
