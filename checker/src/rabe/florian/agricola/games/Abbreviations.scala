package rabe.florian.agricola.games

import rabe.florian.agricola._

trait SimpleSpaceAbbreviations {
  val Tw = TakeWood
  val Tc = TakeClay
  val Tr = TakeReed
  val Ts2 = TakeStone2
  val Ts4 = TakeStone4
  
  val TG = TakeGrain
  val TV = TakeVegetable
  
  val TS = TakeSheep
  val TB = TakeBoar
  val TC = TakeCattle

  val Pl = Plow
  val FG = FamilyGrowth
  val FGW = FamilyGrowthWithoutRoom
  val Fi = Fishing
  val SP = StartingPlayer
} 

/** abbreviations for action spaces */
object SpaceAbbreviations extends SimpleSpaceAbbreviations {
  val BR = BuildSpace
  val Fe = FencesSpace
  val BS = BakeAndStableSpace
  val RI = RenovImprovementSpace
  val RF = RenovFencesSpace
  val MI = ImproveSpace
  val SB = SowAndBakeSpace
  val PS = PlowAndSowSpace
  val DL = DayLaborerSpace
}

/** abbreviations for actions */
trait ActionAbbreviations extends SimpleSpaceAbbreviations {self: SinglePlayerGame =>
  def DL(r: Resource = null) = DayLaborer(r)
  def MI(i: Improvement) = Improve(i)
  def SB(n: Int, ps: Produce*) = SowAndBake(n, ps:_*)
  def BS(s: Square, b: Int) = BakeAndStable(Some(s), b)
  def RI(i: Improvement) = RenovImprovement(Some(i))
  
/*
 * a2 b2
 * a1 b1
 * a0 b0
 */
  val a0 = Square(0,0)
  val a1 = Square(0,1)
  val a2 = Square(0,2)
  val b0 = Square(1,0)
  val b1 = Square(1,1)
  val b2 = Square(1,2)
  def Fe(ps: Pasture*) = Fences(Pastures(ps:_*))
  def Pa(ss: Square*) = Pasture(ss:_*)

  val FT = FellTrees
  val CP = CutPeat
  val SB = SlashAndBurn
  val HC = HorseCoper
  val HF = HiringFair
  def CW(i: Improvement) = ClandestineWork(i)
  
  def G = Grain
  def V = Vegetable
}