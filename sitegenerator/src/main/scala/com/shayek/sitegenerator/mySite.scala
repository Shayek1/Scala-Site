package com.shayek.sitegenerator

import os.*
import scalatags.Text.all.{p, *}
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.parser.Parser

import java.time.*

object mySite {
  def postRender(path: Path, outPath: Path) = {
    //take in the file name
    val s"$filename.md" = path.last //this makes it so the name of the file is registered as the name without ".md"
    val dateOfPublish = LocalDate.ofInstant(
      Instant.ofEpochMilli(os.mtime(path)),
      ZoneOffset.UTC
    )

    //the rendering process
    val parsing = Parser.builder().build()
    val parsed = parsing.parse(os.read(path))
    val renderer = HtmlRenderer.builder().build()
    val output = renderer.render(parsed)

    val slugPage = s"${filename.toLowerCase.replace(" ", "-")}.html"

    os.write(
      outPath / "article" / slugPage ,
      html(
        head(
          //TODO add same css file
        ),
        body(
        a(href := "../index.html", "<- Back"),
          raw(output),
          p(i(s"Written on $dateOfPublish"))
        )
      )
    )

    (slugPage, dateOfPublish)
  }

  def blogRender() ={
    //using os.pwd to anchor the relative path, thus making it the absolute path
    val resourcePath = os.pwd/"sitegenerator"/"src"/"main"/"resources"
    val outPath = resourcePath / "blogOut"
    val blogRoot = resourcePath / "articles"


    //cleaning the blogRoot
    os.remove.all(outPath)
    os.makeDir.all(outPath / "article")


    //page generation
    val articles = os.list(blogRoot).map(filePath => postRender(filePath, outPath))
      .sortBy(_._2)

    //index.html generation
    os.write(
      outPath / "index.html",
      html(
        head(
          //TODO add a css file here
        ),
        body(
          h1("This is my site"),
          articles.map{
            case (slug, dateofPublish) => h2(
              a(href := s"article/ $slug")
            )
          }
        )
      )
    )
  }
  def main(args: Array[String]): Unit = {
    blogRender()
  }
}
