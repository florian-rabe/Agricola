package rabe.florian.agricola

import rabe.florian.utils._
import Auxil._

/**
 * optimized for typing a game as Scala code
 *
 * instances of this class can be run to play the game
 * passing a file name as a command line argument will generate an HTML log in that file
 */
abstract class ProgrammaticSinglePlayerGame(g: GameType) extends SinglePlayerGame(g) {
  private var _rounds : List[Round] = Nil 
  def rounds = _rounds
  def round(moves: Move*) {
    _rounds = _rounds ::: List(Round(moves.toList, Nil, Nil))
  }
  def harvest(aF: OtherMove*)(aB: OtherMove*) = {
    val r = rounds.last
    val rh = r.copy(afterBreeding = aB.toList, afterFieldPhase = aF.toList)
    _rounds = rounds.init ::: List(rh)
  }
  
  def main(args: Array[String]) {
    val (gip, cls) = if (args.length == 0) {
       (new GameInProgress(this, EmptyGameHooks), () => ())
    } else {
       val h = new HTMLHooks(File(args(0)), g)
       (new GameInProgress(this, h), () => h.close)
    }
    gip.play
    cls()
    println("\n\nScoring\n" + Score(gip.player,gip.game.gameType))
  }
}

/** produce HTML log of game */
class HTMLHooks(f: File, g: GameType) extends GameHooks {
   val h = new HTMLFileWriter(f)
   import h._
   implicit def iToS(i: Int) = i.toString
   literal("<html>")
   head {
     css("agricola.css")
     javascript("https://code.jquery.com/jquery-2.1.4.min.js")
     javascript("agricola.js")
   }
   literal("<body>")
   literal(s"<h2>${g.toStringLong}</h2>")

   def pointRow(cls: String, a: String, b: String, c: String) {
      tr(cls) {
        td {text(a)}
        td {text(b)}
        td {text(c)}
      }
   }
   def pointTable(score: Score) {
       table {
         score.family match {case (c,p) =>
           pointRow("","family members", c, p)
         }
         score.rooms match {case (r,c,p) =>
           pointRow("",r.toString + " rooms", c, p)
         }
         score.categories.foreach {case (s,c,p) =>
           pointRow("",s.toString, c, p)
         }
         score.empty match {case (c,p) =>
           pointRow("","empty squares", c, p)
         }
         tr {
           td("",attributes = List("colspan"->3)) {text("improvements")}
         }
         score.improvements.foreach {case (i,p,b) =>
           val bS = if (b != 0) "+" + b else ""
           pointRow("improvement",i.toString, "", p.toString + bS)
         }
         if (score.bonus != 0)
           pointRow("", "bonus points","",score.bonus)
         pointRow("total", "total", "", score.total)
       }     
   }
   
   def doTokenGroup[T <: Token](p: Player, g: TokenGroupObject[T], inset: Boolean) {
    val gS = g.toString.toLowerCase
    g.all foreach {t =>
      val tS = t.toString.toLowerCase
      span(s"$gS tokens-$tS"){if (inset) text(p(t))}
      if (!inset) text(p(t))
    }
   }

   override def afterRound(g: GameInProgress) {
     printStatus(g, g.getNewEvents, false)
   }
   override def afterHarvest(g: GameInProgress) {
     printStatus(g, g.getNewEvents, true)
   }

   def printStatus(g: GameInProgress, events: List[Event], harvest: Boolean) {
      val p = g.player
      val b = g.board
      val score = Score(p,g.game.gameType)
      div(if (harvest) "harvest" else "round") {
        div("round-title") {
          if (harvest) text("Harvest")
          else text("Round " + g.currentRound)
        }
        div("events") {ul {
          events.foreach {
            case BeginMove(m) =>
              val mP = m match {case CompoundMove(mP,_) => mP case _ => m}
              val mC = mP match {
                case _:Action => "move-member"
                case _:SpecialAction => "move-special"
                case _:OtherMove => "move-other"
                case _:CompoundMove => ""//impossible
              }
              literal(s"<li class='move $mC'>")
              text(m.toString)
              literal("<ul>")
            case EndMove(m) =>
              literal("</ul></li>")
            case ch: Change =>
               li("change") {
                 val chS = if (ch.cause.isInstanceOf[ByMove])
                   ch.changeString
                 else
                   ch.toString
                 text(chS)
               }
            case BeginHarvest =>
            case EndHarvest =>
          }}
        }
        div("farmyard") {
          div("family") {
            val cls = "square room-"+p.hut.toString.toLowerCase
            Range(0,p.room).foreach {i =>
              span(cls) {}
            }
            span("member") {text(p.member)}
          }
          div("fields") {
            p.field.foreach {f =>
              val (cls,tx) = f.getProduce match {
                 case Some(s) => (s.toString.toLowerCase,f.getAmount.toString)
                 case None => ("empty", "")
              }
              span("square field-"+cls) {
                text(tx)
              }
            }
            doTokenGroup(p, Produce, true)
          }
          div("animals") {
            val fences = p.animalKeeping.getPastures.fences
            val stable = p.animalKeeping.getStables
            val maxCol = (1::p.animalKeeping.squares.map(_.column)).max
            table {
              upto(3).foreach {row =>
                tr {
                  upto(maxCol+1).foreach {col =>
                     val sq = Square(col,2-row)
                     val edg = sq.edges zip List("left", "right", "bottom", "top")
                     val fnc = edg.filter(e => fences contains e._1)
                     val stb = if (stable contains sq)
                       "stable"
                     else
                       ""
                     val cls = "pasture " + stb + " " + fnc.map(e => "edge-" + e._2).mkString(" ") 
                     td(cls) {}
                  }
                }
              }
            }
            doTokenGroup(p, Animal, false)
          }
          div("other") {
            div {
              upto(p.forest).foreach {i => span("square forest") {}}
              upto(p.moor).foreach {i => span("square moor") {}}
            }
            div {
              upto(p.emptySpaces).foreach {i => span("square empty") {}}
            }
          }
          div("tokens") {
            List(Resource,Currency).foreach {g =>
             div {doTokenGroup(p, g, true)}
            }
          }
          div {
            span("points") {text(score.total)}
          }
        }
      }
      if (harvest) {
        br
        if (g.currentRound == 14) {
          div("score") {
            pointTable(score)
          }
        }
      }
   }
   def close {
     literal("</body></html>")
     h.close
  }
}