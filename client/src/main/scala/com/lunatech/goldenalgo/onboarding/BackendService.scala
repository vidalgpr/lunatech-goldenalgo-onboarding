package com.lunatech.goldenalgo.onboarding

import org.scalajs.dom.ext.Ajax
import io.circe.parser._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Failure, Success }
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.CallbackTo
import diode.{ Dispatcher, Effect, ModelRO }
import scala.concurrent.Future
import diode.EffectSingle

// counter is our stateful RootModel
object BackendService {

  def fetchRecipes(): Unit = Ajax.get("/recipes").onComplete {
    case Success(xhr) => decode[List[Recipe]](xhr.responseText)
    case Failure(t)   => println("An error has occurred: " + t.getMessage)
  }

  def fetchRecipeByIdEffect(id: String) = Effect(Ajax.get(s"http://127.0.0.1:8080/api/search_recipes/all").map { xhr =>
    decode[Seq[Recipe]](xhr.responseText).fold(
      err => ResetRecipe,
      _ match {
        case Nil    => ResetRecipe
        case r :: c => SetRecipe(r)
      }
    )
  })

  // def postRecipe(recipe: Recipe): Unit = Effect(Ajax.post(s"/api/upload_recipe?id=$user").map(r => NewMessages(r.responseText)))

}
