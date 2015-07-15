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

class TranslationHistoryAdapter(translationHistoryItems: Seq[TranslationHistoryEntity])
  (implicit context: ActivityContextWrapper)
  extends RecyclerView.Adapter[TranslationHistoryViewHolder]
  with ComponentRegistryImpl {

  override val contextProvider: ContextWrapper = context

  override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): TranslationHistoryViewHolder =
    new TranslationHistoryViewHolder(new TranslationHistoryLayoutAdapter())

  override def getItemCount: Int = translationHistoryItems.size

  override def onBindViewHolder(viewHolder: TranslationHistoryViewHolder, position: Int): Unit = {
    val translationHistoryItem = translationHistoryItems(position)
    val from = translationHistoryItem.data.from.toString
    val to = translationHistoryItem.data.to.toString
    runUi(
      viewHolder.bind(persistentServices.getLanguagesStringFromData(from, to) getOrElse "",
        translationHistoryItem.data.originalText,
        translationHistoryItem.data.translatedText)
    )
  }
}