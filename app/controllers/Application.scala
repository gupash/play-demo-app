package controllers

import javax.inject.Singleton

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc.{Action, Controller}

@Singleton
class Application extends Controller {

  val userForm = Form(
    tuple(
      "name" -> text.verifying(nonEmpty),
      "age" -> number.verifying(min(18), max(30))
    )
  )

  def index = Action {
    Ok(views.html.index())
  }

  def submit = Action {
    implicit request =>
      val (name, age) = userForm.bindFromRequest.get
      Ok(views.html.display(s"Hi $name $age !"))
  }
}
