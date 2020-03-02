package com.soundcloud.maze.repository

import scala.collection.mutable

class Followers {

  private val followers = mutable.Map[Int, Set[Int]]()

  def getAll(userId: Int): Set[Int] = {
    followers.getOrElse(userId, Set.empty)
  }

  def addFollower(userId: Int, followedBy: Int) = {
    followers.get(userId) match {
      case Some(value) => {
        followers.put(userId, value + followedBy)
      }
      case None => {
        followers.put(userId, Set(followedBy))
      }
    }
  }

  def removeFollower(userId: Int, followedBy: Int) = {
    followers.get(userId) match {
      case Some(value) => {
        followers.put(userId, value - followedBy)
      }
      case None => {
        followers.remove(userId)
      }
    }
  }
}
