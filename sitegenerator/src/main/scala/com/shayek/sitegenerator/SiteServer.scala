package com.shayek.sitegenerator

object SiteServer extends cask.MainRoutes {
  override val port = 3324

  mySite.blogRender()

  val resourcePath = os.pwd/"sitegenerator"/"src"/"main"/"resources"
  val staticDir = resourcePath / "blogOut"

  @cask.staticFiles("/") //it will map the file in the folder to the root URL
  def staticFileRoute() = staticDir.toString

  //allows the server to start
  initialize()
}
