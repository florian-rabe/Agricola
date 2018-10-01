package rabe.florian.agricola.games

import rabe.florian.agricola._

/** createa HTML for a list of programmaticgames */
object PlayAll {
  /** the games to run */
  val games = List(EarlyFoodGrab,TwoWoodRooms,OneWoodRoom,OneWoodOneClayRoom,ManyHorses)
  def main(args: Array[String]) {
    val base =  new java.io.File( "." ).getCanonicalPath()
    games foreach {g =>
      val n = g.getClass.getName.split("\\.").last
      val f = base + "\\" + n.substring(0,n.length-1) + ".html"
      g.main(Array(f))
    }
  }
}