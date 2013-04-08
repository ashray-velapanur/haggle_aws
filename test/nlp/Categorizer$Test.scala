package nlp

import org.specs2.mutable._
import scala.collection.mutable.Map
import opennlp.tools.cmdline.parser.ParserTool
import opennlp.tools.parser.{Parse, ParserFactory, ParserModel}
import opennlp.tools.sentdetect.{SentenceModel, SentenceDetectorME}
import anorm.SqlMappingError
import io.Source
import play.libs.Json
import org.codehaus.jackson.JsonNode
import java.util
import collection.JavaConversions

class Categorizer$Test extends Specification {
  def confustionMatrix = {
    Map("P" -> Map("P" -> 0, "U" -> 0, "N" -> 0), "U" -> Map("P" -> 0, "U" -> 0, "N" -> 0), "N" -> Map("P" -> 0, "U" -> 0, "N" -> 0))
  }

  val classLoader = getClass.getClassLoader
  "Categorizer" should {

/*
     "categorize a sentence" in{
    val categorizer = new Categorizer()
       println(categorizer.categorize("bussing staff is rude."))
     }
*/
    "categorize all yelp reviews " in {

       val categorizer = Categorizer
       val matrix = confustionMatrix

       var cnt=0
       val json: JsonNode = Json.parse(Source.fromInputStream(classLoader.getResourceAsStream("fs_tips.txt")).mkString)

       for (restaurant:JsonNode <- JavaConversions.asScalaIterator(json.getElements)){
           for(review:JsonNode <- JavaConversions.asScalaIterator(restaurant.getElements)){

             val  sentiment = review.get("sentiment").getTextValue
             val  text = review.get("text").getTextValue
             val row = matrix(sentiment)
             val outcome = categorizer.categorize(text)
             println(outcome, sentiment)
             row(outcome) += 1
           }
       }
       println("%4s |%7s |%7s |%7s".format("", "P", "U", "N"))
       println("-" * 35)
       for((key,map)<-matrix){
         println("%4s |%7d |%7d |%7d".format(key, map("P"), map("U"), map("N")))
       }
       println()
     }
       /*
              /*for (line <- Source.fromInputStream().getLines()) {
                line.split("\\s+", 2) match {
                  case Array(sentiment, text) => {
                    val row = matrix(sentiment)
                    val outcome = categorizer.categorize(text)
                    //println(outcome.mkString(", "), sentiment)
                    val res = categorizer.categorize(outcome)
                    row(res) += 1
                  }
                  case _ => {
                    throw new Exception("This shouldnt happen")
                  }
                }
              }
              println("%4s |%7s |%7s |%7s".format("", "P", "U", "N"))
              println("-" * 35)
              for((key,map)<-matrix){
                println("%4s |%7d |%7d |%7d".format(key, map("P"), map("U"), map("N")))
              }

            }


            def traverse(root:Parse):Seq[String] = {
              val list = new collection.mutable.MutableList[String]
               if(root.getType == "NP" || root.getType == "VP" )
                   list += root.getSpan.getCoveredText(root.getText).toString
              else for(child <- root.getChildren){
                 list ++= traverse(child)
               }

              list
           }


           //def noOfWords(parse:Parse)

           "playing with tree" in {
             val sentenceDetector = new SentenceDetectorME(new SentenceModel(classLoader.getResourceAsStream("en-sent.bin")))
             val model = new ParserModel(classLoader.getResourceAsStream("en-parser-chunking.bin"))
             val parser = ParserFactory.create(model)

             val doc = "Python is a programming language that lets you work more quickly and integrate your systems more effectively. Please don't eat sucky burgers in this restaurant. don't ever eat the pizza here." +
                       "You can learn to use Python and see almost immediate gains in productivity and lower maintenance costs. The food was not as good as it is made out to be. Coming here was not a good idea."
             val sentences = sentenceDetector.sentDetect(doc)
             for (sentence <- sentences) {
               val parses = ParserTool.parseLine(sentence, parser, 1)
               for (parse <- parses) {
                 for (parse1<-parse.getTagNodes){
                   print(parse1.toString  + ",")
                 }
                 println("\n" +traverse(parse))
                 //println(traverse(parse,"VP" ))
       //          println(parse.getChildren()(0).getText)
               }
             }
           }*/
*/
  }


}
