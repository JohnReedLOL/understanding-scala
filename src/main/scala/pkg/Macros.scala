package pkg

/**
  * Created by johnreed on 5/28/16.
  */
object Macros {
  import scala.language.existentials
  import scala.language.experimental.macros

  object makeFive {
    def apply(): Int = macro makeFiveMacro

    def makeFiveMacro(c: scala.reflect.macros.blackbox.Context)(): c.Tree = {
      import c.universe._
      val five = 5
      q"""$five""" // this just adds 5 into the source code at compile time
    }
  }
}
