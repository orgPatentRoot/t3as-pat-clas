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

object USPC {

  case class Description(id: Int, symbol: String, classTitle: String, subClassTitle: String, subClassDescription: String, text: String)

  case class Hit(score: Float, symbol: API.Symbol, classTitleHighlights: String, subClassTitleHighlights: String, subClassDescriptionHighlights: String, textHighlights: String)  extends API.HitBase

  /** Names of USPC fields in the Lucene index. */
  object IndexFieldName extends Enumeration {
    type IndexFieldName = Value
    val Symbol, ClassTitle, SubClassTitle, SubClassDescription, Text = Value

    implicit def convert(f: IndexFieldName) = f.toString
  }
  import IndexFieldName._
  
  val textFields: Array[String] = Array(ClassTitle, SubClassTitle, SubClassDescription, Text)
  val hitFields: Set[String] = Set(Symbol, ClassTitle, SubClassTitle, SubClassDescription, Text)
  
  def mkHit(score: Float, f: Map[String, String], h: Map[String, String]) = {
    def getH(s: String) = h.getOrElse(s, f.getOrElse(s, ""))
    def getHU(s: String) = h.getOrElse(s, f.getOrElse(s, "").toUpperCase)
    Hit(score, API.Symbol(f(Symbol).toUpperCase, getHU(Symbol)), getH(ClassTitle), getH(SubClassTitle), getH(SubClassDescription), getH(Text))
  }

  /** Entity class mapping to a database row representing a USPC Symbol.
    */
  case class UsClass(id: Option[Int], xmlId: String, parentXmlId: String, symbol: String, classTitle: Option[String], subClassTitle: Option[String], subClassDescription: Option[String], text: String) {
    def toDescription(f: String => String) = Description(id.get, symbol, classTitle.getOrElse(""), subClassTitle.map(f).getOrElse(""), subClassDescription.map(f).getOrElse(""), f(text))
  }

}
