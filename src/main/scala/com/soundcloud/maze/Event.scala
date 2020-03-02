package com.soundcloud.maze

object Event {

  val orderedBySeq: Ordering[Event] = Ordering.by(_.sequenceNum)
  implicit def toInt(str: String) = str.trim.toInt

  def apply(eventMessage: String): Option[Event] = {
    eventMessage.split('|') match {
      case Array(sequenceNum, "F", fromUserId, toUserId) =>
        Some(Follow(sequenceNum, fromUserId, toUserId))
      case Array(sequenceNum, "U", fromUserId, toUserId) =>
        Some(UnFollow(sequenceNum, fromUserId, toUserId))
      case Array(sequenceNum, "B") => Some(Broadcast(sequenceNum))
      case Array(sequenceNum, "P", fromUserId, toUserId) =>
        Some(Private(sequenceNum, fromUserId, toUserId))
      case Array(sequenceNum, "S", fromUserId) => Some(StatusUpdate(sequenceNum, fromUserId))
      case _ => None
    }
  }
}

trait Event {
  val sequenceNum: Int
  def toString: String
}

case class Broadcast(sequenceNum: Int) extends Event {
  override def toString = s"$sequenceNum|B"
}
case class Private(sequenceNum: Int, fromUserId: Int, toUserId: Int) extends Event {
  override def toString = s"$sequenceNum|P|$fromUserId|$toUserId"
}
case class Follow(sequenceNum: Int, follower: Int, followed: Int) extends Event {
  override def toString = s"$sequenceNum|F|$follower|$followed"
}
case class UnFollow(sequenceNum: Int, follower: Int, followed: Int) extends Event {
  override def toString = s"$sequenceNum|U|$follower|$followed"
}
case class StatusUpdate(sequenceNum: Int, fromUserId: Int) extends Event {
  override def toString = s"$sequenceNum|S|$fromUserId"
}