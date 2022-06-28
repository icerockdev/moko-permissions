package com.icerockdev.library

import dev.icerock.moko.mvvm.test.TestViewModelScope
import dev.icerock.moko.mvvm.test.createTestEventsDispatcher
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.test.createPermissionControllerMock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleViewModelTest {
    @BeforeTest
    fun setup() {
        TestViewModelScope.setupViewModelScope(CoroutineScope(Dispatchers.Unconfined))
    }

    @AfterTest
    fun tearDown() {
        TestViewModelScope.resetViewModelScope()
    }

    @Test
    fun `test successful permission`() {
        val eventsListener = EventsListenerCatcher()
        val permission = Permission.RECORD_AUDIO
        val controller: PermissionsController = createPermissionControllerMock(
            allow = setOf(permission)
        )
        val viewModel = SampleViewModel(
            eventsDispatcher = createTestEventsDispatcher(eventsListener),
            permissionsController = controller,
            permissionType = permission
        )

        viewModel.onRequestPermissionButtonPressed()

        assertEquals(expected = listOf("onSuccess"), actual = eventsListener.events)
    }

    @Test
    fun `test already got permission`() {
        val eventsListener = EventsListenerCatcher()
        val permission = Permission.RECORD_AUDIO
        val controller: PermissionsController = createPermissionControllerMock(
            granted = setOf(permission)
        )
        val viewModel = SampleViewModel(
            eventsDispatcher = createTestEventsDispatcher(eventsListener),
            permissionsController = controller,
            permissionType = permission
        )

        viewModel.onRequestPermissionButtonPressed()

        assertEquals(expected = listOf("onSuccess"), actual = eventsListener.events)
    }

    @Test
    fun `test reject permission`() {
        val eventsListener = EventsListenerCatcher()
        val controller: PermissionsController = createPermissionControllerMock(
            allow = emptySet()
        )
        val viewModel = SampleViewModel(
            eventsDispatcher = createTestEventsDispatcher(eventsListener),
            permissionsController = controller,
            permissionType = Permission.RECORD_AUDIO
        )

        viewModel.onRequestPermissionButtonPressed()

        assertEquals(
            expected = listOf("onDenied(dev.icerock.moko.permissions.DeniedException: mock block permission)"),
            actual = eventsListener.events
        )
    }

    class EventsListenerCatcher : SampleViewModel.EventListener {
        private val _events = mutableListOf<String>()
        val events: List<String> = _events

        override fun onSuccess() {
            _events.add("onSuccess")
        }

        override fun onDenied(exception: DeniedException) {
            _events.add("onDenied($exception)")
        }

        override fun onDeniedAlways(exception: DeniedAlwaysException) {
            _events.add("onDeniedAlways($exception)")
        }
    }
}
