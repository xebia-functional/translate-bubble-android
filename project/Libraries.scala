import sbt._

object Libraries {

  def onCompile(dep: ModuleID): ModuleID = dep % "compile"
  def onTest(dep: ModuleID): ModuleID = dep % "test"

  //Plugins:
  lazy val wartRemover = "org.brianmckenna" %% "wartremover" % Versions.wartremoverV
  lazy val androidSDKPlugin = "com.hanhuy.sbt" % "android-sdk-plugin" % Versions.androidPluginV

  object scala {

    lazy val scalaReflect = "org.scala-lang" % "scala-reflect" % Versions.scalaV
    lazy val scalap = "org.scala-lang" % "scalap" % Versions.scalaV
  }

  object android {

    def androidDep(module: String) = "com.android.support" % module % Versions.androidV

    lazy val androidSupportv4 = androidDep("support-v4")
    lazy val androidAppCompat = androidDep("appcompat-v7")
    lazy val androidRecyclerview = androidDep("recyclerview-v7")
    lazy val androidCardView = androidDep("cardview-v7")
  }

  object playServices {

    def playServicesDep(module: String) = "com.google.android.gms" % module % Versions.playServicesV

    lazy val playServicesGooglePlus = playServicesDep("play-services-plus")
    lazy val playServicesAccountLogin = playServicesDep("play-services-identity")
    lazy val playServicesActivityRecognition = playServicesDep("play-services-location")
    lazy val playServicesAppIndexing = playServicesDep("play-services-appindexing")
    lazy val playServicesCast = playServicesDep("play-services-cast")
    lazy val playServicesDrive = playServicesDep("play-services-drive")
    lazy val playServicesFit = playServicesDep("play-services-fitness")
    lazy val playServicesMaps = playServicesDep("play-services-maps")
    lazy val playServicesAds = playServicesDep("play-services-ads")
    lazy val playServicesPanoramaViewer = playServicesDep("play-services-panorama")
    lazy val playServicesGames = playServicesDep("play-services-games")
    lazy val playServicesWallet = playServicesDep("play-services-wallet")
    lazy val playServicesWear = playServicesDep("play-services-wearable")
    // Google Actions, Google Analytics and Google Cloud Messaging
    lazy val playServicesBase = playServicesDep("play-services-base")
  }

  object macroid {

    def macroid(module: String = "") =
      "org.macroid" %% s"macroid${if(!module.isEmpty) s"-$module" else ""}" % Versions.macroidV

    lazy val macroidRoot = macroid()
    lazy val macroidExtras = "com.fortysevendeg" %% "macroid-extras" % Versions.macroidExtrasV
  }

  object apacheCommons {
    def apacheCommonsDep(module: String) = "org.apache.commons" % module % Versions.apacheCommonsV
    
    lazy val apacheCommonsLang = apacheCommonsDep("commons-lang3")
    
  }

  object json {
    lazy val json4s = "org.json4s" %% "json4s-native" % Versions.json4sV
  }

  object test {
    lazy val specs2 = "org.specs2" %% "specs2-core" % Versions.specs2V % "test"
    lazy val androidTest = "com.google.android" % "android" % "4.1.1.4" % "test"
    lazy val mockito = "org.specs2" % "specs2-mock_2.11" % Versions.mockitoV % "test"
  }
}