package com.soundcloud.maze

import java.io.{BufferedReader, InputStreamReader}
import java.net.{ServerSocket, Socket}
import java.util.concurrent.ConcurrentHashMap

import com.soundcloud.maze.repository.Clients

import scala.concurrent.{ExecutionContext, Future}

trait SocketServer {

  val port: Int
  val serverSocket = new ServerSocket(port)
  def start: Future[Unit]
  def handle(message: String, socket: Socket): Unit
  def shutdown: Unit
}

class ClientSocketServer(val port: Int, sessions: Clients)(implicit ex: ExecutionContext)
  extends SocketServer {

  override def start: Future[Unit] = Future {
    println(s"Listening for client on $port")
    while (true) {
      val socket = serverSocket.accept()
      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
      handle(reader.readLine, socket)
    }
  }

  override def handle(message: String, socket: Socket) = {
    val clientId = message.toInt
    sessions.add(clientId, socket)
  }

  override def shutdown: Unit = {
    serverSocket.close()
  }
}


class EventSocketServer(val port: Int, queue: SequentialEventQueue)(implicit ex: ExecutionContext)
  extends SocketServer {
  override def start: Future[Unit] = Future {
    println(s"Listening for event on $port")
    val socket = serverSocket.accept()
    val reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    reader.lines.forEach(message => {
      handle(message, socket)
    })
  }

  override def shutdown: Unit = {
    serverSocket.close()
  }

  override def handle(message: String, socket: Socket): Unit = {
    queue.enqueue(message)
  }
}