package com.soundcloud.maze.listener

import com.soundcloud.maze.repository.Followers
import com.soundcloud.maze.service.NotificationService
import com.soundcloud.maze.Follow


class FollowListener(followers: Followers, notificationService: NotificationService) extends Listener[Follow] {

  override def process(event: Follow) = {
    followers.addFollower(event.followed, event.follower)
    notificationService.notifyOne(event.followed, event)
  }
}
