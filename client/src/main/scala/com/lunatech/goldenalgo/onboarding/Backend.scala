package com.lunatech.goldenalgo.onboarding

import org.scalajs.dom.ext.Ajax
import io.circe.parser._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.CallbackTo
import diode.{ Dispatcher, ModelRO }

// counter is our stateful RootModel
class Backend(rootModel: ModelRO[AppModel], dispatch: Dispatcher) {

  def fetchRecipes(): Unit = Ajax.get("/recipes").onComplete {
    case Success(xhr) => decode[List[Recipe]](xhr.responseText)
    case Failure(t)   => println("An error has occurred: " + t.getMessage)
  }

  def searchRecipeById(id: String): Unit = Ajax.get(s"api/search_recipes/?id=$id").onComplete {
    case Success(xhr) =>
      val recipe = decode[Recipe](xhr.responseText).fold(
        x => println("Unable to decode input: " + x.getMessage),
        r => dispatch(SetRecipe(r))
      )
    case Failure(t) => println("An error has occurred: " + t.getMessage)
  }

  def resetRecipe(): Unit = dispatch(ResetRecipe)
  
  def setRecipeName(name: String): Unit = dispatch(SetRecipeName(name))

  def setRecipeIngredients(ingredients: String): Unit = dispatch(SetRecipeIngredients(ingredients.split(',').toSeq))

  def setRecipeInstructions(instructions: String): Unit = dispatch(SetRecipeInstructions(instructions.split(',').toSeq))

  def setRecipeId(): Unit = dispatch(SetRecipeId())

  def postRecipe(recipe: Recipe): Unit = ???

  def recipe: Recipe = rootModel.value.recipe
}
