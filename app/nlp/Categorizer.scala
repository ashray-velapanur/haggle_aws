package nlp

import opennlp.tools.doccat.DocumentCategorizer

object categorizer extends DocumentCategorizer{

  def categorize(sentence: Array[String]): Array[Double] = ???

  def getBestCategory(outcome: Array[Double]): String = ???

  def getIndex(category: String): Int = ???

  def getCategory(index: Int): String = ???

  def getNumberOfCategories: Int = ???

  def categorize(document: String): Array[Double] = ???

  def getAllResults(p1: Array[Double]): String = ???
}
