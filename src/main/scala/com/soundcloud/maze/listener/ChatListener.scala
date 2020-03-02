package com.soundcloud.maze.listener

import com.soundcloud.maze.repository.Followers
import com.soundcloud.maze.service.NotificationService
import com.soundcloud.maze.Private

class ChatListener(followers: Followers, notificationService: NotificationService) extends Listener[Private] {
  def process(event: Private) = notificationService.notifyOne(event.toUserId, event)
}
