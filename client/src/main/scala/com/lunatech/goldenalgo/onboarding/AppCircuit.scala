package com.lunatech.goldenalgo.onboarding

import diode._
import diode.react.ReactConnector
import diode.ActionResult.ModelUpdate
import diode.react.ReactConnectProxy

/** AppCircuit provides the actual instance of the `AppModel` and all the action handlers we need. Everything else comes from the
  * `Circuit`
  */
object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(Nil)

  val counterHandler = new ActionHandler(zoomTo(_.recipes)) {
    override def handle = {
      case InitRecipe             => updated(Recipe.empty :: Nil)
      case ResetRecipe            => updated(Recipe.empty :: Nil)
      case SetSingleRecipe(r)     => updated(r :: Nil)
      case SetMultipleRecipes(rs) => updated(rs)
      case SetNewRecipe(r)        => updated(r.copy(id = RecipeId.random) :: Nil)
      case LoadSingleRecipe(id)   => effectOnly(BackendService.fetchRecipeByIdEffect(id))
      case PostRecipe             => effectOnly(BackendService.postRecipe(value.headOption))
      case UpdateRecipe           => effectOnly(BackendService.updateRecipe(value.headOption))
      case DeleteRecipe           => effectOnly(BackendService.deleteRecipe(value.headOption))
      case LoadAllRecipes         => effectOnly(BackendService.fetchAllRecipes())
    }
  }
  val actionHandler: HandlerFunction = composeHandlers(counterHandler)

  val recipesProxy: ReactConnectProxy[Seq[Recipe]] = AppCircuit.connect(_.recipes)
}
