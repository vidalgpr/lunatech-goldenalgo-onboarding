package com.lunatech.goldenalgo.onboarding

import org.scalajs.dom.ext.Ajax
import io.circe.parser._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.CallbackTo
import diode.{Dispatcher, ModelRO}

// counter is our stateful RootModel
class Backend(recipe: ModelRO[AppModel], dispatch: Dispatcher) {
  
  def fetchRecipes(): Unit = Ajax.get("/recipes").onComplete {
    case Success(xhr) => decode[List[Recipe]](xhr.responseText)
    case Failure(t) => println("An error has occurred: " + t.getMessage)
  }

  def searchRecipeById(id: String): Unit = Ajax.get(s"api/search_recipes/?id=$id").onComplete {
    case Success(xhr) => decode[Recipe](xhr.responseText)
    case Failure(t)   => println("An error has occurred: " + t.getMessage)
  }

  def resetRecipe(): Unit = dispatch(ResetRecipe)

  def updateRecipeName(): Unit = Ajax.get("api/search_recipes/all").onComplete {
    case Success(xhr) =>
      decode[Seq[Recipe]](xhr.responseText).fold(
        ex => println("Unable to decode input: " + ex.getMessage),
        _ => dispatch(UpdateRecipeName("UPDATED"))
      )
    case Failure(t) => println("An error has occurred: " + t.getMessage)
  }

  def getRecipe: Recipe = recipe.value.recipe
}
