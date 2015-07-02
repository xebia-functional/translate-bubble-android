/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortysevendeg.translatebubble.ui.history

import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, ContextWrapper}

class TranslationHistoryAdapter(tranlationHistoryItems: Seq[TranslationHistoryEntity], listener: RecyclerClickListener)
    (implicit context: ActivityContextWrapper)
    extends RecyclerView.Adapter[TranslationHistoryViewHolder]
    with ComponentRegistryImpl {

  override val contextProvider: ContextWrapper = context
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