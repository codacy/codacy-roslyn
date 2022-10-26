package com.codacy.rolsyn

import java.nio.file.Paths

object Main {

  def main(args: Array[String]): Unit = {
    val stdin = scala.io.Source.fromInputStream(System.in)
    val input = stdin.mkString
    val pwd = Paths.get(System.getProperty("user.dir"))
    val jsonString = Converter.convert(input, relativizeTo = pwd)
    println(jsonString)
  }
}
