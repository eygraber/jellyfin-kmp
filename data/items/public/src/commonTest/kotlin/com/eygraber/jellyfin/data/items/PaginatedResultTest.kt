package com.eygraber.jellyfin.data.items

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PaginatedResultTest {
  @Test
  fun hasMore_is_true_when_more_items_exist() {
    val result = PaginatedResult(
      items = listOf("a", "b", "c"),
      totalRecordCount = 10,
      startIndex = 0,
    )
    result.hasMore shouldBe true
  }

  @Test
  fun hasMore_is_false_when_all_items_loaded() {
    val result = PaginatedResult(
      items = listOf("a", "b", "c"),
      totalRecordCount = 3,
      startIndex = 0,
    )
    result.hasMore shouldBe false
  }

  @Test
  fun hasMore_is_false_for_last_page() {
    val result = PaginatedResult(
      items = listOf("d", "e"),
      totalRecordCount = 5,
      startIndex = 3,
    )
    result.hasMore shouldBe false
  }

  @Test
  fun hasMore_is_true_for_middle_page() {
    val result = PaginatedResult(
      items = listOf("b", "c"),
      totalRecordCount = 5,
      startIndex = 1,
    )
    result.hasMore shouldBe true
  }

  @Test
  fun hasMore_is_false_for_empty_result() {
    val result = PaginatedResult(
      items = emptyList<String>(),
      totalRecordCount = 0,
      startIndex = 0,
    )
    result.hasMore shouldBe false
  }
}
