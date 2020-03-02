package com.soundcloud.maze

import com.soundcloud.maze.listener._
import com.soundcloud.maze.repository.{Clients, Followers}
import com.soundcloud.maze.service.NotificationService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object Main {

  private val EventPort = 9090
  private val ClientPort = 9099

  def main(args: Array[String]): Unit = {

    val executorService = java.util.concurrent.Executors.newCachedThreadPool
    implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executorService)

    // Repositories & Queues
    val clientsRepository = new Clients()
    val followersRepository = new Followers()
    val eventQueue = new SequentialEventQueue()

    // Services & Listeners
    val notificationService = new NotificationService(clientsRepository)

    // Servers
    val clientSocketServer = new ClientSocketServer(ClientPort, clientsRepository)
    val eventSocketServer = new EventSocketServer(EventPort, eventQueue)
    val dispatcher = new Dispatcher(eventQueue,
      new BroadcastListener(notificationService),
      new FollowListener(followersRepository, notificationService),
      new ChatListener(followersRepository, notificationService),
      new UnFollowListener(followersRepository, notificationService),
      new StatusListener(followersRepository, notificationService))

    Await.result(Future.sequence(Seq(dispatcher.start, clientSocketServer.start, eventSocketServer.start)), Duration.Inf)

  }

}
