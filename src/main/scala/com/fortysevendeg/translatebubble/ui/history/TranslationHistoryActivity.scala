package com.fortysevendeg.translatebubble.ui.history

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.repository.FetchAllTranslationHistoryRequest
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity
import com.fortysevendeg.translatebubble.ui.commons.{LineItemDecorator, ListLayout}
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Ui}

import scala.concurrent.ExecutionContext.Implicits.global

class TranslationHistoryActivity
    extends Activity
    with Contexts[Activity]
    with ComponentRegistryImpl
    with ListLayout {

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(content)

    runUi(
      (recyclerView
          <~ rvLayoutManager(new LinearLayoutManager(appContextProvider.get))
          <~ rvAddItemDecoration(new LineItemDecorator)) ~
          (reloadButton <~ On.click(Ui {
            loadTranslationHistory()
          })))

    loadTranslationHistory()

  }

  def loadTranslationHistory(): Unit = {
    loading()
    val result = for {
      response <- repositoryServices.fetchAllTranslationHistory(FetchAllTranslationHistoryRequest())
    } yield reloadList(response.result)

    result recover {
      case _ => failed()
    }
  }

  def reloadList(translationHistoryItems: Seq[TranslationHistoryEntity]) = {
    translationHistoryItems.length match {
      case 0 => empty()
      case _ =>
        val translationAdapter = new TranslationHistoryAdapter(translationHistoryItems, new RecyclerClickListener {
          override def onClick(translationHistoryItem: TranslationHistoryEntity): Unit = {}
        })
        adapter(translationAdapter)
    }
  }

}

