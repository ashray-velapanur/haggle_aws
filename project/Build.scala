import sbt._
import Keys._
import play.Project._
import com.github.play2war.plugin._


object ApplicationBuild extends Build {

  val appName         = "foo"
  val appVersion      = "1.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.apache.opennlp" % "opennlp-tools" % "1.5.2-incubating"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",

      Play2WarKeys.servletVersion := "3.0"
  ).settings(Play2WarPlugin.play2WarSettings: _*)


}
