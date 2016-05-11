package controllers

import javax.inject.{Inject, Singleton}

import models.User
import play.api.cache.CacheApi
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller, Result}

@Singleton
class Application @Inject() (cache: CacheApi) extends Controller {

  val userForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "pass" -> nonEmptyText
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
        userData => {
          AuthenticateMe(userData) {
            authUser => {

              val SID = java.util.UUID.randomUUID().toString
              cache.set(SID, authUser)

              Ok(views.html.display(s"Hi ${authUser.name}!")).withSession("SID" -> SID)
            }
          }
        }
      )
  }

  def loggedInUser = Action {
    implicit request => {
      val currentSessionID = request.session.get("SID").getOrElse("No Value")

      cache.get[User](currentSessionID) match {
        case Some(User(name, _)) => Ok(views.html.show(name))
        case None => BadRequest("No Logged In User")
      }

    }
  }

  def AuthenticateMe(currentUser: User)(f: User => Result) = {
    val user:Option[User] = if(currentUser.name == "Ashish" && currentUser.pass == "password#1") Some(currentUser) else None
    user match {
      case Some(user) => f(user)
      case None => Forbidden("I don't know you")
    }
  }

}
