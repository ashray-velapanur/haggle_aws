package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}

object Application extends Controller {

  val helloForm = Form(
      "text" -> nonEmptyText
  )

  def index = Action {
    Ok(views.html.index(helloForm))
  }

  def sentiment = Action { implicit request =>
    helloForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      {case text => Ok(analyse(text))}
    )
  }


  def analyse(text:String):String={
    val tzer = new TokenizerME(new TokenizerModel(play.Play.application().resourceAsStream("en-token.bin")))
    tzer.tokenize(text).mkString(", ")
  }

}
