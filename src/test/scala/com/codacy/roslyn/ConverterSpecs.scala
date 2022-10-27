package com.codacy.roslyn

import java.nio.file.Paths
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import better.files.Resource
import com.codacy.rolsyn.Converter

class ConverterSpecs extends AnyWordSpec with Matchers {

  val pwd = "/src"

  "Converter::convert" should {
    "convert output correctly" in {
      val input = Resource.asString("report.json").get

      val expected =
        """[{"tool":"roslyn","issues":{"Success":{"results":[{"filename":"MyScript.cs","results":[{"Issue":{"patternId":{"value":"Roslyn_CA1822"},"filename":"MyScript.cs","message":{"text":"Member 'FixedUpdate' does not access instance data and can be marked as static"},"level":"Error","location":{"LineLocation":{"line":7}}}},{"Issue":{"patternId":{"value":"Roslyn_UNT0001"},"filename":"MyScript.cs","message":{"text":"The Unity message 'FixedUpdate' is empty."},"level":"Warning","location":{"LineLocation":{"line":7}}}},{"Issue":{"patternId":{"value":"Roslyn_UNT0002"},"filename":"MyScript.cs","message":{"text":"Comparing tags using == is inefficient."},"level":"Warning","location":{"LineLocation":{"line":13}}}}]},{"filename":"SerializedAttributes.cs","results":[{"Issue":{"patternId":{"value":"Roslyn_UNT0013"},"filename":"SerializedAttributes.cs","message":{"text":"SerializeField attribute is invalid or redundant for property or field: 'publicField'."},"level":"Error","location":{"LineLocation":{"line":9}}}},{"Issue":{"patternId":{"value":"Roslyn_UNT0013"},"filename":"SerializedAttributes.cs","message":{"text":"SerializeField attribute is invalid or redundant for property or field: 'PrivateProperty'."},"level":"Error","location":{"LineLocation":{"line":12}}}},{"Issue":{"patternId":{"value":"Roslyn_UNT0013"},"filename":"SerializedAttributes.cs","message":{"text":"SerializeField attribute is invalid or redundant for property or field: 'staticField'."},"level":"Error","location":{"LineLocation":{"line":15}}}},{"Issue":{"patternId":{"value":"Roslyn_UNT0013"},"filename":"SerializedAttributes.cs","message":{"text":"SerializeField attribute is invalid or redundant for property or field: 'readonlyField'."},"level":"Error","location":{"LineLocation":{"line":18}}}}]}]}}}]"""
      val actual = Converter.convert(input, relativizeTo = Paths.get("/src"))

      actual should be(expected)
    }
  }

}
