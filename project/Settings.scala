import sbt._

object Settings {

  lazy val resolvers =
    Seq(
        Resolver.mavenLocal,
        DefaultMavenRepository,
        Resolver.typesafeRepo("releases"),
        Resolver.typesafeRepo("snapshots"),
        Resolver.typesafeIvyRepo("snapshots"),
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        Resolver.defaultLocal,
        "jcenter" at "http://jcenter.bintray.com",
        "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
        "47deg Public" at "http://clinker.47deg.com/nexus/content/groups/public",
        "47deg Private Snapshot Repository" at "http://clinker.47deg.com/nexus/content/repositories/private-snapshots",
        "47deg Private Release Repository" at "http://clinker.47deg.com/nexus/content/repositories/private-releases"
      )

  lazy val proguardCommons = Seq(
    "-ignorewarnings",
    "-keep class scala.Dynamic")

}
