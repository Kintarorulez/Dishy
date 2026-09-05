package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dishy", appName)
  }

  @Test
  fun `verify realtime state default values`() {
    val state = com.example.data.remote.DishwasherRealtimeState()
    assertEquals(com.example.data.model.DishwasherPowerState.OFF, state.powerState)
    assertEquals(com.example.data.model.DishwasherOperationState.INACTIVE, state.operationState)
    assertEquals(0, state.remainingSeconds)
    assertEquals(0, state.progressPercent)
    assertEquals("Realtime Running Status Synced", state.syncStatusMessage)
  }
}
