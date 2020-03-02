package com.soundcloud.maze

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class SequentialEventQueueTest extends AnyWordSpec with Matchers {

  implicit val ec = ExecutionContext.global
  "SequentialEventQueue.reject" should {
    "should add message to the dead letter" in {
      val spyDeadLetter = new mutable.Queue[String]()
      val queue = new SequentialEventQueue() {
        override protected val deadLetter: mutable.Queue[String] = spyDeadLetter
      }
      val dummyEvent = Broadcast(1)
      queue.reject(dummyEvent, None)
      spyDeadLetter.size.shouldBe(1)
      spyDeadLetter.dequeue().shouldBe(dummyEvent.toString)
    }
  }

  "SequentialEventQueue.enqueue" should {
    "should add message to the dead letter when  malformed" in {
      val spyDeadLetter = new mutable.Queue[String]()
      val queue = new SequentialEventQueue() {
        override protected val deadLetter: mutable.Queue[String] = spyDeadLetter
      }
      queue.enqueue("foo")
      spyDeadLetter.size.shouldBe(1)
      spyDeadLetter.dequeue().shouldBe("foo")
    }
  }

}
