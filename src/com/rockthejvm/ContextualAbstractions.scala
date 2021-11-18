package com.rockthejvm

object ContextualAbstractions {

  /*
  *  1 - context parameters/arguments
  */
  val aList = List(2, 1, 3, 4)
  val anOrderedList = aList.sorted //Ordining argument automatically injected by compiler (contextual argument)

  // Ordering - a comparison object, says which element is less than another
  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _) //lambda notation: (a, b) => a > b
  //given keyword signals compiler to use this rather than the default Ordering
  //similar to an implicit val

  trait Combinator[A] { //monoid
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
    list.reduce(combinator.combine)
  //list.reduce((a,b) => combinator.combine(a,b))

  given intCombinator: Combinator[Int] = new Combinator[Int] {
    override def combine(x: Int, y: Int) = x + y
  }

  val theSum = combineAll(aList) //implicitly uses intCombinator now

  //import yourpackage.given - this is how you can import instances
  /*
  * Given places - where compiler looks for given instances?
  * -local scope
  * -imported scope
  * -companions of all the types involved in the call
      -companion of List
      -companion of Int
  * */

  //context bounds
  def combineAll_v2[A](list: List[A])(using Combinator[A]): A = ??? //these 2 definitions are the same, different notations

  def combineAll_v3[A: Combinator](list: List[A]): A = ???

  /*
  * where context args are useful
  * -type classes
  * -dependency injection
  * -context-dependent functionality
  * -type-level programming
  */

  /*
      2 - extension methods
  */

  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name"
  }

  //extensions allow us to enrich existing classes to suit our immediate needs on the fly
  extension (string: String) { //extension on the string type, essentially giving the string type this function, greet()
    def greet(): String = new Person(string).greet()
  }

  val danielsGreeting = "Daniel".greet()
  //compiler searches for all possible extensions on the string type
  //this is called type enrichment, or hinting in scala 2

  //POWER
  extension [A] (list: List[A]){
    def combineAllValues(using combinator: Combinator[A]): A =
      list.reduce(combinator.combine)
  }
  val theSum_v2 = aList.combineAllValues


  def main(args: Array[String]): Unit = {
    println(anOrderedList)
    println(theSum)
    println(theSum_v2)
  }

}
