package com.eygraber.jellyfin.services.database.impl

import app.cash.sqldelight.Query
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

fun <T : Any> Query<T>.asFlow(): Flow<Query<T>> = flow {
  val channel = Channel<Unit>(CONFLATED)
  channel.trySend(Unit)

  val listener = Query.Listener {
    channel.trySend(Unit)
  }

  addListener(listener)
  try {
    @Suppress("UnusedVariable")
    for(item in channel) {
      emit(this@asFlow)
    }
  }
  finally {
    removeListener(listener)
  }
}

fun <T : Any> Flow<Query<T>>.mapToOne(): Flow<T> = map {
  it.awaitAsOne()
}

fun <T : Any> Flow<Query<T>>.mapToOneOrDefault(
  defaultValue: T,
): Flow<T> = map {
  it.awaitAsOneOrNull() ?: defaultValue
}

fun <T : Any> Flow<Query<T>>.mapToOneOrNull(): Flow<T?> = map {
  it.awaitAsOneOrNull()
}

fun <T : Any> Flow<Query<T>>.mapToOneNotNull(): Flow<T> = mapNotNull {
  it.awaitAsOneOrNull()
}

fun <T : Any> Flow<Query<T>>.mapToList(): Flow<List<T>> = map {
  it.awaitAsList()
}
