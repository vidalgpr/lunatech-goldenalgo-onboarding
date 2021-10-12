package com.lunatech.goldenalgo.onboarding

import diode._

import japgolly.scalajs.react._
import japgolly.scalajs.react.feature.ReactFragment
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl
import diode.react.{ ModelProxy, ReactConnectProxy }
import org.scalajs.dom.ext.KeyCode

object Home {

  case class Props(
      proxy: ModelProxy[Option[Recipe]],
      ctl: RouterCtl[AppRouter.Page]
  )

  case class State(editRecipe: Recipe)

  class Backend($ : BackendScope[Props, State]) {
    def mounted(props: Props) = Callback {}

    def onSubmitButtonPressed(dispatch: Action => Callback)(currentRecipe: Recipe): Option[Callback] =
      if (currentRecipe.name.trim.nonEmpty && currentRecipe.ingredients.nonEmpty)
        Some(
          dispatch(SetRecipe(currentRecipe)) >>
          Callback.alert("Succefully received recipe!")
        )
      else None

    def editFieldChanged(field: RecipeField): ReactEventFromInput => Callback = e =>
      Callback(e.persist()) >> $.modState { s =>
        s.copy(editRecipe = field match {
          case RecipeName         => s.editRecipe.copy(name = e.target.value)
          case RecipeIngredients  => s.editRecipe.copy(ingredients = e.target.value.split("\n").toSeq)
          case RecipeInstructions => s.editRecipe.copy(instructions = e.target.value.split("\n").toSeq)
        })
      }

    def render(p: Props, s: State): VdomElement = {
      val proxy                        = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB

      <.div(
        ^.padding         := "250px",
        ^.textAlign       := "center",
        ^.backgroundColor := "#d5aca3",
        <.h2("My Awesome Recipe"),
        <.br,
        <.p(
          "Current Circuit Recipe = ",
          <.b(proxy.map(_.toString))
        ),
        <.p(
          "Current State Recipe = ",
          <.b(s.editRecipe.toString)
        ),
        <.input.text(
          ^.onChange ==> editFieldChanged(RecipeName),
          ^.placeholder := "Pizza a la leña",
          ^.autoFocus   := true,
          ^.size        := 50
        ),
        <.br,
        <.textarea(
          ^.onChange ==> editFieldChanged(RecipeIngredients),
          ^.placeholder := "Harina\nTomates\n..."
        ),
        <.br,
        <.textarea(
          ^.onChange ==> editFieldChanged(RecipeInstructions),
          ^.placeholder := "1)...\n2)...\n3)..."
        ),
        <.br,
        <.button(
          "SUBMIT",
          ^.onClick -->? onSubmitButtonPressed(dispatch)(s.editRecipe)
        ),
        <.br,
        <.button(
          "GET RECIPE ID 00",
          ^.onClick --> dispatch(LoadRecipe("00"))
        )
      )
    }
  }

  def component =
    ScalaComponent
      .builder[Props]("Home")
      .initialStateFromProps(props => State(Recipe("", "", Nil, Nil)))
      .renderBackend[Backend]
      .build

  def apply(ctl: RouterCtl[AppRouter.Page]): VdomElement = AppCircuit.recipeProxy(p => component(Props(p, ctl)))

}
