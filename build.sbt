import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-21"

name := """translate-bubble-android"""

scalaVersion := "2.11.1"

// a shortcut
run <<= run in Android

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com"
)

// add linter
scalacOptions in (Compile, compile) ++=
    (dependencyClasspath in Compile).value.files.map("-P:wartremover:cp:" + _.toURI.toURL)

scalacOptions in (Compile, compile) ++= Seq(
  "-P:wartremover:traverser:macroid.warts.CheckUi"
)

libraryDependencies ++= Seq(
  aar("com.android.support" % "appcompat-v7" % "21.0.2"),
  aar("com.android.support" % "recyclerview-v7" % "21.0.2"),
  aar("com.android.support" % "cardview-v7" % "21.0.2"),
  aar("org.macroid" %% "macroid" % "2.0.0-M3"),
  "org.json4s" %% "json4s-native" % "3.2.10",
  compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")
)

proguardScala in Android := true

// Generic ProGuard rules
proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic"
)
