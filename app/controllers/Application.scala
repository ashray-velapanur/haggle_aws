package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}
import opennlp.tools.postag.{POSTaggerME, POSModel}

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
    val application = play.Play.application()
    val tzer = new TokenizerME(new TokenizerModel(application.resourceAsStream("en-token.bin")))
    val tokens: Array[String] = tzer.tokenize(text)
    val posTagger = new POSTaggerME(new POSModel(application.resourceAsStream("en-pos-maxent.bin")))
    val tags = posTagger.tag(tokens)
    tokens.mkString(", ") + "<br />" + tags.mkString(", ")
  }

}
