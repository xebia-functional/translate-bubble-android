package com.fortysevendeg.translatebubble.macroid

import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentManager}
import macroid.{ActivityContext, FragmentBuilder, FragmentManagerContext}

object ExtraFragment {

  def addFragment[F <: Fragment](
      builder : FragmentBuilder[F],
      args : Option[Bundle] = None,
      id : Option[Int] = None,
      tag : Option[String] = None
      )(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager])  = {
    builder.pass(args.getOrElse(new Bundle())).factory.map {
      fragment =>
        managerContext.manager.beginTransaction().add(id.getOrElse(0), fragment, tag.getOrElse("")).commit()
    }
  }

  def removeFragment(fragment: Fragment
      )(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager])  = {
    managerContext.manager.beginTransaction().remove(fragment).commit()
  }

  def replaceFragment[F <: Fragment](
      builder : FragmentBuilder[F],
      args : Bundle,
      id : Int,
      tag : Option[String] = None
      )(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager])  = {
    builder.pass(args).factory.map {
      fragment =>
        managerContext.manager.beginTransaction().replace(id, fragment, tag.getOrElse(null)).commit()
    }
  }

  def findFragmentByTag(tag : String)(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager]) : Fragment  = {
    managerContext.manager.findFragmentByTag(tag)
  }

  def findFragmentById(id : Int)(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager]) : Fragment  = {
    managerContext.manager.findFragmentById(id)
  }

  def existFragmentByTag(tag : String)(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager]) : Boolean  = {
    managerContext.manager.findFragmentByTag(tag) != null
  }

  def existFragmentById(id : Int)(implicit context: ActivityContext, managerContext: FragmentManagerContext[Fragment, FragmentManager]) : Boolean  = {
    managerContext.manager.findFragmentById(id) != null
  }

}
