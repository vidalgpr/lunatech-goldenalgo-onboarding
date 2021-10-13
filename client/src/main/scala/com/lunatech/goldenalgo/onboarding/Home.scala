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

  case class State(editRecipe: Recipe, editId: String, receivedRecipes: Seq[Recipe])

  class Backend($ : BackendScope[Props, State]) {
    def mounted(props: Props) = Callback {}

    def onSubmitButtonPressed(dispatch: Action => Callback)(currentRecipe: Recipe): Option[Callback] =
      if (currentRecipe.name.trim.nonEmpty && currentRecipe.ingredients.nonEmpty)
        Some(
          dispatch(SetNewRecipe(currentRecipe)) >>
          dispatch(PostRecipe) >>
          Callback.alert("Succefully received recipe!")
        )
      else None

    def onSearchByIdButtonPressed(dispatch: Action => Callback)(currentId: String): Option[Callback] =
      if (currentId.trim.nonEmpty)
        Some(
          dispatch(LoadRecipe(currentId)) >>
          Callback.info("Showing found recipes!")
        )
      else None
  
    // def onShowAllRecipesButtonPressed(dispatch: Action => Callback): Callback = 
    //   $.modState(_.copy(receivedRecipes = BackendService.fetchRecipes())

    def handleNewIdKeyDown(dispatch: Action => Callback): ReactKeyboardEventFromInput => Option[Callback] = e => {
      val id = e.target.value.trim
      if (e.nativeEvent.keyCode == KeyCode.Enter && id.nonEmpty) onSearchByIdButtonPressed(dispatch)(id)
      else None
    }

    val editIdFieldChanged: ReactEventFromInput => Callback = e =>
      Callback(e.persist()) >> $.modState(s => s.copy(editId = e.target.value))

    def editFieldChanged(field: RecipeField): ReactEventFromInput => Callback = e =>
      Callback(e.persist()) >> $.modState { s =>
        s.copy(editRecipe = field match {
          case RecipeName         => s.editRecipe.copy(name = e.target.value)
          case RecipeIngredients  => s.editRecipe.copy(ingredients = e.target.value.split("\n").toSeq)
          case RecipeInstructions => s.editRecipe.copy(instructions = e.target.value.split("\n").toSeq)
        })
      }

    def recipesList(recipes: Seq[Recipe]) =
      <.section(
        ^.textAlign := "left",
        <.ul(
          recipes.toTagMod { recipe =>
            <.li(
              <.div(
                <.label(
                  recipe.toString
                )
              )
            )
          }
        )
      )

    def render(p: Props, s: State): VdomElement = {
      val proxy                        = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB

      <.div(
        <.h1("My Awesome Recipe"),
        ^.textAlign := "center",
        <.header(
          <.h5("Search by ID:"),
          <.input.text(
            ^.onChange ==> editIdFieldChanged,
            ^.placeholder := "my-recipe-id",
            ^.autoFocus   := true,
            ^.size        := 33,
            ^.onKeyDown ==>? handleNewIdKeyDown(dispatch)
          ),
          <.button(
            "GET RECIPE",
            ^.onClick -->? onSearchByIdButtonPressed(dispatch)(s.editId)
          )
        ),
        <.div(
          ^.padding         := "32px",
          ^.textAlign       := "center",
          ^.backgroundColor := "#cfd5a9",
          <.h3("Edit and Upload your recipe here"),
          <.input.text(
            ^.onChange ==> editFieldChanged(RecipeName),
            ^.placeholder := "Pizza a la leña",
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
          )
        ),
        <.div(
          ^.padding         := "32px",
          ^.textAlign       := "center",
          ^.backgroundColor := "#c2d25e",
          <.br,
          <.h3("Retrieve recipes here:"),
          <.br,
          <.button(
            "SHOW ALL",
            // ^.onClick -->? onShowAllRecipesButtonPressed(dispatch)(s.editId)
          ),
          <.br,
          recipesList(s.receivedRecipes)
        ),
        <.footer(
          <.h6("[DEBUG] State and Circuit Model"),
          <.p("Current ID State = ", <.b(s.editId)),
          <.p(
            "Current Recipe State =",
            <.b(s.editRecipe.toString)
          ),
          <.p(
            "Current Circuit Recipe",
            <.b(proxy.map(_.toString))
          ),
          ^.fontSize     := "11px",
          ^.alignContent := "left",
          ^.paddingTop := "20px"
        )
      )

    }
  }

  def component =
    ScalaComponent
      .builder[Props]("Home")
      .initialStateFromProps(props =>
        State(
          Recipe("", "", Nil, Nil),
          "-1",
          Seq(Recipe("0", "sa", Nil, Nil), Recipe("1", "ds", Nil, Nil))
        )
      )
      .renderBackend[Backend]
      .build

  def apply(ctl: RouterCtl[AppRouter.Page]): VdomElement = AppCircuit.recipeProxy(p => component(Props(p, ctl)))

}
