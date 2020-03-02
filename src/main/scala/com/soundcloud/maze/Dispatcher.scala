package com.soundcloud.maze

import com.soundcloud.maze.listener._

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class Dispatcher(eventQueue: SequentialEventQueue,
                 broadcastListener: BroadcastListener,
                 followListener: FollowListener,
                 chatListener: ChatListener,
                 unFollowListener: UnFollowListener,
                 statusListener: StatusListener)(implicit ex: ExecutionContext) {

  def start(): Future[Unit] = Future {
    println("Start dispatcher")
    process()
  }

  @tailrec
  private def process(): Unit = {
    eventQueue.dequeue().foreach(innerProcessEvent)
    process()
  }

  def innerProcessEvent(event: Event): Unit = {
    publish(event) match {
      case Success(_) => eventQueue.ack(event)
      case Failure(exc) => eventQueue.reject(event, Some(exc.getMessage))
    }
  }

  def publish(event: Event): Try[Unit] = {
    event match {
      case e: Broadcast => broadcastListener.process(e)
      case e: Follow => followListener.process(e)
      case e: UnFollow => unFollowListener.process(e)
      case e: StatusUpdate => statusListener.process(e)
      case e: Private => chatListener.process((e))
      case _ => Failure(new Exception("Event not supported"))
    }
  }

}
