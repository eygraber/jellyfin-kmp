package com.eygraber.jellyfin.test.utils

import kotlinx.coroutines.channels.Channel

class TestSubjectCoordinator {
  private val channel = Channel<Unit>()

  suspend fun wait() {
    channel.receive()
  }

  suspend fun proceed() {
    channel.send(Unit)
  }
}

inline fun withTestSubjectCoordinator(
  block: TestSubjectCoordinator.() -> Unit,
) {
  TestSubjectCoordinator().apply(block)
}
