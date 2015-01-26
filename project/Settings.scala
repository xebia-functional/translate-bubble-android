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
        "47deg Public" at "http://clinker.47deg.com/nexus/content/groups/public"
      )

  lazy val proguardCommons = Seq(
    "-ignorewarnings",
    "-keep class com.fortysevendeg.** { *; }",
    "-keep class macroid.** { *; }",
    "-keep class scala.Dynamic")

}
