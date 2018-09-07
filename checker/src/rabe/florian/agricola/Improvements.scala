package rabe.florian.agricola

/** major improvements with their building costs */
sealed abstract class Improvement(s: String, val wood: Int, val clay: Int, val reed: Int, val stone: Int) { 
  override def toString = s
  /** override for upgrading improvements */
  val mayUpgrade : List[Improvement] = Nil
  var _upgradedFrom: Option[Improvement] = None
  /** call this before buying if upgrade desired */
  def upgradedFrom(f: Improvement): this.type = {
    _upgradedFrom = Some(f)
    this
  }
  /** point value of this improvement */
  def points : Int
  /** bonus score of this improvement */
  def bonusScore(p: Player) = 0
  /** hook to trigger effects when building */
  def onBuild(g: GameInProgress) {}
  /** hook to trigger effects actions after building */
  def onStartOfRound(builtIn: Int)(g: GameInProgress) {}
  /** for cause of changes */
  implicit val cause = ByImprovement(this)
  
  val moor = false
}

/** from Farmers of the Moor */
trait MoorImprovement

/** improvements initially under some other improvement (for FotM expansion) */
trait ConcealedImp extends MoorImprovement {
  def under: Improvement
}

/** improvements that allow baking */
trait BakingImp {
  def limitedBake: List[Int] = Nil
  def unlimitedBake: Int = 0
}

/** conversion rates when cooking with a [[CookingImp]] */
case class CookRate(sheep: Int, boar: Int, cattle: Int, horse: Int, vegetable: Int) {
  def apply(c: Cookable) = c match {
    case Sheep => sheep
    case Boar => boar
    case Cattle => cattle
    case Horse => horse
    case Vegetable => vegetable
    case Grain => 1
  }
}

/** improvements that allow cooking */
trait CookingImp extends BakingImp {
  def cook: CookRate
  def cookAnimal(a: Animal) = a match {
    case Sheep => cook.sheep
    case Boar => cook.boar
    case Cattle => cook.cattle
    case Horse => cook.horse
  }
  def points = 1
}

trait Fireplace extends CookingImp {
  def cook = CookRate(2,2,3,0,2)
  override def unlimitedBake = 2
}
trait CookingHearth extends CookingImp {self: Improvement =>
  def cook = CookRate(2,3,4,0,3)
  override def unlimitedBake = 3
  override val mayUpgrade = List(Fireplace1,Fireplace2)
}
trait HorseCooking extends CookingImp {
  override abstract def cook = super.cook.copy(horse=2)
  override def points = 2
}
sealed abstract class HorseSlaughterhouse extends Improvement("Horse Slaughterhouse", 0, 1, 0, 1) with CookingImp {
  def cook = CookRate(1,1,2,2,1)
  override def points = 2
}
sealed abstract class Cookhouse extends Improvement("Cookhouse", 0, 6, 0, 0) with CookingImp {
  def cook = CookRate(2,3,4,2,3)
  override def unlimitedBake = 3
  override def points = 2
}

case object Fireplace1 extends Improvement("Fireplace", 0, 2, 0, 0) with Fireplace
case object Fireplace2 extends Improvement("Fireplace", 0, 3, 0, 0) with Fireplace
case object CookingHearth1 extends Improvement("Cooking Hearth", 0, 4, 0, 0) with CookingHearth
case object CookingHearth2 extends Improvement("Cooking Hearth", 0, 5, 0, 0) with CookingHearth
case object HorseSlaughterhouse1 extends HorseSlaughterhouse with ConcealedImp {
  def under = Fireplace1
}
case object HorseSlaughterhouse2 extends HorseSlaughterhouse with ConcealedImp {
  def under = Fireplace2
}
case object Cookhouse1 extends Cookhouse with ConcealedImp {
  def under = CookingHearth1
}
case object Cookhouse2 extends Cookhouse with ConcealedImp {
  def under = CookingHearth2
}

trait Oven
case object ClayOven extends Improvement("Clay Oven", 0, 3, 0, 1) with BakingImp with Oven {
  override def limitedBake = List(5)
  def points = 2
}
case object StoneOven extends Improvement("Stone Oven", 0, 1, 0, 3) with BakingImp with Oven {
  override def limitedBake = List(4,4)
  def points = 3
}

case object Furnace extends Improvement("Furnace", 0, 1, 0, 1) with ConcealedImp {
  def under = ClayOven
  def points = 1
  override def onBuild(g: GameInProgress) {
    g.gain(2, Fuel)
  }
}
case object HeatingStove extends Improvement("Heating Stove", 0, 2, 0, 1) with ConcealedImp {
  def under = StoneOven
  def points = 1
}

case object Well extends Improvement("Well", 1, 0, 0, 3) {
  def points = 4
  override def onStartOfRound(builtIn: Int)(g: GameInProgress) {
    if (g.currentRound-builtIn <= 5) {
       g.gain(1, Food)
    }
  }
}
case object VillageChurch extends Improvement("Village Church", 2, 0, 0, 4) {
  def points = 4
  override def onBuild(g: GameInProgress) {
    g.gain(2, Food)
  }
}

/** common properties of joinery, pottery, basketmaker's workshop */ 
trait ConvertingImprovement {self: Improvement =>
  def points = 2
  def convert: Resource
  def food: Int
  def bonus: List[Int]
  override def bonusScore(p: Player) = {
    val c = p.resource(convert)
    bonus.takeWhile(_ <= c).length
  }
}
case object Joinery extends Improvement("Joinery", 2, 0, 0, 2) with ConvertingImprovement {
  def convert = Wood
  def food = 2
  def bonus = List(3,5,7)
}
case object Pottery extends Improvement("Pottery", 0, 2, 0, 2) with ConvertingImprovement {
  def convert = Clay
  def food = 2
  def bonus = List(3,5,7)
}
case object Basketry extends Improvement("Basketmaker's Workshop", 0, 0, 2, 2) with ConvertingImprovement {
  def convert = Reed
  def food = 3
  def bonus = List(3,4,5)
}

/** exchange rates used by the [[ExchangingImprovement]]s */
case class ExchangeRate(what: Resource, wood: Boolean, clay: Boolean, reed: Boolean, stone: Boolean)
/** common properties of the 3 stalls */
trait ExchangingImprovement {
  def exchangeRate: ExchangeRate
  def exchange(from: Resource, into: Resource) = {
    exchangeRate.what == from && (into match {
      case Wood => exchangeRate.wood
      case Clay => exchangeRate.clay
      case Reed => exchangeRate.reed
      case Stone => exchangeRate.stone
    })
  }
  def points = 2
}
case object WoodStall extends Improvement("Furniture Stall", 1, 0, 0, 1) with ExchangingImprovement with ConcealedImp {
  def exchangeRate = ExchangeRate(Wood, true, true, false, false)
  def under = Joinery
}
case object ClayStall extends Improvement("Ceramics Stall", 0, 1, 0, 1) with ExchangingImprovement with ConcealedImp {
  def exchangeRate = ExchangeRate(Clay, true, true, false, false)
  def under = Pottery
}
case object ReedStall extends Improvement("Basket Stall", 0, 0, 1, 1) with ExchangingImprovement with ConcealedImp {
  def exchangeRate = ExchangeRate(Reed, true, true, true, true)
  def under = Basketry
}

case object Kiln extends Improvement("Peat-charcoal Kiln", 0, 0, 0, 1) with MoorImprovement {
  def points = 1
  override def bonusScore(p: Player) = {
    val f = p.currency(Fuel)
    List(3,5).takeWhile(_ <= f).length
  }
}

case object ForestersLodge extends Improvement("Forester's Lodge", 1, 2, 0, 0) with MoorImprovement {
  def points = 1
  override def bonusScore(p: Player) = p.forest
}

case object MuseumOfTheMoor extends Improvement("Museum of the Moor", 0, 1, 1, 1) with ConcealedImp {
  def under = Kiln
  def points = 3
  def reduction(imp: Improvement) = imp match {
    case StoneOven | Well => (0,0,0,1)
    case ClayOven | Pottery | ForestersLodge => (0,1,0,0)
    case Joinery => (1,0,0,0)
    case Basketry => (0,0,1,0)
    case _ => (0,0,0,0)
  }
}
case object RidingStables extends Improvement("Riding Stables", 2, 1, 1, 0) with ConcealedImp {
  def under = ForestersLodge
  def points = 3
  override def onStartOfRound(buildIn: Int)(g: GameInProgress) {
    if (g.player(Horse) >= 2)
      g.gain(1, Food)
  }
}