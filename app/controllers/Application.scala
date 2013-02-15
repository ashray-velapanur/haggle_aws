package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import opennlp.tools.parser.{ParserFactory, ParserModel}
import opennlp.tools.cmdline.parser.ParserTool
import nlp.Categorizer
import net.didion.jwnl.JWNL
import net.didion.jwnl.dictionary.Dictionary
import net.didion.jwnl.data.POS


object Application extends Controller {
  val application = play.Play.application()
  val model = new ParserModel(application.resourceAsStream("en-parser-chunking.bin"));
  val parser = ParserFactory.create(model);

  JWNL.initialize(application.resourceAsStream("file_properties.xml"))
  val dict = Dictionary.getInstance()

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

  def tree = Action { implicit request =>
    helloForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      {case text => {
        val topParses = ParserTool.parseLine(text, parser, 1)
        val sb = new StringBuffer()
        topParses(0).show(sb)
        Ok(sb.toString)}}
    )
  }


  def wordnet = Action { implicit request =>
    helloForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      {
        case text =>{
        var res:String=""
        val lookupIndexWord = dict.lookupIndexWord(POS.NOUN, text)
        for(sense<-lookupIndexWord.getSenses){

          for(word <- sense.getWords){
            res += ", " + word.getLemma
          }
        }
        Ok(res)
      }}
    )
  }

  val categorizer = new Categorizer()
  def analyse(text:String):String={

    val outcomes = categorizer.categorize(text)
    categorizer.getBestCategory(outcomes)

  }
    //val outcomes = categorizer.categorize(text)
    //categorizer.getBestCategory(outcomes)
    //test(text)
  //}
/*
>>>>>>> included jwnl

  def parse_input(sentence:String):String={
    val topParses = ParserTool.parseLine(sentence, parser, 1)
    val sb = new StringBuffer()
    topParses(0).show(sb)
    System.setProperty("wordnet.database.dir", "/usr/local/WordNet-3.0/dict/")
    val database = WordNetDatabase.getFileInstance()
    val synsets = database.getSynsets("fly", SynsetType.NOUN)
    var out_str = ""
    for (synset <- synsets) {
      val nounSynset = synset.asInstanceOf[NounSynset]
      val hyponyms = nounSynset.getHyponyms()
      out_str = out_str + (nounSynset.getWordForms()(0) +
        ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
    }
    return out_str
    return sb.toString
  }
*/
}
