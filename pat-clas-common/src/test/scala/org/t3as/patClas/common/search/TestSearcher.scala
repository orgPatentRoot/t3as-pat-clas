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

package org.t3as.patClas.common.search

import java.io.File
import scala.annotation.tailrec
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.IndexSearcher
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.LoggerFactory
import org.t3as.patClas.api.CPC, CPC.{hitFields, mkHit, Hit}, CPC.IndexFieldName.{ClassTitle, NotesAndWarnings, convert}
import org.t3as.patClas.api.API.HitBase
import resource.managed
import org.apache.lucene.analysis.core.KeywordAnalyzer

class TestSearcher extends FlatSpec with Matchers {
  val log = LoggerFactory.getLogger(getClass)

  "Searcher" should "search" in {
    for (searcher <- managed(new Searcher[Hit](Array(ClassTitle, NotesAndWarnings), Constants.cpcAnalyzer, hitFields, RAMIndex.makeTestIndex, mkHit))) {
      {
        val hits = searcher.search(s"${ClassTitle.toString}:FAKED") // analyzer should make this match "faking"
        hits.size should be(3)
        isDescending(hits) should be(true)
        
        hits forall { h =>
          h.classTitleHighlights.contains("""<span class="hlight">""")
        } should be(true)
      }
      {
        val hits = searcher.search(s"${NotesAndWarnings.toString}:Attention")
        hits.size should be(1)
        isDescending(hits) should be(true)
        hits forall { h =>
          h.notesAndWarningsHighlights.contains("""<span class="hlight">""")
        } should be(true)
      }
      {
        val hits = searcher.search("FAKED Attention") // Attention matches one doc in NotesAndWarnings, "FAKED" other 3 docs in ClassTitle
        log.debug(s"hits = $hits")
        hits.size should be(4)
        isDescending(hits) should be(true)
        
        hits count { h =>
          h.classTitleHighlights.contains("""<span class="hlight">""")
        } should be(3)
        hits count { h =>
          h.notesAndWarningsHighlights.contains("""<span class="hlight">""")
        } should be(1)
      }
      {
        val hits = searcher.search("fak*")
        log.debug(s"hits = $hits")
      }
      {
        val hits = searcher.search("Symbol:B29C3*")
        log.debug(s"hits = $hits")
      }
    }
  }

  def isDescending[H <: HitBase](list: List[H]) = isSorted(Nil ++ list, (a: H, b: H) => a.score >= b.score)

  @tailrec
  private final def isSorted[H <: HitBase](l: List[H], compare: (H, H) => Boolean): Boolean = l match {
    case Nil => true
    case h :: Nil => true
    case h :: t => compare(h, t.head) && isSorted(t, compare)
  }

}

