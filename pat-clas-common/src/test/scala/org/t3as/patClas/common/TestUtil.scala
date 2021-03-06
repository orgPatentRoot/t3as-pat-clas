/*
    Copyright 2013 NICTA
    
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

package org.t3as.patClas.common

import scala.collection.JavaConversions._
import org.scalatest.{Matchers, FlatSpec}
import org.t3as.patClas.common.CPCUtil.ClassificationItem

class TestUtil extends FlatSpec with Matchers {

  "properties" should "load from classpath" in {
    val p = Util.properties("/util-test.properties")
    p("name") should be ("value")
  }
  
  "toText" should "concatenate all text node descendents in document order, trimming whitespace" in {
    val xml = """<a> text1 <elem>text  2</elem>   <b>text   3<elem>text 4</elem>  </b>  <elem>text 5</elem>text    6</a>"""
    val s = Util.toText(xml)  
    s should be ("text1\ntext 2\ntext 3\ntext 4\ntext 5\ntext 6")
  }

  "ltrim" should "left trim" in {
    for ((in, out) <- Seq(("", ""), ("0", ""), ("00", ""), ("01", "1"), ("001", "1"), ("010", "10"), ("00100", "100"))) {
      Util.ltrim(in, '0') should be (out)
    }
  }

  "rtrim" should "right trim" in {
    for ((in, out) <- Seq(("", ""), ("0", ""), ("00", ""), ("10", "1"), ("100", "1"), ("010", "01"), ("00100", "001"))) {
      Util.rtrim(in, '0') should be (out)
    }
  }
  
  "Classification" should "update parentId" in {
    ClassificationItem(None, -1, true, true, true, "2013-01-01", 2, "symbol", "classTitle", "notesAndWarnings").copy(parentId = 3).parentId should be (3)
  }
  
  "toCpcFormat" should "convert to CPC style format" in {
    for ((in, out) <- Seq(("A01B0012987000", "A01B12/987"), ("A01B0012986000", "A01B12/986"), ("A01B0012000000", "A01B12"))) {
      IPCUtil.toCpcFormat(in) should be (out)
    }
  }
  
  "IPCUtil.ipcToText" should "concatenate all text node descendents and sref/@ref in document order, trimming whitespace" in {
    val xml = """<a> text1 <elem>text  2</elem>   <b>text   3 ref to <sref ref="A01B0012986000"/><elem>text 4</elem>  </b>  <elem>text 5</elem>text    6</a>"""
    val s = IPCUtil.ipcToText(xml)  
    s should be ("text1\ntext 2\ntext 3 ref to\nA01B12/986\ntext 4\ntext 5\ntext 6")
  }


}
