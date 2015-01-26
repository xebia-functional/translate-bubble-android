import Libraries.android._
import Libraries.macroid._
import Libraries.playServices._
import Libraries.apacheCommons._
import Libraries.json._
import android.PromptPasswordsSigningConfig

android.Plugin.androidBuild

platformTarget in Android := Versions.androidPlatformV

name := """translate-bubble-android"""

organization := "com.fortysevendeg"

organizationName := "47 Degrees"

organizationHomepage := Some(new URL("http://47deg.com"))

version := Versions.appV

scalaVersion := Versions.scalaV

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers ++= Settings.resolvers

libraryDependencies ++= Seq(
  aar(macroidRoot),
  aar(androidAppCompat),
  aar(macroidExtras),
  aar(playServicesBase),
  apacheCommonsLang,
  json4s,
  compilerPlugin(Libraries.wartRemover))


apkSigningConfig in Android := Option(
  PromptPasswordsSigningConfig(
    keystore = new File(Path.userHome.absolutePath + "/.android/translate-bubble.keystore"),
    alias = "47deg"))

run <<= run in Android

packageRelease <<= packageRelease in Android

proguardScala in Android := true

useProguard in Android := true

proguardOptions in Android ++= Settings.proguardCommons
