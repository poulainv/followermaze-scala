package com.soundcloud.maze


import com.soundcloud.maze.listener.BroadcastListener
import org.mockito.Mockito._
import org.scalatest.TryValues._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContext
import scala.util.Failure


class DispatcherTest extends AnyWordSpec with Matchers {

  implicit val ec = ExecutionContext.global
  "Dispatcher.publish" should {
    "should return Event Not Supported Failure with correct message if event is not supported" in {
      case class UnknowEvent(val sequenceNum: Int) extends Event
      val queue = new SequentialEventQueue()
      val dispatcher = new Dispatcher(queue, null, null, null, null, null)
      dispatcher.publish(UnknowEvent(0)).failure.exception.getMessage.shouldBe("Event not supported")
    }

    "should return Failure with correct message if process listener failed" in {
      val queue = mock(classOf[SequentialEventQueue])
      val listener = mock(classOf[BroadcastListener])
      val dispatcher = new Dispatcher(queue, listener, null, null, null, null)
      val dummyFailure = Failure(new Exception("Foo"))
      when(listener.process(Broadcast(0))).thenReturn(dummyFailure)
      dispatcher.publish(Broadcast(0)).shouldBe(dummyFailure)
    }
  }

  "Dispatcher.innerProcessEvent" should {
    "should reject message if message publication failed" in {
      val queue = mock(classOf[SequentialEventQueue])
      case class UnknowEvent(sequenceNum: Int) extends Event
      val dummyEvent = UnknowEvent(0)
      val dispatcher = new Dispatcher(queue, null, null, null, null, null)
      dispatcher.innerProcessEvent(dummyEvent)
      verify(queue).reject(dummyEvent, Some("Event not supported"))
    }
  }
}
