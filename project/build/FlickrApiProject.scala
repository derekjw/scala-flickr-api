import sbt._

class FlickrApiProject(info: ProjectInfo) extends DefaultProject(info) {

  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  override def libraryDependencies =
    Set("net.databinder" %% "dispatch-http" % "0.7.+",
        "net.liftweb" % "lift-common" % "2.0-scala280-SNAPSHOT",
        "org.slf4j" % "slf4j-api" % "1.5.+",
        "ch.qos.logback" % "logback-classic" % "0.9.+" % "test",
        "org.scala-tools.testing" %% "specs" % "1.6.+" % "test")

  override def repositories =
    Set(ScalaToolsSnapshots,
        "Databinder" at "http://databinder.net/repo")

}

