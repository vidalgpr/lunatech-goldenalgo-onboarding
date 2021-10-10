package com.lunatech.goldenalgo.onboarding

import diode._
import diode.react.ReactConnector
import diode.ActionResult.ModelUpdate

/** AppCircuit provides the actual instance of the `AppModel` and all the action handlers we need. Everything else comes from the
  * `Circuit`
  */
object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(Recipe("0", "_initialized", Nil, Nil))

  val counterHandler = new ActionHandler(zoomTo(_.recipe)) {
    override def handle = {
      case InitRecipe                          => updated(value)
      case ResetRecipe                         => updated(Recipe("", "", Nil, Nil))
      case SetRecipeId(id)                     => updated(value.copy(id = id))
      case SetRecipeName(name)                 => updated(value.copy(name = name))
      case SetRecipeIngredients(ingredients)   => updated(value.copy(ingredients = ingredients))
      case SetRecipeInstructions(instructions) => updated(value.copy(instructions = instructions))
      case SetRecipe(r) =>
        updated(
          value.copy(
            id = r.id,
            name = r.name,
            ingredients = r.ingredients,
            instructions = r.instructions
          )
        )
    }
  }
  val actionHandler: HandlerFunction = composeHandlers(counterHandler)

}
