package pkg

/**
  * Created by johnreed on 5/23/16.
  */
object Main {

  import scala.language.existentials
  import scala.language.experimental.macros

  def main(args: Array[String]) {

    val string1 = {
      val left = "string"
      val right = "1"
      left + right
    } // string1 = "string1"

    acceptString1(string1)
    val string2 = new Function0[String] {
      // no parenthesis "apply" method is getter - no side effects
      override def apply: String = {
        // Note: using "return" is bad style, unnecessary boilerplate.
        return "string2" // you can (and should) leave out "return"
      }
    }
    acceptString2(string2)
    acceptString3 {
      // curly braces are for multi-line, parenthesis only compile for single-line
      println("Hey we are getting string 3")
      "string3"
    }

    import scala.trace.Pos
    // position

    def add(a: Int, b: Int): Int = {
      a + b
    }
    val stringArray1 = new Array[String](_length = 2)
    val stringArray2 = Array.apply(1, 2, 3) // Object apply methods used for constructors by convention
    val stringArray3 = Array("1", "2", "3") // equivalent to Array.apply("1","2","3")

    type IntT = Int // type alias
    val three: IntT = 1 + 2 // equivalent to "val three: Int = 1 + 2"

    // existential types are like wildcards. They are typesafe, but the type is <?> (unknown)
    def acceptExistential1(seq: Seq[Whatever] forSome {type Whatever}) = {
      scala.trace.SDebug.traceContentsStdOut(seq)
    }
    def acceptExistential2(seq: Seq[_]) = {
      scala.trace.SDebug.traceContentsStdOut(seq)
    } // this is the same as acceptExistential1

    def acceptInts(seq: Seq[Int]) = {}
    val sequenceOfWhateverType: Seq[_] = List(1, 2, 3) // casting it to type Seq[Whatever] loses type info.

    acceptExistential1(sequenceOfWhateverType)
    acceptExistential2(sequenceOfWhateverType)
    acceptInts(List(1, 2, 3)) // this is legal
    val sequenceOfWhateverType2: Seq[_] = List("1", "2", "3")
    // acceptInts( sequenceOfWhateverType2 ) // will not compile - not type safe
    // acceptInts( sequenceOfWhateverType ) // this is illegal - Whatever might not be an int

    val crazyName_!@<>\=*&^%~~~~~~-=: = 5 // In Scala, names can be symbols.
    // "_" separates chars from symbols
    println(crazyName_!@<>\=*&^%~~~~~~-=: + Pos())

    trait Motion { // If I make a mistake please comment below - corrections in description.
      type MyType <: Motion
      private[this] val privateWithThis = 1 // can only be accessed within this instance. Most restrictive.
      protected[this] val protectedWithThis = 2 // can be accessed with "this" keyword from subclass
      private[Motion] val privateInMotion = 3 // can be accessed within any instance of type Motion
      protected[Motion] val protectedInMotion = 4 // accessible from within any subclass of Motion
      val publicVal = "public" // public access is default, no restrictions

      var numMotions = 0

      def move(): Unit = {
        println("Moving")
        numMotions += 1
      }

      def returnMe: MyType

      def returnMotion: Motion
    }

    abstract class Animal(val isMulticellular: Boolean = true) {
      private[pkg] val packagePrivate = 5 // can only be accessed in package pkg.
      protected[pkg] val tuple = (1, 2, 3) // can be accessed by my subclasses outside of package "pkg".
    }

              // This is the main constructor. All other functions that want to construct must call it.
    class Person1(val name: String, val age: Int) extends Animal with Motion {
      // everything in this space is executed at construction time.
      println(packagePrivate) // legal because we are in package "pkg"
      println(publicVal) // legal because public
      // println(this.privateWithThis) // illegal because privateWithThis can only be accessed in parent
      // Note: you can leave out "this" and compiler will insert it for you
      println(this.protectedWithThis) // legal because we are accessing with "this" from protected parent

      def printOther(other: Person1) = {
        println(this.protectedWithThis) // legal see above
        // println(other.protectedWithThis) // illegal, can only be accessed with "this" keyword
        // println(other.privateInMotion) // illegal, can only be accessed inside trait "Motion"
        println(other.protectedInMotion) // legal, can be accessed by any subclass of trait "Motion"
      }
      // this is a type alias
      type MyClassType = Person1 // this class type is a subclass of "Motion"

      // this type is specific to this instance. It is like a unique value.
      override type MyType = this.type // this instance type is a subclass of "Motion" and "Person1"

      override def returnMotion: Person1 = this // Returns something of type "Person1"

      override def returnMe: Person1.this.type = this // Returns something of type "this"
    }

    class Person2(val name: String, val age: Int) extends Animal with Motion {
      override type MyType = Person2 // this is another type alias

      override def returnMe: MyType = this

      override def returnMotion: Motion = this
    }

    case class caseBacterial(name: String) extends Animal(false)

    println("Hello World 1")
    println("Hello World 2" + Pos())

    implicit val f: Int = 5 // This implicit val can be "grabbed" and "sucked into" an implicit parameter.

    def implicitFunc(implicit f: Int) = {
      println(f)
    }

    implicitFunc // "Ctr + Shift + P" to locate implicit parameters
    // this works too:
    val implicitInt = implicitly[Int] // locate the implicit parameter of type "Int"

    // Another example of "implicitly"

    implicit val myImplicitFloat: Float = 7.4.toFloat

    val implicitFloat: Float = implicitly[Float] // find an implicit of type "Float"

    println(implicitFloat + Pos()) // prints 7.4

    final class IntWrapper(val i: Int) {
      def sayHi(): Unit = { println("Hi") }
    }

    implicit def wrapIntInWrapper(i: Int) = {new IntWrapper(i)}

    val six = 6

    six.sayHi() // Navigate > Implicit Conversion (Ctr + Shift + Q)
    wrapIntInWrapper(six).sayHi() // Make implicit into explicit with Alt + Enter

    def onlyTuples[Type, TupleL, TupleR](tuple: Type)(implicit evidence: Type <:< (TupleL, TupleR)) = {}

    onlyTuples((1, 1)) // compiles because (1,1) is a tuple
    // onlyTuples( 1 ) // Does not compile. 1 is not a tuple

    val inclusiveRange: Range.Inclusive = 1 to 10

    val exclusiveRange = 1 until 10

    for (i <- 1 to 10) { /* Do nothing */ }

    for (i <- inclusiveRange) {
      println(i + Pos())
    }

    for (i <- inclusiveRange) {
      for (i <- exclusiveRange) {  }
    }

    // PLEASE PLEASE PLEASE AVOID WHILE LOOPS IN PRODUCTION CODE. This isn't C.
    var loopCounter = 0
    while(loopCounter < 10) {
      // Execute some code in loop
      loopCounter += 1
    }

    val matrixCharacters = List("neo", "smith", "trinity")

    //converts each character of the string to its corresponding code
    val toInts: String => List[Int] = str => {
      val numbers = str.map(char => char.toInt)
      numbers.toList
    }

    val listOfLists: List[List[Int]] = matrixCharacters map toInts
    val flatList: List[Int] = matrixCharacters flatMap toInts

    val flatList2: List[Int] = for {
      character <- matrixCharacters
      int <- toInts(character)
    } yield int // concatenate all the ints from all the characters

    val flatList3: List[Int] = matrixCharacters.flatMap { character =>
      toInts(character)
    }

    val flatList4: List[Int] = matrixCharacters flatMap toInts

    val outerList: List[Int] = List(1, 2, 3)

    val productList1: List[Int] =
      outerList.flatMap(element => {
        List(1, 2).map(inner =>
          inner * element
        )
      }
    )
    scala.trace.SDebug.traceContentsStdOut(productList1)

    // This parameter takes var-args, any number of String arguments ex. (s1: String, s2: String, etc.)
    def printAll(strings: String*): Unit = {
      val stringList = strings
      strings.foreach { e => println(e) }
    }
    printAll("a", "b", "c", "d", "e")

    val innerList = List(1, 2, 3)
    val productList2: List[Int] = for {
      outer <- List(1,2)
      inner <- innerList
      if (inner < 3)
    } yield (outer + inner)

    scala.trace.SDebug.traceContentsStdOut(productList2)

    val helloWorld = { // this is a scope that returns the last thing in the scope
      val hello = "hello"
      val world = "world"
      hello + world
    }

    def returnNothing(): Unit = {
      type UnitT = Unit
      def totallyUnnecessary(): UnitT = {}
      val someThing = "something"
      () // this is the same as "Unit"
    }
    val nothing = returnNothing

    val pairs = List(("1", 1), ("2", 2))
    pairs.foreach { pair =>
      val (strVal, intVal) = pair // this breaks a pair into two values, strVal and intVal
      println(strVal + " " + intVal + Pos())
    }

    import scala.annotation.tailrec // import the tail recursive optimization checker annotation
    @tailrec def tailRecursiveCount(i: Int): Int = {
      if (i == 0) {
        println("Done");
        0
      }
      else tailRecursiveCount(i - 1) // this is optimized into a loop
    }

    // this lazy value gets cached into a variable when it is first accessed.
    lazy val myLazy = {
      scala.trace.Debug.traceStdOut("Lets get lazy", 3)
      tailRecursiveCount(2)
    }

    println(myLazy + Pos()) // lazy code is executed at site of first accessed and then cached for reuse.

    println(myLazy + Pos()) // By this point the value is cached, no re-execution of "Debug.traceStdOut"

    def return5: Int = {
      5
    }

    val vv: () => Int = return5 _ // this converts a function into a value of type "() => Int"

    val vv2: Int = return5 // this evaluated the function, returning a value of type "Int"

    def addNoCurrying(a: Int, b: Int) = {
      a + b
    }

    val addNoCurryingToVal: (Int, Int) => Int = addNoCurrying _

    def curryingAdditionFunction(a: Int)(b: Int) = {
      a + b
    }

    val curryingAdditionValue: (Int) => (Int) => Int // Haskell style (Haskell and Scala are related)
      = (addNoCurrying _).curried

    val addFive: (Int) => Int = curryingAdditionValue(5)

    val ten = addFive(5)

    println(ten + Pos())

    val evilNull: Null = null // null is a type

    type topOfTypeHierarchy = Any // everything extends any

    type bottomOfTypeHierarchy = Nothing // everything is a supertype of nothing

    // Use "???" for "Not yet implemented" - will throw an exception if called.
    def unimplemented: Nothing = ???

    val langs: List[(String, String)] = List(
      ("C++", "Stroustrup"), // C++ adds object-oriented-programming to C
      ("Scala", "Odersky") // Scala adds functional-programming to Java
    )

    for (tuple <- langs) {
      tuple match {
        case ("Scala", _) => println("Scala" + Pos())
        case (lang, author) => println(lang + " " + author + Pos())
      }
    }

    import scala.trace.SDebug

    val boilerplateyPartialFunction = new PartialFunction[Any, String] {

      override def isDefinedAt(x: Any): Boolean = {
        if (x.isInstanceOf[String] || x.isInstanceOf[Double]) {
          true
        }
        else false
      }

      override def apply(v1: Any): String = {
        val toReturn: String = v1 match {
          case s: String => "String"
          case d: Double => "Double"
        }
        toReturn
      }
    }

    try {
      println(boilerplateyPartialFunction.isDefinedAt(5) + Pos())
      println(boilerplateyPartialFunction.apply(5) + Pos())
    } catch {
      case _: MatchError => println("Is not defined" + Pos())
    }

    val partialFunction: PartialFunction[Any, String] = {
      case _ => "Neither String nor Double"
    }

    val wholeFunction: PartialFunction[Any, String]
      = boilerplateyPartialFunction orElse partialFunction

    import scala.trace.implicitlyPrintable

    val foo = "foo"
    (foo + Pos()).println()


    sealed trait MyTrait {
      type T <: MyTrait

      def returnMe: T
    }

    class MyClass extends MyTrait {
      override type T = MyClass

      override def returnMe: T = {
        this
      }
    }

    type T[_] = List[_] // this is a type constructor. T[Int] is a concrete type.
    type T2 = List[_]

    val existentialTypeList: T2 = List(1, 2, 3)
    val listOfInt: T[Int] = List(1, 2, 3, 4)
    println()

    type IntList = T[Int]
    type IntList2 = T[Int]

    object mySingleton {
      type myType = mySingleton.type
    }
    object anotherSingleton {}

    type singletonType = mySingleton.type

    def takeInSingleton(s: mySingleton.type): s.myType = {
      s
    }
    takeInSingleton(mySingleton)
    // takeInSingleton(anotherSingleton) // does not compile

    class Outer {

      // type: Outer.this.Inner
      val inner: Outer.this.Inner = new Outer.this.Inner()

      class Inner {}
    }
    val outer1 = new Outer

    // outer2 is instance of anonymous class that extends "Outer"
    val outer2 = new Outer {
      override val inner = new Inner()
    }
  }

  def acceptString1(s: String): Unit = {
    val result = s // this copies a reference to an immutable String
    println(s)
  }

  def acceptString2(s: Function0[String]): Unit = {
    val result = s // this executes a function call that returns a String
    println(result)
  }

  def acceptString3(s: => String): Unit = {
    val result = s // this executes a function call that returns a String
    println(result)
  }

  object makeFive {
    def apply: Int = macro makeFiveMacro

    def makeFiveMacro(c: scala.reflect.macros.blackbox.Context): c.Tree = {
      import c.universe._
      val five = 5
      q"""$five""" // this just adds 5 into the source code at compile time
    }
  }

  println("I am going to use a macro to print: " + makeFive)
}
