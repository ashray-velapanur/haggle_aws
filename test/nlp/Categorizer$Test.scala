package nlp

import org.specs2.mutable._
import io.Source
import scala.collection.mutable.Map

class Categorizer$Test extends Specification {
  def confustionMatrix = {
    Map("P" -> Map("P" -> 0, "U" -> 0, "N" -> 0), "U" -> Map("P" -> 0, "U" -> 0, "N" -> 0), "N" -> Map("P" -> 0, "U" -> 0, "N" -> 0))
  }

  val classLoader = getClass.getClassLoader
  "Categorizer" should {
    "categorize all yelp reviews " in {
      val matrix = confustionMatrix
      val categorizer = new Categorizer()
      for (line <- Source.fromInputStream(classLoader.getResourceAsStream("yelp_model_sentiment")).getLines()) {
        line.split("\\s+", 2) match {
          case Array(sentiment, text) => {
            val row = matrix(sentiment)
            val outcome = categorizer.categorize(text)
            println(outcome.mkString(", "), sentiment)
            val res = categorizer.getBestCategory(outcome)
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
  }

}
