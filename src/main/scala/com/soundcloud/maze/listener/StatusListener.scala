package com.soundcloud.maze.listener

import com.soundcloud.maze.repository.Followers
import com.soundcloud.maze.service.NotificationService
import com.soundcloud.maze.StatusUpdate

import scala.util.{Failure, Success}

class StatusListener(followers: Followers, notificationService: NotificationService) extends Listener[StatusUpdate] {

  override def process(event: StatusUpdate) =
    followers.getAll(event.fromUserId)
      .map(notificationService.notifyOne(_, event)).toList.forall(_.isSuccess) match {
      case true => Success(event)
      case false => Failure(new Exception("One of the follower was not connected"))
    }
}
