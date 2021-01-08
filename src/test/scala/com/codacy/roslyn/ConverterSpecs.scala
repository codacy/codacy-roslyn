package com.codacy.roslyn

import java.nio.file.Paths

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import better.files.Resource

class ConverterSpecs extends AnyWordSpec with Matchers {

  val pwd = "/src"

  "Converter::convert" should {
    "convert output correctly" in {
      val input = Resource.asString("report.json").get

      val expected = """[{"tool":"roslyn","issues":{"Success":{"results":[{"filename":"dotnet-example/Program.cs","results":[{"Issue":{"patternId":{"value":"Roslyn_SA1200"},"filename":"dotnet-example/Program.cs","message":{"text":"Using directive should appear within a namespace declaration"},"level":"Warning","category":"CodeStyle","location":{"LineLocation":{"line":1}}}},{"Issue":{"patternId":{"value":"Roslyn_SA1633"},"filename":"dotnet-example/Program.cs","message":{"text":"The file header is missing or not located at the top of the file."},"level":"Warning","category":"CodeStyle","location":{"LineLocation":{"line":1}}}},{"Issue":{"patternId":{"value":"Roslyn_SA1400"},"filename":"dotnet-example/Program.cs","message":{"text":"Element 'Program' should declare an access modifier"},"level":"Warning","category":"CodeStyle","location":{"LineLocation":{"line":5}}}},{"Issue":{"patternId":{"value":"Roslyn_SA1400"},"filename":"dotnet-example/Program.cs","message":{"text":"Element 'Main' should declare an access modifier"},"level":"Warning","category":"CodeStyle","location":{"LineLocation":{"line":7}}}},{"Issue":{"patternId":{"value":"Roslyn_SA1300"},"filename":"dotnet-example/Program.cs","message":{"text":"Element 'dotnet_example' should begin with an uppercase letter"},"level":"Warning","category":"CodeStyle","location":{"LineLocation":{"line":3}}}}]}]}}}]"""
      Converter.convert(input, relativizeTo = Paths.get("/src")) should be(expected)
    }
  }

}
