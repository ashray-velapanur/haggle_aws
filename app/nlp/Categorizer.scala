package nlp

import opennlp.tools.doccat.DocumentCategorizer
import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}
import opennlp.tools.postag.{POSTaggerME, POSModel}
import util.matching.Regex
import io.Source
import collection.mutable

object Categorizer{
  val application = play.Play.application()
  val tokenizeModel = new TokenizerModel(application.resourceAsStream("en-token.bin"))
  val taggingModel = new POSModel(application.resourceAsStream("en-pos-maxent.bin"))
  val impWords = Source.fromInputStream(application.resourceAsStream("imp_words_uniq.txt")).getLines()
  val scoreDict: mutable.Map[String, (Double, Double)] = impWords.foldLeft(scala.collection.mutable.Map[String,(Double,Double)]())((map,str)=> {
    val parts = str.split("\\s+")
    map += (parts(2) -> (parts(0).toDouble,parts(1).toDouble))
  })
  val categories = Array("P","U","N")
}

class Categorizer extends DocumentCategorizer{
  var posScore, negScore=0d
  protected def categorize(tokens: Array[String]): Array[Double] = {
    for (token<-tokens){
      var (pos,neg) = Categorizer.scoreDict.getOrElse(token,(0d,0d))
      posScore += pos
      negScore += neg
    }
    Array(posScore,0,negScore)
    //val posTagger = new POSTaggerME(taggingModel)
    //val tags = posTagger.tag(tokens)
    //tags.mkString(" ")
    //Array(0.0)
  }

  def categories=Categorizer.categories

  def getBestCategory(outcome: Array[Double]): String = {
    var max_idx = 0
    for((e,i) <- outcome.zipWithIndex){
      if(e>outcome(max_idx)){
        max_idx=i
      }
    }
    categories(max_idx)
  }

  def getIndex(category: String): Int = categories.indexOf(category)

  def getCategory(index: Int): String = categories(index)

  def getNumberOfCategories: Int = categories.length

  def categorize(document: String): Array[Double] = {
    categorize(new TokenizerME(Categorizer.tokenizeModel).tokenize(document.toLowerCase))
  }

  def getAllResults(outcome: Array[Double]): String = {
    outcome.zipWithIndex.map((tuple:(Double,Int)) => categories(tuple._2)+ "[" + tuple._1 + "]").mkString(" ")
  }
}
