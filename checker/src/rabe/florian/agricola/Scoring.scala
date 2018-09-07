package rabe.florian.agricola

/**
 * segment-wise function
 * 
 * each segment (i,f) maps n>=i to f(n)
 * segments must be ordered by i
 */
class ScoringFunction(segments: (Int,Int=>Int)*) {
  private var max: Option[Int] = Some(4)
  def apply(i: Int) = {
    val segment = segments.takeWhile {case (b,_) => b <= i}.last
    val score = segment._2(i)
    max match {
      case None => score
      case Some(m) => score min m 
    }
  }
  def noMax = {
    max = None
    this
  }
}

class ShiftedHalfScore(shift: Int) extends ScoringFunction(
  (0,_ => -1),
  (1, _ => 1),
  (2, n => (n+shift)/2)
)

class IdentityScore(minusOne: Boolean) extends ScoringFunction(
  (0,_ => if (minusOne) -1 else 0),
  (1, n => n)
)

case class Score(
    family: (Int,Int),
    rooms: (Resource, Int, Int),
    animals: List[(Animal,Int,Int)],
    produce: List[(Produce,Int,Int)],
    otherCategories: List[(String,Int,Int)],
    empty: (Int,Int),
    improvements: List[(Improvement,Int,Int)],
    bonus: Int
) {
  def categories = animals ::: produce ::: otherCategories
  def total = family._2 + rooms._3 +
    categories.map(_._3).sum +
    empty._2 + improvements.map(i => i._2+i._3).sum + bonus
  override def toString = {
    val parts =
      ("Family", family._1, family._2) ::
      (rooms._1 + " rooms", rooms._2, rooms._3) ::
      categories.map({case (c,n,s) => (c.toString,n,s)}) :::
      List(("Empty spaces", empty._1, empty._2)) :::
      improvements.map({case (i,p,b) =>
        val bS = if (b > 0) ", bonus " + b else ""
        (" " + i.toString, s"$p$bS", p+b)
      })
    val partsS = parts.map({case (l,n,p) => s"$l: $n => $p"}).mkString("\n")
    val bonusS = "other bonus points: " + bonus
    val totalS = "total points: " + total
    partsS + "\n" + bonusS + "\n---------\n" + totalS
  }
}

object Score {
  val sheep = new ShiftedHalfScore(0)
  val boar = new ShiftedHalfScore(1)
  val cattle = new ShiftedHalfScore(2)
  val horse = new IdentityScore(true).noMax
  val grain = new ShiftedHalfScore(0)
  val vegetable = new IdentityScore(true)
  val pasture = new IdentityScore(true)
  val stable = new IdentityScore(false)
  val field = new ScoringFunction((0,_ => -1), (2, n=>n-1))
  
  def hutFactor(r: Resource) = r match {
    case Wood => 0
    case Clay => 1
    case Stone => 2
    case Reed => -1 // impossible
  } 
  
  /** the score of a player */
  def apply(p: Player, gameType: GameType): Score = {
    val animals = (Animal.allowed(gameType) zip List(sheep, boar, cattle, horse)) map {
      case (a,f) => (a, p(a), f(p(a)))
    }
    val produce = (Produce.all map {
      prod => (prod, p.produce(prod), p.produceOnField(prod))
    } zip List(grain, vegetable)) map {
      case ((p,pStock,pField), f) => (p, pStock+pField, f(pStock+pField))
    }
    val pas = p.animalKeeping.numPastures
    val fss = p.animalKeeping.numFencedStables
    val otherCategories = List(
      ("Pastures", pas, pasture(pas)),
      ("Fenced Stables", fss, stable(fss)),
      ("Fields", p.field.length, field(p.field.length))
    )
    
    val family = (p.member, p.member*3)
    val rooms = (p.hut, p.room, p.room*hutFactor(p.hut))
    val empty = (p.emptySpaces, -p.emptySpaces)
    val imps = p.improvements.sortBy(_.toString).map(i => (i, i.points, i.bonusScore(p)))
    val bonus = p.bonus
    Score(family, rooms, animals, produce, otherCategories, empty, imps, bonus)
  }
}