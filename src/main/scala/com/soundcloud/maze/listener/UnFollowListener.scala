package com.soundcloud.maze.listener

import com.soundcloud.maze.repository.Followers
import com.soundcloud.maze.service.NotificationService
import com.soundcloud.maze.UnFollow

import scala.util.Try

class UnFollowListener (followers: Followers, notificationService: NotificationService) extends Listener[UnFollow] {
  def process(event: UnFollow) = {
    Try(followers.removeFollower(event.followed, event.follower))
  }
}
