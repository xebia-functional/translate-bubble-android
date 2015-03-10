package com.fortysevendeg.translatebubble.ui.history

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.{GridLayoutManager, LinearLayoutManager}
import android.view.MenuItem
import com.fortysevendeg.macroid.extras.DeviceMediaQueries._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.repository.FetchAllTranslationHistoryRequest
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity
import com.fortysevendeg.translatebubble.ui.commons.{HistoryItemDecorator, ListLayout}
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

    getActionBar.setDisplayHomeAsUpEnabled(true)
    getActionBar.setHomeButtonEnabled(true)

    val layoutManager = if (landscapeTablet) {
      new GridLayoutManager(appContextProvider.get, 3)
    } else if (tablet) {
      new GridLayoutManager(appContextProvider.get, 2)
    } else {
      new LinearLayoutManager(appContextProvider.get)
    }

    runUi(
      (recyclerView
          <~ rvLayoutManager(layoutManager)
          <~ rvAddItemDecoration(new HistoryItemDecorator)) ~
          (reloadButton <~ On.click(Ui {
            loadTranslationHistory()
          })))

    loadTranslationHistory()

  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home => finish(); true
      case _ => super.onOptionsItemSelected(item)
    }
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

