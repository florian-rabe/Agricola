package rabe.florian.agricola

/** resources, animals, etc. */
abstract class Token {
  def allowedIn(g: GameType) = true
  def toStringShort = toString()(0)
}

abstract class TokenGroupObject[+T <: Token](s: String) {
  override def toString = s
  def all: List[T]
  def allowed(g: GameType) = all.filter(_.allowedIn(g))
} 

object Token {
  def all = List(Resource,Produce,Animal,Currency)
}

/** building resources */
sealed abstract class Resource(s: String) extends Token {
  override def toString = s
}
case object Wood extends Resource("Wood")
case object Clay extends Resource("Clay")
case object Reed extends Resource("Reed")
case object Stone extends Resource("Stone")

object Resource extends TokenGroupObject[Resource]("Resource") {
  def all = List(Wood,Clay,Reed,Stone)
}

/** animals and produce */
sealed trait Cookable extends Token

/** animals */
sealed abstract class Animal(s: String) extends Token with Cookable {
  override def toString = s
}
case object Sheep extends Animal("Sheep")
case object Boar extends Animal("Boar")
case object Cattle extends Animal("Cattle")
case object Horse extends Animal("Horse") {
  override def allowedIn(g: GameType) = g.moor
}

object Animal extends TokenGroupObject[Animal]("Animal") {
  def all = List(Sheep,Boar,Cattle,Horse)
}

/** grain and vegetable */
sealed abstract class Produce(s: String) extends Token with Cookable  { 
  override def toString = s
}
case object Grain extends Produce("Grain")
case object Vegetable extends Produce("Vegetable")

object Produce extends TokenGroupObject[Produce]("Produce") {
  def all = List(Grain,Vegetable)
}

/** food and fuel */
sealed abstract class Currency(s: String) extends Token { 
  override def toString = s
}
case object Food extends Currency("Food")
case object Fuel extends Currency("Fuel") {
  override def allowedIn(g: GameType) = g.moor
}

object Currency extends TokenGroupObject[Currency]("Currency") {
  def all = List(Food,Fuel)
}
