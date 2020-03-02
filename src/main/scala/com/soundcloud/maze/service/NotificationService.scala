package com.soundcloud.maze.service

import java.io.{BufferedWriter, OutputStreamWriter}
import java.net.Socket

import com.soundcloud.maze.Event
import com.soundcloud.maze.repository.Clients

import scala.util.{Failure, Success, Try}

class NotificationService (clients: Clients) {

  def notifyOne(userId: Int, event: Event): Try[Unit] = {
    clients.get(userId) match {
      case Some(socket) => Success(send(event, socket))
      case None => Failure(new Exception("Client not connected"))
    }
  }

  def notifyAll(event: Event) = {
    clients.getAll.foreach(send(event, _))
  }

  private def send(event: Event, socket: Socket) = {
    val writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    writer.write(s"${event.toString}\n")
    writer.flush()
  }
}
