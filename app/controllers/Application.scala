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
    mapping(
      "name" -> text.verifying(nonEmpty),
      "age" -> number.verifying(min(18), max(30))
    ) (User.apply) (User.unapply)
  )

  def index = Action {
    Ok(views.html.index(userForm))
  }

  def submit = Action {
    implicit request =>
      val currentUser = userForm.bindFromRequest.get
      Ok(views.html.display(s"Hi ${currentUser.name} ${currentUser.age} !"))
  }
}
