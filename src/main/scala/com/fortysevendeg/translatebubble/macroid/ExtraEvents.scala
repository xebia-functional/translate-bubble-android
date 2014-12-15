package com.fortysevendeg.translatebubble.macroid

import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import macroid.FullDsl._
import macroid.Ui

object ExtraSeekBarEvents {

  case class OnSeekBarChangeListenerHandler(
      onProgressChangedHandler: (SeekBar, Int, Boolean) => Ui[Option[View]] =
      (seekBar: SeekBar, progress: Int, fromUser: Boolean) =>
        Ui(Some(seekBar)),
      onStopTrackingTouchHandler: (SeekBar) => Ui[Option[View]] =
      (seekBar: SeekBar) =>
        Ui(Some(seekBar)),
      onStartTrackingTouchHandler: (SeekBar) => Ui[Option[View]] =
      (seekBar: SeekBar) =>
        Ui(Some(seekBar))
      )

  implicit def onSeekBarChangeListener(listener: OnSeekBarChangeListenerHandler): OnSeekBarChangeListener = {
    new OnSeekBarChangeListener {
      override def onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean): Unit = {
        runUi(listener.onProgressChangedHandler(seekBar, progress, fromUser))
      }
      override def onStopTrackingTouch(seekBar: SeekBar): Unit = {
        runUi(listener.onStopTrackingTouchHandler(seekBar))
      }
      override def onStartTrackingTouch(seekBar: SeekBar): Unit = {
        runUi(listener.onStartTrackingTouchHandler(seekBar))
      }
    }
  }

}
