package nlp

import opennlp.tools.tokenize.{TokenizerModel, TokenizerME}
import opennlp.tools.postag.POSModel
import io.Source
import collection.mutable
import opennlp.tools.parser.Parse
import opennlp.tools.sentdetect.{SentenceModel, SentenceDetectorME}

object Categorizer {
  val classLoader = getClass.getClassLoader
  val tokenizeModel = new TokenizerModel(classLoader.getResourceAsStream("en-token.bin"))
  val tokenizer = new TokenizerME(tokenizeModel)
  val taggingModel = new POSModel(classLoader.getResourceAsStream("en-pos-maxent.bin"))
  val impWords = Source.fromInputStream(classLoader.getResourceAsStream("imp_words_uniq.txt")).getLines
  val negWords = Source.fromInputStream(classLoader.getResourceAsStream("negation_words.txt")).getLines.toSet
  val ampWords = Source.fromInputStream(classLoader.getResourceAsStream("amplifier_words.txt")).getLines
  val sentenceDetector = new SentenceDetectorME(new SentenceModel(classLoader.getResourceAsStream("en-sent.bin")))
  //val model = new ParserModel(classLoader.getResourceAsStream("en-parser-chunking.bin"))
  //  val parser = ParserFactory.create(model)

  val scoreDict: mutable.Map[String, (Double, Double)] = impWords.foldLeft(scala.collection.mutable.Map[String, (Double, Double)]())((map, str) => {
    val parts = str.split("\\s+")
    map += (parts(2) ->(parts(0).toDouble, parts(1).toDouble))
  })

  val categories = Array("P", "U", "N")

  def collectPhrases(root: Parse): Seq[Parse] = {
    val list = new collection.mutable.MutableList[Parse]
    if (root.getType == "NP" || root.getType == "VP")
      list += root
    else for (child <- root.getChildren) {
      list ++= collectPhrases(child)
    }

    list
  }


  protected def categorize(tokens: Array[String]): Int = {
    var posScore, negScore = 0d
    var negFlag: Boolean = false
    for (token <- tokens) {
      if (negWords.contains(token)) {
        negFlag = true
      }
      var (pos, neg) = scoreDict.getOrElse(token, (0d, 0d))
      posScore += pos
      negScore += neg
    }
    //println(posScore, negScore, negFlag)
    val scor = score((posScore - negScore), (posScore + negScore), 0.2)
    negFlag match {
      case true if (scor == 0) => -1
      case true => -scor
      case _ => scor

    }
  }


  def categorizePhrase(phrase: Parse): Int = {
    phrase.show
    categorize(phrase.getTagNodes.map((parse: Parse) => parse.toString))
  }

  def categorizeSentence(sentence: String): Int = {
    var _sentence = sentence.replaceAll("n\'t", "nt").replaceAll("-", " ").replaceAll("!", " ").replaceAll("\\?", " ").replaceAll("\\.", " ").replaceAll(",", " ").toLowerCase.trim
    if (_sentence.isEmpty) {
      return 0
    }
    categorize(tokenizer.tokenize(_sentence))
  }


  def score(counter: Double, len: Double, threshold: Double = 0.3): Int = {
    if (len == 0) {
      return 0
    }

    counter / len match {
      case x if (x > threshold) => 1
      case x if (x < -threshold) => -1
      case _ => 0
    }
  }

  def categorize(document: String): String = {
    var counter = 0
    val sentences = sentenceDetector.sentDetect(document)
    for (sentence <- sentences) {
      counter += categorizeSentence(sentence)
    }
    sentiment(counter, sentences.length)
  }


  def sentiment(counter: Int, len: Int): String = {
    counter * 1.0 / len match {
      case x if (x > 0.3) => "P"
      case x if (x < -0.3) => "N"
      case _ => "U"
    }
  }
}
