package rabe.florian.agricola

class Field {
  private var produce: Option[Produce] = None
  private var amount: Int = 0
  override def toString = produce match {
    case None => "[  ]"
    case Some(p) => "[" + amount + p.toString()(0) + "]"
  }
  def getProduce = produce
  def getAmount = amount
  def empty = produce.isEmpty
  def sow(p: Produce) {
    if (! empty)
      throw Error("field not empty")
    produce = Some(p)
    amount = p match {
      case Grain => 3
      case Vegetable => 2
    }
  }
  def growing(p: Produce) = if (produce == Some(p)) amount else 0
  def harvest: Option[Produce] = produce map {p =>
    amount -= 1
    if (amount == 0)
      produce = None
    p
  }
}

class Player(gameType: GameType) {
   val tokens = new MapWithDefault[Token,Int](0)
   def apply(t: Token) = tokens(t)
   def currency(c: Currency) = tokens(c)
   def resource(r: Resource) = tokens(r)
   def produce(p: Produce) = tokens(p)
   def produceOnField(p: Produce) = field.map(_.growing(p)).sum
   def animal(a: Animal) = tokens(a)

   var bonus: Int = 0
   
   var member: Int = 2
   var room: Int = 2
   var hut: Resource = Wood
   
   val animalKeeping = new AnimalKeeping

   var field: List[Field] = Nil
   
   var improvements: List[Improvement] = Nil
   
   var forest = if (gameType.moor) 5 else 0
   var moor = if (gameType.moor) 3 else 0
   
   def emptySpaces = 15 - room - field.length - animalKeeping.numUsedSpaces - forest - moor
}

/** 3x5 farmyard, only intended for visualization,
 *  should be computed within Player, but currently not used
 */ 
class Farmyard {
   private val squares = new Array[Array[FarmyardSquare]](5)
   for (i <- List(0,1,2))
     squares(i) = new Array[FarmyardSquare](3)
   def update(sq: Square, fs: FarmyardSquare) {
      squares(sq.column)(sq.row) = fs
   }
   var pet: Option[Animal] = None
}

abstract class FarmyardSquare
case class Room(resource: Resource) extends FarmyardSquare
case class FieldSquare(content: Field) extends FarmyardSquare
case class AnimalSquare(left: Boolean, up: Boolean, right: Boolean, down: Boolean,
           stable: Boolean, animal: Animal, count: Int) extends FarmyardSquare
case object Moor extends FarmyardSquare
case object Forest extends FarmyardSquare
case object Empty extends FarmyardSquare