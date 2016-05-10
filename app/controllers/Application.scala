package controllers

import javax.inject.Singleton

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

@Singleton
class Application extends Controller {

  val userForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "age" -> number(18, 30)
    ) (User.apply) (User.unapply)
  )

  def index = Action {
    Ok(views.html.index(userForm))
  }

  def submit = Action {
    implicit request =>
      val formRequest = userForm.bindFromRequest
      formRequest.fold(
        requestWithError => BadRequest(views.html.index(requestWithError)),
        userData => Ok(views.html.display(s"Hi ${userData.name} ${userData.age} !"))
      )
  }


}
