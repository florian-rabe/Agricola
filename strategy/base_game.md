## Game Type

### Order of Round Cards

The order of Round Cards is the only element of luck left in the game.
Therefore, I prefer allowing the solo player to choose the order of Round Cards.
I call this *best-case-order*.

The dual scenario of *worst-case-order*, where we must find a strategy that works for any order of Round Cards, is considerably more complex but not necessarily more fun to think about.

Note that to find a worst-case strategy, it is not enough to find a stategy that uses every Round Card only in or after the last Round of its stage.
That would not even be very difficult as typical strong strategies already do that anyway except for usually taking the 2 Family Growth actions earlier than the worst case allows.
However, for the accumulating Round Cards, the worst-case strategy must consider that late appearances imply fewer Stones/animals collected when the action is used later on.
This is particularly critical for
* Sheep, where appearance in Round 1 vs. Round 4 means a difference of 6 Food in the midgame.
* Stone, where early vs. late appearance means a difference of up to 3 Stones over the whole game, which is already a bottleneck for maximizing points.


### Occupations and Minor Improvements

My analysis covers only the game types without these cards.
With them, the search space for optimal strategies huge.

Playing the Standard (i.e., non-Family) game without cards is still interesting as it can provide a base line for games with cards.
For example, under best-case-order, it is possible to score 67 points and still have enough actions and Food to play 2 Occupations.

### Family Version

The family has two critical advantages over the standard version:
* The Day Laborer allows obtaining additional resources (typically Wood and Stone). Moreover, it allows obtaining them earlier than usual, e.g., to build an early Oven.
* The Bake and Built 1 Stable for 1 Wood action allows saving 4 Wood during the course of a game.
There family version games can usually score a few more points.

The additional advantage of Food accumulating on the Starting Player is usually irrelevant as it is usually inefficient to take that food.

## Food Engine

It is usually best to let animals accumulate (which allows building Fences late).
Sheep are taken early once for Food. Then each animal is taken once in Round 13 or 14.
This allows cooking about 7 Sheep, 1 Boar, 1 Cattle (depending on when the cards occur and how much space there is for animals).

Most Food is obtained by sowing and baking Grain.
Typically, at least one Oven is built to maximize baking efficiency (and to get free baking actions).
Despite using Grain as a Food engine, it is usually easy to get all 12 points for Fields, Grain, and Vegetable.

Fishing is usually taken once in Stage 1.
Later uses of Fishing are usually inefficient.

## Resource Bottleneck

Contrary to the multi-player game, the limiting factor of the solo game is the number of resources that can be obtained (as opposed to the number of actions or available Food).
This is because there is no competition for accumulating squares so that the optimial strategy takes resources as rarely and as late as possible.
This saves actions.

When maximizing points, we often have actions left but don't have enough
- Wood to get all 20 points for animals, Stables, and Pastures.
- Stone to build more extensions, which is the only way to get more points when the farmyard points are maxed out.

## Wood Use for Animal Keeping

Animal keeping involves the 5 scoring categories of Pastures, Stables, and animals.
This allows for 20 points.
The following analyses how may of these points can be obtained with a given number of Wood.

We assume that any free squares are filled otherwise, i.e., the number of used squares is not considered in the number of points.
Pastures are given in the format size/capacity.

In the standard game, we have at most 28 Wood, from which we want to build 2 Rooms and the Well. That leaves only 17 Wood for animal keeping.
Building only 1 Wood Room leaves 22 Wood.
Not building the Well leaves 18 resp. 23 Wood, but it is very hard to make up the 4 points of the Well elsewhere.
In the family version, the situation is better because the Day Laborer can be used to generate a few additional Wood.

 17 Wood:
 - 13 Fences + 2 Stables: 3 Pastures 1/4, 1/4, 4/8, accommodates 8S, 5B, 4C ==> 15 points, 6 squares
 - 15 Fences + 1 Stable:  4 Pastures 2/8, 2/4, 1/2, 1/2, accommodates 8S, 5B, 4C ==> 15 points, 6 squares
 - 11 Fences + 3 Stables: 3 Pastures 2/8, 1/4, 1/4, accommodates 8S, 5B, 4C ==> 16 points, 4 squares
(Building at least 2 Stables is often helpful because it allows for breeding 1 animal before building fences.)

 18 Wood:
 - 14 Fences + 2 Stables: 3 Pastures 2/8, 2/8, 2/4, accommodates 8S, 7B, 4C ==> 16 points, 6 squares
 - 14 Fences + 2 Stables: 4 Pastures 2/8, 1/4, 1/2, 1/2, accommodates 8S, 5B, 4C ==> 16 points, 5 squares
 - 12 Fences + 3 Stables: 4 Pastures 1/4, 1/4, 1/4, 1/2, accommodates 8S, 5B, 2C ==> 16 points, 4 squares
 
 19 Wood:
 - 13 Fences + 3 Stables: 3 Pastures 2/8, 2/8, 1/4, accommodates 8S, 7B, 4C ==> 17 points, 5 squares

 20 Wood:
 - 12 Fences + 4 Stables: 4 Pastures 1/4, 1/4, 1/4, 1/4, accommodates 8S, 5B, 4C ==> 18 points, 4 squares
 - 14 Fences + 3 Stables: 4 Pastures 2/8, 1/4, 1/4, 1/2, accommodates 8S, 7B, 4C ==> 18 points, 5 squares

 21 Wood:
 - 13 Fences + 4 Stables: 3 Pastures 2/16, 2/8, 1/4, accommodates 8S, 7B, 4C ==> 18 points, 5 squares

 22 Wood:
 - 14 Fences + 4 Stables: 4 Pastures 2/8, 1/4, 1/4, 1/4, accommodates 8S, 7B, 4C ==> 19 points, 5 squares
 - 15 Fences + 3 Stables (1 Wood left): 4 Pastures 2/8, 2/8, 1/4, 1/2, accommodates 8S, 7B, 6C ===> 19 points, 6 squares
 
 23 Wood:
 - 15 Fences + 4 Stables: 4 Pastures 2/8, 2/4, 1/4, 1/4, accommodates 8S, 7B, 6C ==> 20 points, 6 squares
 
Notes on number of squares used for pastures:

 Pastures covering 4 squares hold at most animals for 10 out of 12 points.
 Pastures covering 5 squares hold at most animals for 11 out of 12 points.
 Pastures covering 7 squares or more can make at most 3 Pastures holding at most animals for 11 (7 squares) or 10 (8 squares) out of 12 points.
 12 Fences covering 5 squares can make at most 2 Pastures.
 13 Fences covering 5 squares can make at most 3 Pastures.

## My Best Games

My games can be found in the games package of the checker.
Running the cheker produces detailed analyses, which is available in the results directory.

### Standard Version

I believe 67 to be the best possible score or at least very close to it.

The main difficulty is the lack of Wood (see above for analysis).
This leads to 3 different Room building strategies, which surprisingly yield essentially the same number of points in my best games:
* 2 early Wood Rooms: 67 points
* 1 early Wood and 1 late Clay Room: 67 points
* 1 early Wood Room: 66 points

Of these the first strategy seems best as it leaves the most slack.
In my best game, I actually have 2 unused actions because I run out of resources to do anything with.
By building a Clay Room, I was able to gain points for animal keeping (19 instead of 16) but lose them again because I lack Clay for the ClayOven and an only-for-points Fireplace.

### Family Version

TBD