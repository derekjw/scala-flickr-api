import sbt._

class FlickrApiProject(info: ProjectInfo) extends DefaultProject(info) {

  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  val dispatchHttp = "net.databinder" %% "dispatch-http" % "0.7.0"
  val liftCommon = "net.liftweb" % "lift-common" % "2.0-scala280-SNAPSHOT"
  val slf4jApi = "org.slf4j" % "slf4j-api" % "1.5.10"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "0.9.18" % "test"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.3" % "test"

  val scalaSnapshots = ScalaToolsSnapshots
  val fyrieReleases = "Fyrie Releases" at "http://repo.fyrie.net/releases/"

  override def managedStyle = ManagedStyle.Maven
  val publishTo = projectVersion.value match {
    case BasicVersion(_,_,_,Some("SNAPSHOT")) =>
      "Fyrie Nexus Snapshots" at "http://nexus.fyrie.net/content/repositories/snapshots/"
    case _ =>
      "Fyrie Nexus Releases" at "http://nexus.fyrie.net/content/repositories/releases/"
  }
  Credentials(Path.userHome / ".ivy2" / ".fyrie-credentials", log)

}

