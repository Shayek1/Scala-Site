package com.shayek.sitegenerator

import os.{rel as _, *}
import scalatags.Text.all.*
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.parser.Parser
import scalatags.Text.tags2.nav

import java.time.*

case class metaDetails(slug: String, name: String, date: LocalDate)

object ImprovingMySite {

  val bootstrapLink = link(
    rel := "stylesheet",
    href := "https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
  )

  val navBar = div(cls := "container my-3",
    nav(cls := "navbar navbar-expand-lg navbar-light bg-light mb-4",
      a(cls := "navbar-brand", href := "/index.html", "Dashboard"),
      ul(cls := "navbar-nav",
        li(cls := "nav-item", a(cls := "nav-link", href := "/index.html", "Home")),
        li(cls := "nav-item", a(cls := "nav-link", href := "/about.html", "About")),
        li(cls := "nav-item", a(cls := "nav-link", href := "/contact.html", "Contact"))
      )
    )
  )

  def postRender(path: Path, outPath: Path): metaDetails = {
    val filename = path.last.stripSuffix(".md")
    val dateOfPublish = LocalDate.ofInstant(
      Instant.ofEpochMilli(os.mtime(path)),
      ZoneOffset.UTC
    )

    val parsing = Parser.builder().build()
    val parsed = parsing.parse(os.read(path))
    val renderer = HtmlRenderer.builder().build()
    val output = renderer.render(parsed)

    val slugPage = s"${filename.toLowerCase.replace(" ", "-")}.html"

    os.write(
      outPath / "article" / slugPage,
      html(
        head(bootstrapLink),
        body(
          div(cls := "container my-4",
            navBar,
            a(href := "../index.html", "<- Back", cls := "btn btn-secondary mb-3"),
            raw(output),
            p(i(s"Written on $dateOfPublish"))
          )
        )
      )
    )

    metaDetails(slugPage, filename, dateOfPublish)
  }

  def generateStaticPage(outPath: Path, name: String, content: Frag*): Unit = {
    os.write(
      outPath / s"$name.html",
      html(
        head(bootstrapLink),
        body(
          div(cls := "container my-4",
            navBar,
            content
          )
        )
      )
    )
  }

  def blogRender(): Unit = {
    val resourcePath = os.pwd / "sitegenerator" / "src" / "main" / "resources"
    val outPath = resourcePath / "blogOut"
    val blogRoot = resourcePath / "articles"

    // Clean output
    os.remove.all(outPath)
    os.makeDir.all(outPath / "article")

    // Render blog posts
    val articles = os.list(blogRoot).map(filePath => postRender(filePath, outPath))
      .sortBy(_.date)

    // Generate index.html
    os.write(
      outPath / "index.html",
      html(
        head(bootstrapLink),
        body(
          div(cls := "container my-4",
            navBar,
            h1("Welcome to my Scala Site"),
            div(cls := "row",
              articles.map { case metaDetails(slug, filename, dateOfPublish) =>
                div(cls := "col-md-6 mb-4",
                  div(cls := "card h-100",
                    div(cls := "card-body",
                      h2(a(cls := "card-title", href := s"article/$slug", filename)),
                      p(cls := "card-text", i(s"Published on $dateOfPublish"))
                    )
                  )
                )
              }
            )
          )
        )
      )
    )

    // Generate About and Contact pages
    generateStaticPage(outPath, "about", h1("About this site"), p("The site is built using Scala and a simple static generation workflow, so every post you read is written in Markdown and transformed into a clean, responsive page."))
    generateStaticPage(outPath, "contact", h1("Contact"), p("Checkout my: ", a(href := "https://github.com/Shayek1", target := "_blank", "GitHub!"))
    )
  }

  def main(args: Array[String]): Unit = blogRender()
}
