package rabe.florian.agricola.games

import rabe.florian.agricola._

abstract class Cards extends ProgrammaticSinglePlayerGame(GameType(true,true)) {
  import SpaceAbbreviations._
  
  val roundActionSpaces: List[RoundActionSpace] = List(
      MI,TS,Fe,SB,Ts2,FG,RI,TB,TV,Ts4,TC,FGW,PS,RF
  )
  
  val specialActionCards = List(
     HC,BMCWHF,CPSB,BMCWHFFT,HFFT,CPSBHC,BMCW,CPSBFT,BMCWHFHC,HCFT
  )
}

/**
 * Heavy use of Horses, 87 points.
 */
object ManyHorses extends Cards with ActionAbbreviations {
  import Square._
  
  round(DL(Stone), HC, TG)
  round(HF, MI(Kiln), TG)
  round(DL(Clay), CP, SB, Pl)
  round(Fi, HF, SB(0, G,G))

  round(FT, Tw, Tr)
  round(CP, Build(2,a1), FG)
  round(DL(Clay), CW(Fireplace1), TS and Cook(6, Sheep), FG)

  round(CP, SB, DL(Reed), Ts2, Tw, Pl)
  round(Fi, Tc, CW(MuseumOfTheMoor), HC, RI(ClayOven) and Bake(1), SB(1,G,G))
  harvest()(Release(1,Horse))

  round(DL(Stone), FT, Fe(Pa(a0,b0)), BS(a2, 1), FT, MI(Well))
  round(TV, SB(1, G, V), BS(a0, 1), HC, HC, MI(Furnace))
  
  round(Ts2, BS(b0, 1), MI(StoneOven) and Bake(2), HC, HC, FGW)
  val fe = Fe(Pa(a0,b0),Pa(a1,b1),Pa(a2),Pa(b2))
  round(HC, HC, Ts4, Tr, RI(Basketry), MI(CookingHearth1.upgradedFrom(Fireplace1)), TC and Cook(1,Cattle))
 
  round(HC, HC, PlowAndSow(G, G, V, V), Tw and Burn(3), fe, TS, TB and Cook(5, Boar))
  harvest(Convert(Basketry))()
}