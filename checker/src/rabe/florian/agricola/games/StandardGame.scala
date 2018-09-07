package rabe.florian.agricola.games

import rabe.florian.agricola._

abstract class StandardGameCards extends ProgrammaticSinglePlayerGame(GameType(false,false)) {
  import SpaceAbbreviations._
  
  val roundActionSpaces: List[RoundActionSpace] = List(
      TS,SB,MI,Fe, Ts2,FG,RI, TB,TV, Ts4,TC, FGW,PS, RF
  )
  
  val specialActionCards = Nil
}

/**
 * Pastures are given as size/capacity.
 * 
 * Wood:
 *  We have at most 28 Wood, from which we build 2 Rooms and the Well. That leaves 17 Wood for animals.
 *  Building only 1 Wood Room leaves 22 Wood.
 *  Not building the Well leaves 18 resp. 23 Wood, but it is very hard to make up the 4 points of the Well elsewhere.
 *  
 *  Maximum animal points based on available Wood for stables and pastures, assuming free fields are filled otherwise:
 *  17 Wood:
 *  - 13 Fences + 2 Stables: 3 Pastures 1/4, 1/4, 4/8, accommodates 8S, 5B, 4C ==> 15 points, 6 squares
 *  - 15 Fences + 1 Stable:  4 Pastures 2/8, 2/4, 1/2, 1/2, accommodates 8S, 5B, 4C ==> 15 points, 6 squares
 *  - 11 Fences + 3 Stables: 3 Pastures 2/8, 1/4, 1/4, accommodates 8S, 5B, 4C ==> 16 points, 4 squares
 *  The former is better because it allows breeding 1 animal before building fences.
 *  
 *  18 Wood:
 *  - 14 Fences + 2 Stables: 3 Pastures 2/8, 2/8, 2/4, accommodates 8S, 7B, 4C ==> 16 points, 6 squares
 *  - 14 Fences + 2 Stables: 4 Pastures 2/8, 1/4, 1/2, 1/2, accommodates 8S, 5B, 4C ==> 16 points, 5 squares
 *  - 12 Fences + 3 Stables: 4 Pastures 1/4, 1/4, 1/4, 1/2, accommodates 8S, 5B, 2C ==> 16 points, 4 squares
 *  
 *  19 Wood:
 *  - 13 Fences + 3 Stables: 3 Pastures 2/8, 2/8, 1/4, accommodates 8S, 7B, 4C ==> 17 points, 5 squares
 *
 *  20 Wood:
 *  - 12 Fences + 4 Stables: 4 Pastures 1/4, 1/4, 1/4, 1/4, accommodates 8S, 5B, 4C ==> 18 points, 4 squares
 *  - 14 Fences + 3 Stables: 4 Pastures 2/8, 1/4, 1/4, 1/2, accommodates 8S, 7B, 4C ==> 18 points, 5 squares
 * 
 *  21 Wood:
 *  - 13 Fences + 4 Stables: 3 Pastures 2/16, 2/8, 1/4, accommodates 8S, 7B, 4C ==> 18 points, 5 squares
 *
 *  22 Wood:
 *  - 14 Fences + 4 Stables: 4 Pastures 2/8, 1/4, 1/4, 1/4, accommodates 8S, 7B, 4C ==> 19 points, 5 squares
 *  - 15 Fences + 3 Stables (1 Wood left): 4 Pastures 2/8, 2/8, 1/4, 1/2, accommodates 8S, 7B, 6C ===> 19 points, 6 squares
 *  
 *  23 Wood:
 *  - 15 Fences + 4 Stables: 4 Pastures 2/8, 2/4, 1/4, 1/4, accommodates 8S, 7B, 6C ==> 20 points, 6 squares
 *
 *  Pastures covering 4 squares hold at most animals for 10 out of 12 points.
 *  Pastures covering 5 squares hold at most animals for 11 out of 12 points.
 *  Pastures covering 7 squares or more can make at most 3 Pastures holding at most animals for 11 (7 squares) or 10 (8 squares) out of 12 points.
 *  12 Fences covering 5 squares can make at most 2 Pastures.
 *  13 Fences covering 5 squares can make at most 3 Pastures.
 *  
 * Produce:
 *  5 Fields, 8G, 4V ==> 12 points.
 *  
 * Family:
 *  5 members in 4 Stone Rooms ==> 23 points.  
 */

/** 2 early Wood Rooms, 67 points */
object TwoWoodRooms extends StandardGameCards with ActionAbbreviations {
  
  val Pass = SP
  
  round(Pl, TG)
  round(Pl, TG)
  round(DL(), SB(0, G,G))
  round(Fi, Tr)

  round(Tw, Build(2))
  round(FG, Tc)
  round(MI(Fireplace1), TS and Cook(7, Sheep), FG)

  // 8
  round(Pl, Ts2,    Fi, Pass)
  // 9
  round(Pl, MI(ClayOven) and Bake(1), SB(1,G,G),    Pass)
  harvest()()

  // 10
  round(Tw, MI(Well), TV,    Pl)
  // 11
  round(Build(0,a0,b0,a1), MI(CookingHearth1.upgradedFrom(Fireplace1)), SB(3, G, V),      Pl)
  
  // 12
  round(Tc, FGW, MI(Fireplace1), TC)
  // 13
  round(Ts4, Ts2, Tr, RI(Basketry), MI(StoneOven) and Bake(3))

  // 14
  val past = Pastures(Pa(a0),Pa(b0),Pa(a1,b1))
  round(PlowAndSow(G, G, V, V), Tw, RenovFences(past), TS, TB and Cook(3, Boar))
  harvest()()
}

/** 1 Wood Room only, 66 points */
object OneWoodRoom extends StandardGameCards with ActionAbbreviations {
  
  val Pass = SP
  
  round(Pl, TG)
  round(Pl, TG)
  round(DL(), Tr)
  round(Fi, SB(0, G,G))

  round(Tc, MI(Fireplace1))
  round(Tw, TS and Cook(6, Sheep))
  round(Build(1, a0, a1, a2), FG)

  // 8
  round(Pl, Ts2, MI(ClayOven) and Bake(1))
  // 9
  round(Pl, MI(Well), SB(1,G,G))
  harvest()()

  // 10
  round(TV, Tc, SB(3, G, V))
  // 11
  round(Pl, Tr,        Pass)
  
  // 12
  round(FGW, Ts2, MI(StoneOven) and Bake(3))
  // 13
  round(FGW, Ts4, RI(Basketry), TC)

  // 14
  val past = Pastures(Pa(a0),Pa(b0),Pa(a1,b1),Pa(a2,b2))
  round(Tw, RenovFences(past), TB and Cook(1, Boar), TS, PlowAndSow(G,G,G,V,V))
  harvest(Cook(2,Grain), Cook(1,Sheep), Cook(1, Cattle))()
}

/** 1 Wood, 1 Clay Room, 67 points */
object Current extends StandardGameCards with ActionAbbreviations {
  
  val Pass = SP
  
  round(Pl, TG)
  round(Pl, TG)
  round(DL(), Tw)
  round(Fi, SB(0, G,G))

  round(Tr, Build(1))
  round(Tc, MI(Fireplace1))
  round(TS and Cook(7, Sheep), FG)

  // 8
  round(Pl, Tw, RI(CookingHearth1.upgradedFrom(Fireplace1)))
  // 9
  round(Pl, TV, SB(1,G,G))
  harvest()()

  // 10
  round(Ts2, MI(Well), SB(3, G, V))
  // 11
  round(Tc, Build(1, a0, a1, a2, b0), FG)
  
  // 12
  round(FGW, Pl, TV, MI(StoneOven) and Bake(4))
  // 13
  round(Ts2, Ts4, Tr, MI(Basketry), TC and Cook(1, Cattle))

  // 14
  val past = Pastures(Pa(a0),Pa(b0),Pa(a1,b1),Pa(a2))
  round(Tw, RenovFences(past), TB and Cook(1, Boar), TS, PlowAndSow(G,G,V,V,V))
  harvest(Cook(2,Vegetable))()
}