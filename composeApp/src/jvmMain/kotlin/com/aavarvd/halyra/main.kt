@file:Suppress("DEPRECATION")

package com.aavarvd.halyra

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.aavarvd.halyra.ui.SplashScreen
import kotlinx.coroutines.delay
import java.awt.Frame
import java.awt.GraphicsConfiguration
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.Window as AwtWindow
import java.beans.PropertyChangeListener
import kotlin.time.Duration.Companion.milliseconds
internal fun usableBounds(configuration: GraphicsConfiguration): Rectangle {
    val insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration)
    val bounds = configuration.bounds
    val width = (bounds.width - insets.left - insets.right).coerceAtLeast(1)
    val height = (bounds.height - insets.top - insets.bottom).coerceAtLeast(1)
    return Rectangle(
        bounds.x + insets.left,
        bounds.y + insets.top,
        width,
        height
    )
}

private fun clampWindowToBounds(window: AwtWindow, bounds: Rectangle) {
    val width = window.width.coerceAtMost(bounds.width).coerceAtLeast(1)
    val height = window.height.coerceAtMost(bounds.height).coerceAtLeast(1)
    val maxX = bounds.x + bounds.width - width
    val maxY = bounds.y + bounds.height - height
    val x = window.x.coerceIn(bounds.x, maxX)
    val y = window.y.coerceIn(bounds.y, maxY)

    if (window.x != x || window.y != y || window.width != width || window.height != height) {
        window.setBounds(x, y, width, height)
    }
}

fun main() = application {
    val useCustomTitlebar = false

    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(4000.milliseconds)
        showSplash = false
    }

    if (showSplash) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "",
            undecorated = true,
            resizable = false,
            alwaysOnTop = true,
            state = rememberWindowState(
                placement = WindowPlacement.Floating,
                position = WindowPosition.Aligned(Alignment.Center),
                size = DpSize(600.dp, 400.dp)
            )
        ) {
            SplashScreen()
        }
    } else {
        val mainWindowState = rememberWindowState(placement = WindowPlacement.Maximized)

        var isAskingToClose by remember { mutableStateOf(false) }

        Window(
            onCloseRequest = { isAskingToClose = true },
            title = "Halyra",
            icon = painterResource("images/icon.png"),
            state = mainWindowState,
            undecorated = useCustomTitlebar,
            resizable = !useCustomTitlebar || mainWindowState.placement != WindowPlacement.Maximized
        ) {
            if (useCustomTitlebar) {
                LaunchedEffect(mainWindowState.placement) {
                    if (mainWindowState.placement == WindowPlacement.Maximized) {
                        window.extendedState = Frame.MAXIMIZED_BOTH
                    } else if (mainWindowState.placement == WindowPlacement.Floating) {
                        window.extendedState = Frame.NORMAL
                    }
                }

                LaunchedEffect(mainWindowState.isMinimized) {
                    if (mainWindowState.isMinimized) {
                        window.extendedState = Frame.ICONIFIED
                    }
                }

                DisposableEffect(window) {
                    val windowStateListener = object : java.awt.event.WindowStateListener {
                        override fun windowStateChanged(e: java.awt.event.WindowEvent) {
                            val state = e.newState
                            val isMaximized = (state and Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
                            val isMinimized = (state and Frame.ICONIFIED) == Frame.ICONIFIED
                            
                            if (isMaximized && mainWindowState.placement != WindowPlacement.Maximized) {
                                mainWindowState.placement = WindowPlacement.Maximized
                            } else if (!isMaximized && !isMinimized && mainWindowState.placement == WindowPlacement.Maximized) {
                                mainWindowState.placement = WindowPlacement.Floating
                            }
                            
                            if (isMinimized != mainWindowState.isMinimized) {
                                mainWindowState.isMinimized = isMinimized
                            }
                        }
                    }
                    window.addWindowStateListener(windowStateListener)

                    fun applyMaxBounds() {
                        window.graphicsConfiguration?.let { cfg ->
                            val b = usableBounds(cfg)
                            window.maximizedBounds = b
                            if (mainWindowState.placement == WindowPlacement.Maximized) {
                                window.extendedState = Frame.MAXIMIZED_BOTH
                            } else {
                                clampWindowToBounds(window, b)
                            }
                        }
                    }

                    applyMaxBounds()

                    val propertyListener = PropertyChangeListener { event ->
                        if (event.propertyName == "graphicsConfiguration") {
                            applyMaxBounds()
                        }
                    }

                    window.addPropertyChangeListener(propertyListener)

                    onDispose {
                        window.removePropertyChangeListener(propertyListener)
                        window.removeWindowStateListener(windowStateListener)
                    }
                }
            }

            var triggerClose by remember { mutableStateOf(false) }

            if (triggerClose) {
                SideEffect {
                    exitApplication()
                }
            }

            App(
                windowState = mainWindowState,
                useCustomTitlebar = useCustomTitlebar,
                closeRequested = isAskingToClose,
                onCloseRequest = { triggerClose = true },
                onCloseCancelled = { isAskingToClose = false }
            )

            if (isAskingToClose && !triggerClose) {
            }
        }
    }
}
