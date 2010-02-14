import sbt._

class FlickrApiProject(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = super.compileOptions ++ Seq(Unchecked)

  val dispatchHttp = "net.databinder" %% "dispatch-http" % "0.7.+"
  val liftCommon = "net.liftweb" % "lift-common" % "2.0-scala280-SNAPSHOT"
  val slf4japi = "org.slf4j" % "slf4j-api" % "1.5.+"
  
  val logback = "ch.qos.logback" % "logback-classic" % "0.9.+" % "test"
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.+" % "test"

  val snapshotRepo = "Scala Snapshots" at "http://scala-tools.org/repo-snapshots"
  val databinder = "Databinder" at "http://databinder.net/repo"
}

