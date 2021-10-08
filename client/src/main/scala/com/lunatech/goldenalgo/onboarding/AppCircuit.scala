package com.lunatech.goldenalgo.onboarding

import diode._
import diode.react.ReactConnector
import diode.ActionResult.ModelUpdate

/** AppCircuit provides the actual instance of the `AppModel` and all the action handlers we need. Everything else comes from the
  * `Circuit`
  */
object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(Recipe("", "", Nil, Nil))

  val counterHandler = new ActionHandler(zoomTo(_.recipe)) {
    override def handle = {
      case InitRecipe          => updated(value)
      case UpdateRecipeName(a) => updated(value.copy(name = a))
      case ResetRecipe         => updated(Recipe("0", "RESET", Nil, Nil))
    }
  }
  val actionHandler: HandlerFunction = composeHandlers(counterHandler)

}
