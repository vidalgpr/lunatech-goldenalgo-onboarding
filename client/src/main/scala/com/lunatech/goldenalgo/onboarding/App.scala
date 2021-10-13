package com.lunatech.goldenalgo.onboarding

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom._
import japgolly.scalajs.react.vdom.html_<^._

object App {

  @JSExport
  def main(args: Array[String]): Unit = {

    (1 to 3).foreach(println)
    println("Starting...")

    println("Init")
    AppCircuit.dispatch(InitRecipe)

    val app = dom.document.getElementById("app")
    AppRouter.router().renderIntoDOM(app)
  }
}


// PICKLE UNPICKEL STUFF