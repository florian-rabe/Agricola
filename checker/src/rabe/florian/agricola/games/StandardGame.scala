package rabe.florian.agricola.games

import rabe.florian.agricola._

abstract class StandardGameCards extends ProgrammaticSinglePlayerGame(GameType(false,false)) {
  import SpaceAbbreviations._
  
  val roundActionSpaces: List[RoundActionSpace] = List(
      TS,SB,MI,Fe, Ts2,FG,RI, TB,TV, Ts4,TC, FGW,PS, RF
  )
  
  val specialActionCards = Nil
}

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
object OneWoodOneClayRoom extends StandardGameCards with ActionAbbreviations {
  
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