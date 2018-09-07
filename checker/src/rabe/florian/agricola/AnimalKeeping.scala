package rabe.florian.agricola

object Auxil {
  def isIn(a: Int, min: Int, max: Int) = min <= a && a <= max
  def subset[A](l: List[A], m: List[A]) = l.forall(x => m contains x)
  def upto(n:Int) = Range(0,n).toList
  def power(x: Int, y: Int) = BigInt(x).pow(y).toInt
  def repeatString(s: String, n: Int, sep: String = "") = Range(0,n).map(_ => s).mkString(sep)
}
import Auxil._

/**
 * coordinates in the 5x3 grid of farmyard squares
 * 
 * @param column 0,...,4
 * @param row 0,...,2
 * 
 * (0,2) -- (4,2)
 *   |         |
 * (0,0) -- (4,0)
 */
case class Square(column: Int, row: Int) {
  def isLegal = isIn(row, 0, 4) && isIn(column, 0, 2)
  def isNextTo(that: Square) = {
    val rowDist = (that.row-this.row).abs
    val colDist = (that.column-this.column).abs
    rowDist+colDist == 1 
  }
  def leftEdge = Edge(column, row, true)
  def rightEdge = Edge(column+1, row, true)
  def bottomEdge = Edge(column, row, false)
  def topEdge = Edge(column, row+1, false)
  def edges = List(leftEdge, rightEdge, bottomEdge, topEdge)
}

/**
 * the 6x4 edges of the 5x3 farmyard squares
 * 
 * @param column lower/left corner 
 * @param row lower/left corner
 * @param vertical true/false for vertical/horizontal edges
 */
case class Edge(column: Int, row: Int, vertical: Boolean) {
  def isLegal = isIn(column, 0, 5) && isIn(row, 0, 3)
  /** the square left/below this edge */
  def leftOrBelowSquare = {
    val (c,r) = if (vertical) (1,0) else (0,1)
    Square(column-c, row-r)
  }
  /** the square right/above this edge: Square(column,row) */
  def rightOrAboveSquare = Square(column, row)
}

object Square {
  /** true if all squares are connected */
  def connected(sqs: Seq[Square]) = {
     var checked: List[Square] = Nil
     var progress = true
     while (progress) {
        progress = false
        (sqs diff checked).foreach {s =>
           if (checked.isEmpty || checked.exists(_.isNextTo(s))) {
             checked ::= s
             progress = true
           }
        }
     }
     checked.length == sqs.length
  }
  def distinct(sqs: Seq[Square]) = sqs.distinct.length == sqs.length
}

/** a single pasture */
case class Pasture(squares: Square*) {
   def isLegal: Boolean = {
     squares.forall(_.isLegal) &&
     Square.distinct(squares) &&
     Square.connected(squares)
   }
   /** number of squares */
   def size = squares.length
   /** the set of outer edges, i.e., the needed fences */
   lazy val fences = {
     val all = squares.flatMap(_.edges).distinct
     // filter out the inner edges that are not fenced (i.e., both neighbors in the pasture)
     all.filterNot {e =>
        val s1 = e.rightOrAboveSquare
        val s2 = e.leftOrBelowSquare
        (squares contains s1) && (squares contains s2)
     }
   }
}

/** the locations of the built pastures */
case class Pastures(pastures: Pasture*) {
   def fences = pastures.toList.flatMap(_.fences).distinct
   def numFences = fences.length
   def isLegal = {
      val sqs = pastures.toList.flatMap(_.squares)
      pastures.forall(_.isLegal) &&
      Square.distinct(sqs) && Square.connected(sqs) &&
      numFences <= 15
   }
}
object Pastures {
  implicit def pasturesToList(ps: Pastures) = ps.pastures.toList
}

/** the locations of the built stables */
case class Stables(squares: Square*) {
   def isLegal = squares.length <= 4 && squares.forall(_.isLegal)
   def +(sq: Square) = Stables(sq::squares.toList:_*)
}
object Stables {
  implicit def stablesToList(st: Stables) = st.squares.toList
}

/**
 * the stateful data structure maintaining the locations of pastures and stables
 */
class AnimalKeeping {
  private var stables = Stables()
  private var pastures = Pastures()
  
  def getPastures = pastures
  def getStables = stables.toList

  def isLegal = {
     stables.isLegal && pastures.isLegal
  }
  
  def numStablesOf(p: Pasture) = stables.count(sq => p.squares contains sq)

  def numFences = pastures.numFences
  def numStables = stables.squares.length
  def numPastures = pastures.length
  def numFencedStables = pastures.map(numStablesOf(_)).sum
  def numUnfencedStables = numStables - numFencedStables
  def numUsedSpaces = pastures.map(_.size).sum + numUnfencedStables

  def squares = (pastures.flatMap(_.squares) ::: getStables).distinct 
  
  def capacities: List[CapacityProvided] = {
     val pastureCaps = pastures.zipWithIndex map {case (p,i) =>
       val stab = numStablesOf(p)
       val size = p.size
       InPasture(i, size, stab)
     }
     val stableCaps = upto(numUnfencedStables).map(i => InStable(i))
     val homeCaps = Pet
     Pet :: pastureCaps ::: stableCaps
  }
  
  /** build a stable */
  def addStable(square: Square) {
    if (! square.isLegal)
      throw Error("illegal space for stable")
    if (stables contains square)
      throw Error("already a stable on that space")
    if (numStables >= 4)
      throw Error("not enough stables")
    stables = stables + square
    // should be guaranteed by above checks
    assert(isLegal)
  }
  
  /**
   * add fences to obtain a new pasture layout 
   * 
   * @param ps all pastures after fencing
   */
  def addFences(ps: Pastures) {
     if (!subset(pastures.fences,ps.fences))
       throw Error("fences may not be removed")
     if (ps.numFences > 15)
       throw Error("not enough fences")
     pastures = ps
     // should be guaranteed by above checks
     assert(isLegal)
  }
}

/** input for animal allocation */
abstract class CapacityProvided(s: String) {
  /* defaults to 1 */ 
  def capacity: Int = 1
  /** allocation and remaining need (if any) after using this */
  def put(cn: CapacityNeeded) = {
     val left = cn.amount - capacity
     val stillNeeded = if (left > 0) Some(cn.copy(amount = left)) else None
     (CapacityAllocated(cn.copy(amount = capacity min cn.amount), this), stillNeeded)
  }
  override def toString = s
}
case object Pet extends CapacityProvided("H")
case class InPasture(index: Int, size: Int, stables: Int) extends CapacityProvided({
   val stablesS = repeatString("X", stables)
   val restS = repeatString("_", size-stables)
   "[" + stablesS + restS + "]"}) {
  override def capacity = 2 * size * power(2,stables)
}
case class InStable(index: Int) extends CapacityProvided("X")

/** input for animal allocation */
case class CapacityNeeded(amount: Int, animal: Animal) {
  override def toString = if (amount != 0) s":$amount${animal.toStringShort}" else ""
}

/** output for animal allocation */
case class CapacityAllocated(needed: CapacityNeeded, provider: CapacityProvided) {
  override def toString = s"$provider$needed"
}

object AnimalKeeping {
  /** output for animal allocation */
  /**
   * checks if animals can be sorted into pastures/stables/home
   */
  def accommodate(prov: List[CapacityProvided], need: List[CapacityNeeded]): Option[List[CapacityAllocated]] = {
    val provS = prov.sortBy(_.capacity).reverse
    val needS = need.filter(_.amount != 0).sortBy(_.amount).reverse
    val aO = accommodateAux(provS, needS, Nil)
    aO.foreach {as =>
      // double-check allocation respects capacities
      assert(as.forall(a => a.needed.amount <= a.provider.capacity))
      // double-check all animals allocated
      val allocated = as.groupBy(ca => ca.needed.animal).map {case (a,ps) => (a,ps.map(_.needed.amount).sum)}
      //println(allocated)
      //println(need)
      assert(need.forall(n => allocated.find(_._1 == n.animal).map(_._2).getOrElse(0) == n.amount))
    }
    aO
  }

  private def accommodateAux(prov: List[CapacityProvided], need: List[CapacityNeeded], result: List[CapacityAllocated]): Option[List[CapacityAllocated]] = {
     if (need.isEmpty) {
       val rest = prov.map(_.put(CapacityNeeded(0,null))._1)
       return Some(result.reverse ::: rest)
     }
     if (prov.isEmpty) return None
     // 2 special cases where greedy allocation works
     if (need.head.amount == prov.head.capacity)
       return accommodateAux(prov.tail, need.tail, prov.head.put(need.head)._1::result)
     if (need.last.amount == prov.last.capacity)
       return accommodateAux(prov.init, need.init, prov.last.put(need.last)._1::result)
     // brute-force try of all combinations
     need.foreach {case cn =>
       val cp = prov.head
       val (ca,remOpt) = cp.put(cn)
       val needLeft = (need.filterNot(_ == cn) ::: remOpt.toList).sortBy(_.amount).reverse
       val res = accommodateAux(prov.tail, needLeft, ca::result)
       if (res.isDefined) return res
     }
     None
  }
}