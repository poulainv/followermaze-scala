package com.soundcloud.maze.listener

import com.soundcloud.maze.Event

import scala.util.Try


trait Listener[A <: Event] {
  def process(event: A): Try[Unit]
}