package com.lunatech.goldenalgo.onboarding

import diode._
import diode.react.ReactConnector
import diode.ActionResult.ModelUpdate
import diode.react.ReactConnectProxy

/** AppCircuit provides the actual instance of the `AppModel` and all the action handlers we need. Everything else comes from the
  * `Circuit`
  */
object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(None)

  val counterHandler = new ActionHandler(zoomTo(_.recipe)) {
    override def handle = {
      case InitRecipe   => updated(Some(Recipe("0", "_init", Nil, Nil)))
      case ResetRecipe  => updated(Some(Recipe("-1", "_reset", Nil, Nil)))
      case SetRecipe(r) => updated(value.map(_ => r.copy(id = RecipeId.random)))
      case LoadRecipe(id) => effectOnly(BackendService.fetchRecipeByIdEffect(id))
      // case UpdateRecipeName(id, name)                 => updated(value.map(_.copy(name = name)))
      // case UpdateRecipeIngredients(id, ingredients)   => updated(value.map(_.copy(ingredients = ingredients)))
      // case UpdateRecipeInstructions(id, instructions) => updated(value.map(_.copy(instructions = instructions)))
    }
  }
  val actionHandler: HandlerFunction = composeHandlers(counterHandler)

  val recipeProxy: ReactConnectProxy[Option[Recipe]] = AppCircuit.connect(_.recipe)
} 
