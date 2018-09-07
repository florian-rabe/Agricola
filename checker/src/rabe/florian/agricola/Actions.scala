package rabe.florian.agricola

sealed abstract class Move {
  def and(moves: OtherMove*) = this match {
    case c: CompoundMove => CompoundMove(c.primary, c.secondary ::: moves.toList)
    case _ => CompoundMove(this, moves.toList)
  }
  def allowedIn(gameType: GameType) = true
}

/** an action with a family member
 *  consists of an action space and possibly additional arguments
 */
sealed abstract class Action extends Move {
  def space: ActionSpace
}
trait Sowing {
  def sow: Seq[Produce]
}
trait Baking {
  def bake: Int
}
trait Fencing {
  def pastures : Pastures
}

/** the space on the board */
sealed trait ActionSpace
/** the 14 spaces that are revealed per round */
sealed trait RoundActionSpace extends ActionSpace {
  val stage: Int 
}

/** any action that takes no arguments other than its action space
 * for simplicity, these action are identified with the corresponding action space
 */
sealed trait SimpleAction extends Action with ActionSpace {
  def space = this
}

/** any action that takes arguments in addition to its action space */
sealed abstract class ComplexAction(val space: ActionSpace) extends Action

sealed trait CumulativeSpace extends SimpleAction {
  def restock = 1
}

sealed abstract class TakeRes(val resource: Resource) extends CumulativeSpace {
  override def toString = resource.toString 
}
case object TakeWood extends TakeRes(Wood) {
  override def restock = 2
}
case object TakeClay extends TakeRes(Clay)
case object TakeReed extends TakeRes(Reed)
sealed abstract class TakeStone(val stage: Int) extends TakeRes(Stone) with RoundActionSpace
case object TakeStone2 extends TakeStone(2) {
  override def toString = resource.toString + "1"
}
case object TakeStone4 extends TakeStone(4) {
  override def toString = resource.toString + "2"
}

sealed abstract class TakeAnimal(val animal: Animal, val stage: Int) extends CumulativeSpace with RoundActionSpace  {
  override def toString = animal.toString
}
case object TakeSheep extends TakeAnimal(Sheep, 1)
case object TakeBoar extends TakeAnimal(Boar, 3)
case object TakeCattle extends TakeAnimal(Cattle, 4)

sealed abstract class TakeProduce(val produce: Produce) extends SimpleAction {
  override def toString = produce.toString
}
case object TakeGrain extends TakeProduce(Grain)
case object TakeVegetable extends TakeProduce(Vegetable) with RoundActionSpace {
  val stage = 3
}

case object BuildSpace extends ActionSpace
case class Build(rooms: Int, stables: Square*) extends ComplexAction(BuildSpace) {
  override def toString = {
    val rS = if (rooms != 0) List(s"$rooms rooms") else Nil
    val sNum = stables.length
    val sS = if (sNum != 0) List(s"$sNum stables") else Nil
    (rS:::sS).mkString(", ")
  }
}

case object FencesSpace extends RoundActionSpace {
  val stage = 1
}
case class Fences(val pastures: Pastures) extends ComplexAction(FencesSpace) with Fencing {
  override def toString = "Fences"
}

case object BakeAndStableSpace extends ActionSpace
case class BakeAndStable(stable: Option[Square], bake: Int) extends ComplexAction(BakeAndStableSpace) with Baking {
  override def allowedIn(g: GameType) = g.family
  override def toString = {
    val sS = if (stable.isDefined) " and stable" else ""
    s"Bake $bake$sS"
  }
}

case object ImproveSpace extends RoundActionSpace {
  val stage = 1
}
case class Improve(improvement: Improvement) extends ComplexAction(ImproveSpace) {
  override def toString = improvement.toString
}

case object RenovImprovementSpace extends RoundActionSpace {
  val stage = 2
}
case class RenovImprovement(improvement: Option[Improvement]) extends ComplexAction(RenovImprovementSpace) {
  override def toString = {
    val iS = improvement match {case Some(i) => ", " + i.toString case None => ""}
    "Renovate" + iS
  }
}

case object RenovFencesSpace extends RoundActionSpace {
  val stage = 6
}
case class RenovFences(val pastures: Pastures) extends ComplexAction(RenovFencesSpace) with Fencing {
  override def toString = {
    "Renovate, Fences"
  }
}

case object Plow extends SimpleAction {
  override def toString = "Plow"
}

case object SowAndBakeSpace extends RoundActionSpace {
  val stage = 1
}
case class SowAndBake(bake: Int, sow: Produce*) extends ComplexAction(SowAndBakeSpace) with Sowing with Baking {
  override def toString = {
    val bS = if (bake != 0) List("Bake " + bake) else Nil
    val sowS = List(sow.mkString("Sow ", ", ", ""))
    (bS:::sowS).mkString(", ")
  }
}

case object PlowAndSowSpace extends RoundActionSpace {
  val stage = 5
}
case class PlowAndSow(sow: Produce*) extends ComplexAction(PlowAndSowSpace) with Sowing {
  override def toString = {
    val sowS = sow.mkString(", ")
    List("Plow", sowS).mkString(", Sow ")
  }
}

sealed abstract class FamilyGrowthAction(val withRoom: Boolean, val stage: Int) extends SimpleAction with RoundActionSpace {
  override def toString = "Family growth" + (if (withRoom) "" else " without room")
}
case object FamilyGrowth extends FamilyGrowthAction(true, 2)
case object FamilyGrowthWithoutRoom extends FamilyGrowthAction(false, 5)

case object Fishing extends CumulativeSpace {
  override def toString = "Fishing"
}

case object StartingPlayer extends CumulativeSpace {
  override def toString = "Starting player"
}

case object DayLaborerSpace extends ActionSpace
case class DayLaborer(resource: Resource) extends ComplexAction(DayLaborerSpace) {
  override def toString = "Day laborer" + Option(resource).map(": " + _).getOrElse("")
  override def allowedIn(g: GameType) = {
    if (g.family) resource != null else resource == null
  }
}
object DayLaborer {
  /** for non-family version */
  def apply(): DayLaborer = DayLaborer(null)
}

sealed trait MoorMove extends Move {
   override def allowedIn(g: GameType) = g.moor && super.allowedIn(g)
}

/** the space that allows taking an action */
sealed trait SpecialActionSpace
/** the action itself */
sealed trait SpecialAction extends MoorMove {
  def space: SpecialActionSpace
}
sealed abstract class SimpleSpecialAction(s: String) extends SpecialAction with SpecialActionSpace {
  def space = this
  override def toString = s
}

case object ClandestineWorkSpace extends SpecialActionSpace
case class ClandestineWork(improvement: Improvement) extends SpecialAction {
  def space = ClandestineWorkSpace
  override def toString = "Clandestine Work: " + improvement.toString 
}
case object BlackMarketSpace extends SpecialActionSpace
case object HiringFair extends SimpleSpecialAction("Hiring Fair")
case object HorseCoper extends SimpleSpecialAction("Horse Coper")
case object CutPeat extends SimpleSpecialAction("Cut Peat")
case object SlashAndBurn extends SimpleSpecialAction("Slash and Burn")
case object FellTrees extends SimpleSpecialAction("Fell Trees")

object SpecialActionCard {
  def cb = List(ClandestineWorkSpace,BlackMarketSpace)
  def cbh = HiringFair :: cb
  def cs = List(CutPeat,SlashAndBurn)
}
import SpecialActionCard._

sealed abstract class SpecialActionCard(val spaces: List[SpecialActionSpace]) {
  def payForHC: Boolean = false
  def twoFromHF: Boolean = false
}
trait PayForHC {self: SpecialActionCard =>
  override def payForHC = true
}
case object BMCW extends SpecialActionCard(cb)
case object BMCWHF extends SpecialActionCard(cbh)
case object BMCWHFHC extends SpecialActionCard(HorseCoper::cbh) with PayForHC
case object BMCWHFFT extends SpecialActionCard(FellTrees::HiringFair::cb) {
   override val twoFromHF = true
}
case object CPSB extends SpecialActionCard(cs)
case object CPSBFT extends SpecialActionCard(FellTrees::cs)
case object CPSBHC extends SpecialActionCard(HorseCoper::cs)
case object HC extends SpecialActionCard(List(HorseCoper)) with PayForHC
case object HCFT extends SpecialActionCard(List(HorseCoper,FellTrees))
case object HFFT extends SpecialActionCard(List(HiringFair,FellTrees))

sealed abstract class OtherMove(s: String) extends Move {
  override def toString = s
}
sealed trait OncePerHarvest
case class Burn(number: Int) extends OtherMove("Burn " + number) with MoorMove
case class Bake(bake: Int) extends OtherMove("Bake " + bake) with Baking
case class Cook(number: Int, cookable: Cookable) extends OtherMove(s"Cook $number $cookable")
case class Release(number: Int, animal: Animal) extends OtherMove(s"Release $number $animal")
case class Exchange(imp: ExchangingImprovement, from: Resource, to: Resource) extends OtherMove(s"Exchange $from") with MoorMove
case class Convert(imp: ConvertingImprovement) extends OtherMove("Convert " + imp.convert) with OncePerHarvest
case object VillageChurchBonus extends OtherMove("Village church bonus") with OncePerHarvest with MoorMove

/**
 * make secondary moves licensed by the primary move
 * e.g., baking after oven-building, or cooking after taking animals 
 */ 
case class CompoundMove(primary: Move, secondary: List[OtherMove]) extends Move {
  override def toString = primary + " and " + secondary.mkString(", ")
}