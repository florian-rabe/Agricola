package rabe.florian.agricola

import scala.collection.mutable.ListMap

class MapWithDefault[A,B](default: B) extends ListMap[A,B] {
  override def apply(a: A) = super.getOrElseUpdate(a, default)
}

/**
 * the state of the game board
 * 
 * currently only supports single player family version game
 * 
 * This class implements only those rudimentary aspects of the game logic
 * that are evident from looking at the board, e.g., it throws an exception
 * if an action is taken that is not available.
 * It does not implement any other aspects such as removing tokens from a cumulative action space.
 */
class Board(gameType: GameType) {
   /** the action spaces available in every round */
   val fixedActionSpaces: List[ActionSpace] =
     List(TakeWood,TakeClay,TakeReed,TakeGrain,
          BuildSpace,BakeAndStableSpace,Plow,StartingPlayer,Fishing,DayLaborerSpace)
   /** the roundActionSpaces opened so far, in round order */
   var roundActionSpaces: List[RoundActionSpace] = Nil
   /**
    * the special action cards used so far, in round order (ignored in non-Moor games)
    */
   var specialActionCards: List[SpecialActionCard] = Nil
   
   /** all action spaces available in the current round */
   def allSpaces = fixedActionSpaces:::roundActionSpaces
   
   /** the spaces used in the current round so far */
   var usedSpaces: List[ActionSpace] = Nil
   /** the number of times the current special action card has been used in the current round so far */
   var sacUsed = 0
   
   /** the number of token on the accumulating action spaces */
   val accumulated = new MapWithDefault[CumulativeSpace,Int](0)
   
   /** the improvements that have been built so far */
   var builtImprovements : List[Improvement] = Nil
   
   /** the number of the current round (1-14) */
   def round = roundActionSpaces.length
   /** the number of the current stage (1-6) */
   def stage = List(0,1,1,1,1,2,2,2,3,3,4,4,5,5,6)(round)
   
   /** starts a new round with a given action space and card, includes restocking
    *  @param s the round case of this round
    *  @param sa the special action card of this round, should be non-null only in non-Moor games
    */
   def newRound(s: RoundActionSpace, sa: SpecialActionCard) {
     if (roundActionSpaces contains s)
       throw Error(s + " already on board")
     roundActionSpaces = roundActionSpaces ::: List(s)
     if (s.stage != stage)
       throw Error(s + " not allowed in stage " + stage)
     if (gameType.moor) {
       val nextSAC = if (round <= 10) {
         if (specialActionCards contains sa)
            throw Error(sa + " already used")
          sa
       } else
         specialActionCards.last
       specialActionCards = specialActionCards ::: List(nextSAC)
     }
     sacUsed = 0
     usedSpaces = Nil
     allSpaces.foreach {
       case s: CumulativeSpace =>
         accumulated(s) += s.restock
         if (!gameType.family) {
           accumulated(StartingPlayer) = 0
         }
       case _ =>
     }
   }
   
   /** uses an action space for an action */
   def useSpace(s: ActionSpace) {
     if (!(allSpaces contains s))
       throw Error(s + " not on board")
     if (usedSpaces contains s)
       throw Error(s + " already used in this round")
     usedSpaces ::= s
   }
   /** uses a special action card for an action */
   def useSpecialAction(sa: SpecialAction) {
     if (! (specialActionCards.last.spaces contains sa.space))
       throw Error(sa + " not on special action card")
     if (sacUsed >= 2)
       throw Error("already two special actions used")
     sacUsed += 1
   }
   /** builds an improvement */
   def build(imp: Improvement) {
     if (builtImprovements contains imp)
       throw Error("already built " + imp)
     imp match {
         case imp: ConcealedImp =>
           if (!(builtImprovements contains imp.under))
               throw Error("not available " + imp)
         case _ => 
     }
     imp match {
       case imp: MoorImprovement =>
         if (! gameType.moor) throw Error(s"improvement $imp not available in $gameType game")
       case _ =>
     }
     builtImprovements ::= imp
   }
   /** builds an improvement */
   def unbuild(imp: Improvement) {
     if (! (builtImprovements contains imp))
       throw Error("not built yet " + imp)
     builtImprovements = builtImprovements.filterNot(_ == imp)
   }
}