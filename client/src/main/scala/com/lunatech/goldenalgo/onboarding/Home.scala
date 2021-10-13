package com.lunatech.goldenalgo.onboarding

import diode._

import japgolly.scalajs.react._
import japgolly.scalajs.react.feature.ReactFragment
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.extra.router.RouterCtl
import diode.react.{ ModelProxy, ReactConnectProxy }
import org.scalajs.dom.ext.KeyCode
import scala.sys.Prop

object Home {

  case class Props(
      proxy: ModelProxy[Seq[Recipe]],
      ctl: RouterCtl[AppRouter.Page]
  )

  case class State(recipeHandle: Recipe, editId: String)

  class Backend($ : BackendScope[Props, State]) {
    def mounted(props: Props) = Callback {}

    def onUploadButtonPressed(dispatch: Action => Callback)(s: State): Option[Callback] =
      if (s.recipeHandle.name.trim.nonEmpty && s.recipeHandle.ingredients.nonEmpty)
        Some(
          dispatch(SetNewRecipe(s.recipeHandle)) >>
          dispatch(PostRecipe) >>
          Callback.alert("Succefully received recipe!")
        )
      else None

    def onUpdateButtonPressed(dispatch: Action => Callback)(s: State): Option[Callback] =
      if (s.recipeHandle.name.trim.nonEmpty && s.recipeHandle.ingredients.nonEmpty)
        Some(
          dispatch(SetSingleRecipe(s.recipeHandle)) >>
          dispatch(PostRecipe) >>
          Callback.alert("Succefully received recipe!")
        )
      else None

    def onDeleteButtonPressed(dispatch: Action => Callback)(s: State): Option[Callback] =
      if (s.recipeHandle.id.trim.nonEmpty)
        Some(
          dispatch(SetSingleRecipe(s.recipeHandle)) >>
          dispatch(DeleteRecipe) >>
          Callback.alert("Recipe succefully deleted!")
        )
      else None

    def onBlurEditFields(dispatch: Action => Callback)(s: State): Option[Callback] =
      if (s.recipeHandle.name.trim.nonEmpty && s.recipeHandle.ingredients.nonEmpty)
        Some(dispatch(SetSingleRecipe(s.recipeHandle)))
      else None

    def onSearchByIdButtonPressed(dispatch: Action => Callback)(id: String): Option[Callback] =
      if (id.trim.nonEmpty)
        Some(dispatch(LoadSingleRecipe(id)) >> Callback.info("Showing found recipes!"))
      else None

    def onShowAllRecipesButtonPressed(dispatch: Action => Callback) = dispatch(LoadAllRecipes)

    def handleNewIdKeyDown(dispatch: Action => Callback): ReactKeyboardEventFromInput => Option[Callback] = e => {
      val id = e.target.value.trim
      if (e.nativeEvent.keyCode == KeyCode.Enter && id.nonEmpty) onSearchByIdButtonPressed(dispatch)(id)
      else None
    }

    val editIdFieldChanged: ReactEventFromInput => Callback = e =>
      Callback(e.persist()) >> $.modState(s => s.copy(editId = e.target.value))

    def editFieldChanged(field: RecipeField): ReactEventFromInput => Callback = e =>
      Callback(e.persist()) >> $.modState { s =>
        s.copy(recipeHandle = field match {
          case RecipeName         => s.recipeHandle.copy(name = e.target.value)
          case RecipeIngredients  => s.recipeHandle.copy(ingredients = e.target.value.split("\n").toSeq)
          case RecipeInstructions => s.recipeHandle.copy(instructions = e.target.value.split("\n").toSeq)
        })
      }

    def statePanel(proxy: Seq[Recipe], s: State) = <.footer(
      <.h6("[DEBUG] State and Circuit Model"),
      <.p("Current ID State = ", <.b(s.editId)),
      <.p(
        "Current Recipe State = ",
        <.b(s.recipeHandle.toString)
      ),
      <.p(
        "Current Circuit Recipe",
        <.br,
        <.b(proxy.mkString(" || "))
      ),
      ^.fontSize     := "11px",
      ^.alignContent := "left",
      ^.paddingTop   := "20px"
    )

    def getRecipePanel(dispatch: Action => Callback, s: State) = <.header(
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
    )

    def editableRecipe(p: Props, dispatch: Action => Callback, s: State) =
      <.div(
        ^.padding         := "32px",
        ^.textAlign       := "center",
        ^.backgroundColor := "#cfd5a9",
        <.h3("Recipe data"),
        <.b(s"ID = ${s.recipeHandle.id}"),
        <.br,
        <.input.text(
          ^.onChange ==> editFieldChanged(RecipeName),
          ^.onBlur -->? onBlurEditFields(dispatch)(s),
          ^.placeholder := "Pizza a la leña",
          ^.value       := s.recipeHandle.name,
          ^.size        := 40
        ),
        <.br,
        <.textarea(
          ^.onChange ==> editFieldChanged(RecipeIngredients),
          ^.onBlur -->? onBlurEditFields(dispatch)(s),
          ^.placeholder := "Harina\nTomates\n...",
          ^.value       := s.recipeHandle.ingredients.mkString(", ")
        ),
        <.br,
        <.textarea(
          ^.onChange ==> editFieldChanged(RecipeInstructions),
          ^.onBlur -->? onBlurEditFields(dispatch)(s),
          ^.placeholder := "1)...\n2)...\n3)...",
          ^.value       := s.recipeHandle.instructions.mkString(", ")
        ),
        <.br,
        <.button(
          "UPLOAD",
          ^.onClick -->? onUploadButtonPressed(dispatch)(s)
        ),
        <.button(
          "UPDATE",
          ^.onClick -->? onUpdateButtonPressed(dispatch)(s)
        ),
        <.br,
        <.button(
          "DELETE",
          ^.onClick -->? onDeleteButtonPressed(dispatch)(s)
        )
      )

    def recipesRetrieval(recipes: Seq[Recipe], dispatch: Action => Callback) =
      <.div(
        ^.padding         := "18px",
        ^.textAlign       := "center",
        ^.backgroundColor := "#c2d25e",
        <.button(
          "SHOW ALL RECIPES",
          ^.onClick --> onShowAllRecipesButtonPressed(dispatch)
        ),
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
      )

    def render(p: Props, s: State): VdomElement = {
      val proxy                        = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB

      <.div(
        <.h1("My Awesome Recipe"),
        ^.textAlign := "center",
        getRecipePanel(dispatch, s),
        editableRecipe(p, dispatch, s),
        recipesRetrieval(proxy, dispatch),
        statePanel(proxy, s)
      )

    }
  }

  def component =
    ScalaComponent
      .builder[Props]("Home")
      .initialStateFromProps(p => State(p.proxy().head, p.proxy().head.id))
      .renderBackend[Backend]
      .build

  def apply(ctl: RouterCtl[AppRouter.Page]): VdomElement =
    AppCircuit.recipesProxy(p => component(Props(p, ctl)))

}
