package rabe.florian.agricola.games

import rabe.florian.agricola._

abstract class FamilyGameCards extends ProgrammaticSinglePlayerGame(GameType(true,false)) {
  import SpaceAbbreviations._
  
  val roundActionSpaces: List[RoundActionSpace] = List(
      TS,SB,MI,Fe, Ts2,FG,RI, TB,TV, TC,Ts4, FGW,PS, RF
  )
  
  val specialActionCards = Nil
}

/**  */
object EarlyFoodGrab extends FamilyGameCards with ActionAbbreviations {
  
  round(Pl, TG)
  round(Pl, TG)
  round(DL(Wood), SB(0, G,G))
  round(DL(Stone), Fi)

  round(Tr,Tw)
  round(Build(2), FG)
  round(FG, Fi, SP)

  // 8
  round(Pl, DL(Wood), Tc, MI(ClayOven) and Bake(1))
  // 9
  round(Pl, SB(1,G,G), DL(Reed), BakeAndStable(Some(a0), 0))
  harvest()()

  // 10
  round(DL(Wood), TV, BakeAndStable(Some(a1), 1), MI(Fireplace1))
  // 11
  round(SB(1, G, V), BakeAndStable(Some(a2), 1), Ts2, MI(Pottery))
  
  val past = List(Pa(a0,b0),Pa(a1,b1),Pa(a2),Pa(b2))

  // 12
  round(FGW, Tc, RI(StoneOven) and Bake(3), Tr)
  // 13
  round(Tw, Fe(past:_*), TS and Cook(7, Sheep), TB and Cook(1,Boar), TC)

  // 14
  round(PlowAndSow(G, G, V, V), Ts2, Ts4, MI(Well), RI(Basketry))
  harvest()()
}
