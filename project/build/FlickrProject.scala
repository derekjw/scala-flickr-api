import sbt._

class FlickrProject(info: ProjectInfo) extends ParentProject(info) {

  lazy val core = project("core", "flickr", new Core(_))

  lazy val cli = project("cli", "flickr-cli", new Cli(_), core)

  val scalaSnapshots = ScalaToolsSnapshots
  val fyrieReleases = "Fyrie Releases" at "http://repo.fyrie.net/releases/"
  val fyrieSnapshots = "Fyrie Snapshots" at "http://repo.fyrie.net/snapshots/"

  private def noAction = task {None}
  override def deliverLocalAction = noAction
  override def publishLocalAction = noAction
  override def publishAction = task {None}  

  override def managedStyle = ManagedStyle.Maven
  val publishTo = projectVersion.value match {
    case BasicVersion(_,_,_,Some("SNAPSHOT")) =>
      "Fyrie Nexus Snapshots" at "http://nexus.fyrie.net/content/repositories/snapshots/"
    case _ =>
      "Fyrie Nexus Releases" at "http://nexus.fyrie.net/content/repositories/releases/"
  }
  Credentials(Path.userHome / ".ivy2" / ".fyrie-credentials", log)

  class Core(info: ProjectInfo) extends DefaultProject(info) {
   
    override def compileOptions = super.compileOptions ++ Seq(Unchecked)
    
    val dispatchHttp = "net.databinder" %% "dispatch-http" % "0.7.4"
    val dispatchMime = "net.databinder" %% "dispatch-mime" % "0.7.4"
    val liftCommon = "net.liftweb" % "lift-common" % "2.0-scala280-SNAPSHOT"
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "0.9.18" % "test"
    val specs = "org.scala-tools.testing" %% "specs" % "1.6.5-SNAPSHOT" % "test"
 
  }

  class Cli(info: ProjectInfo) extends DefaultProject(info) {
    val liftUtil = "net.liftweb" % "lift-util" % "2.0-scala280-SNAPSHOT"
  }
}

