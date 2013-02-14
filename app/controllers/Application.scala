package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}
import opennlp.tools.postag.{POSTaggerME, POSModel}
import opennlp.tools.doccat.{FeatureGenerator, DocumentSampleStream, DocumentCategorizerME}
import opennlp.tools.util.PlainTextByLineStream
import java.util
import com.google.common.io.Files
import io.Source
import opennlp.tools.parser.{ParserFactory, Parser, ParserModel}
import java.io.IOException
import opennlp.tools.cmdline.parser.ParserTool
import nlp.Categorizer

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

  val categorizer = new Categorizer()
  def analyse(text:String):String={
    val outcomes = categorizer.categorize(text)
    categorizer.getBestCategory(outcomes)
    //test(text)
  }
  val application = play.Play.application()
  val model = new ParserModel(application.resourceAsStream("en-parser-chunking.bin"));
  val parser = ParserFactory.create(model);

  def parse_input(sentence:String):String={
    val topParses = ParserTool.parseLine(sentence, parser, 1);
    val sb = new StringBuffer()
    topParses(0).show(sb)
    return sb.toString
  }
}
