package com.fortysevendeg.translatebubble.ui.history

import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

class TranslationHistoryAdapter(tranlationHistoryItems: Seq[TranslationHistoryEntity], listener: RecyclerClickListener)
    (implicit context: ActivityContext, appContext: AppContext)
    extends RecyclerView.Adapter[TranslationHistoryViewHolder]
    with ComponentRegistryImpl {

  override val appContextProvider: AppContext = appContext
  val recyclerClickListener = listener

  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): TranslationHistoryViewHolder = {
    val layoutAdapter = new TranslationHistoryLayoutAdapter()
    layoutAdapter.content.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = recyclerClickListener.onClick(tranlationHistoryItems(v.getTag.asInstanceOf[Int]))
    })
    new TranslationHistoryViewHolder(layoutAdapter)
  }

  override def getItemCount: Int = tranlationHistoryItems.size

  override def onBindViewHolder(viewHolder: TranslationHistoryViewHolder, position: Int): Unit = {
    val translationHistoryItem = tranlationHistoryItems(position)
    val from = translationHistoryItem.data.from.toString
    val to = translationHistoryItem.data.to.toString
    viewHolder.content.setTag(position)
    runUi(
      (viewHolder.languages <~ tvText(persistentServices.getLanguagesStringFromData(from, to) getOrElse "")) ~
          (viewHolder.originalText <~ tvText(translationHistoryItem.data.originalText)) ~
          (viewHolder.translatedText <~ tvText(translationHistoryItem.data.translatedText))
    )
  }
}

trait RecyclerClickListener {
  def onClick(translationHistoryItem: TranslationHistoryEntity)
}