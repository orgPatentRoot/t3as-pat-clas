/*
    Copyright 2013, 2014 NICTA
    
    This file is part of t3as (Text Analysis As A Service).

    t3as is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    t3as is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with t3as.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.t3as.patClas.api

import scala.language.implicitConversions

object CPC {

  case class Description(id: Int, symbol: String, level: Int, classTitle: String, notesAndWarnings: String)

  case class Hit(score: Float, symbol: API.Symbol, level: Int, classTitleHighlights: String, notesAndWarningsHighlights: String) extends API.HitBase
  
  /** Names of CPC fields in the Lucene index. */
  object IndexFieldName extends Enumeration {
    type IndexFieldName = Value
    val Symbol, Level, ClassTitle, ClassTitleUnstemmed, NotesAndWarnings, NotesAndWarningsUnstemmed = Value

    implicit def convert(f: IndexFieldName) = f.toString
  }
  import IndexFieldName._
  
  val textFields: List[String] = List(ClassTitle, NotesAndWarnings)
  val unstemmedTextFields: List[String] = List(ClassTitleUnstemmed, NotesAndWarningsUnstemmed) // in pref order for suggester
  val hitFields: Set[String] = Set(Symbol, Level, ClassTitle, NotesAndWarnings)
  
  
  def mkHit(score: Float, f: Map[String, String], h: Map[String, String]) = {
    def getH(s: String) = h.getOrElse(s, f.getOrElse(s, ""))
    def getHU(s: String) = h.getOrElse(s, f.getOrElse(s, "").toUpperCase)
    Hit(score, API.Symbol(f(Symbol).toUpperCase, getHU(Symbol)), f(Level).toInt, getH(ClassTitle), getH(NotesAndWarnings))
  }

  /** Entity class mapping to a database row representing a CPC Classification Symbol.
    * TODO: make notesAndWarnings an Option? classTitle too if it is sometimes empty.
    */
  case class ClassificationItem(id: Option[Int], parentId: Int, breakdownCode: Boolean, allocatable: Boolean, additionalOnly: Boolean,
    dateRevised: String, level: Int, symbol: String, classTitle: String, notesAndWarnings: String) {

    def toDescription(text: String => String) = Description(id.get, symbol, level, text(classTitle), text(notesAndWarnings))
  }

}
