package com.shayek.sitegenerator

import os.*
import scalatags.Text.all.*

object mySite {
  def blogRender() ={
    //using os.pwd to anchor the relative path, thus making it the absolute path
    val resourcePath = os.pwd/"sitegenerator"/"src"/"main"/"resources"
    val outPath = resourcePath / "blogOut"
    val blogRoot = resourcePath / "articles"

    os.write(
      outPath / "index.html",
      html(
        head(
          //TODO add a css file here
        ),
        body(
          h1("This is my site"),
          //TODO add links
        )
      )
    )
  }
  def main(args: Array[String]): Unit = {
    blogRender()
  }
}
