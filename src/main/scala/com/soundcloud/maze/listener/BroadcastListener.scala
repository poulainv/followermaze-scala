package com.soundcloud.maze.listener

import com.soundcloud.maze.service.NotificationService
import com.soundcloud.maze.Broadcast

import scala.util.{Success, Try}

class BroadcastListener(notificationService: NotificationService) extends Listener[Broadcast] {
  def process(event: Broadcast): Try[Unit] = Success(notificationService.notifyAll(event))
}
