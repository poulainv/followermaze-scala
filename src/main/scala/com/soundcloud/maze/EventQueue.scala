package com.soundcloud.maze

import java.util.concurrent.ConcurrentHashMap

import scala.collection.mutable

trait Queue[T] {
  def enqueue(message: String)
  def dequeue(): Option[T]
}

class DeadLetter extends Queue[String] {

  def enqueue(message: String): Unit = println(message)
  def dequeue(): Option[String] = None
}

class SequentialEventQueue(firstSeqNb: Int = 1) extends Queue[Event] {

  protected var nextNb: Int = firstSeqNb
  protected val deadLetter = new mutable.Queue[String]()

  val map = new ConcurrentHashMap[Int, Event]()

  def enqueue(message: String) = Event(message) match {
    case Some(event) => map.put(event.sequenceNum, event)
    case None => deadLetter.enqueue(message)
  }

  def ack(event: Event): Unit = {
    map.remove(nextNb)
    nextNb += 1
  }

  def reject(event: Event, reason: Option[String]): Unit = {
    reason.foreach(println(_))
    deadLetter.enqueue(event.toString)
    println(event.toString)
    ack(event)
  }

  def dequeue(): Option[Event] = {
    if (!map.isEmpty && map.containsKey(nextNb)) {
      if (nextNb % 1000 == 0) {
        println(s"Dispatched $nextNb events")
      }
      val event = map.get(nextNb)
      Some(event)
    } else {
      None
    }
  }
}

