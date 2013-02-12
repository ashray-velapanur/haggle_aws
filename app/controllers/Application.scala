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

  val application = play.Play.application()
  val lines: Set[String] = Source.fromInputStream(application.resourceAsStream("senti_wn_top_words.txt")).getLines.toSet
  val tzer = new TokenizerME(new TokenizerModel(application.resourceAsStream("en-token.bin")))
  val featureGenerator = new FeatureGenerator {
    def extractFeatures(tokens: Array[String]): util.Collection[String] = {
      val list = new util.ArrayList[String]()
      for(token <- tokens){
         if(lines.contains(token)){
            list.add(token)
         }
      }
      list
    }
  }
  val doccatModel = DocumentCategorizerME.train("en",
    new DocumentSampleStream(
      new PlainTextByLineStream(application.resourceAsStream("yelp_model_sentiment"), "UTF-8")
    ), 0, 100, featureGenerator)
  val categorizer = new DocumentCategorizerME(doccatModel, featureGenerator)

  def analyse(text:String):String={
    val outcomes = categorizer.categorize(tzer.tokenize(text))
    categorizer.getBestCategory(outcomes)
  }

}
