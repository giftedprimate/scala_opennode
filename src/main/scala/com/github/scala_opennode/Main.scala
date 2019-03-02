package com.github.scala_opennode

object Main extends App {
  val openNode = new OpenNode(sys.env.get("export mathbot_payments_openNode_apiKey"))
}
