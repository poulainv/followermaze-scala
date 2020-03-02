package com.soundcloud.maze.repository


import java.net.Socket

import scala.collection.mutable

class Clients {

  private val sessions = mutable.Map[Int, Socket]()

  def add(userId: Int, connectionStream: Socket) = {
    sessions += (userId -> connectionStream)
    println(s"Add client ${userId}: ${sessions.size} total")
  }

  def getAll() = {
    sessions.values
  }

  def get(userId: Int) = {
    sessions.get(userId)
  }

  def clear() = {
    sessions.clear()
  }

}