package nl.sajansen.keystroke.queItems

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC
import objects.notifications.Notifications
import plugins.common.BasePlugin
import plugins.common.QueItem
import java.util.logging.Logger


class WindowFinder(private val windowTitle: String) : WNDENUMPROC {
    private val logger = Logger.getLogger(WindowFinder::class.java.name)

    var windowHandle: HWND? = null
    var count: Int = 0

    override fun callback(hWnd: HWND, arg1: Pointer?): Boolean {
        val windowText = CharArray(512)
        User32.INSTANCE.GetWindowText(hWnd, windowText, 512)
        val wText = Native.toString(windowText)

        if (wText.isEmpty()) {
            return true
        }

        if (wText.toLowerCase().contains(windowTitle)) {
            windowHandle = hWnd
            return false
        }
        return true
    }
}

class WindowFocusQueItem(override val plugin: BasePlugin, override val name: String) : QueItem {

    private val logger = Logger.getLogger(WindowFocusQueItem::class.java.name)

    override var executeAfterPrevious: Boolean = false

    override fun activate() {
        val windowHandle = findWindowHandle(name) ?: return

        logger.info("Set focus to window $name")
        User32.INSTANCE.SetForegroundWindow(windowHandle)
        User32.INSTANCE.SetFocus(windowHandle)
    }

    override fun deactivate() {}

    override fun toConfigString(): String {
        throw NotImplementedError("This method is deprecated")
    }

    override fun renderText(): String = "Focus: $name"

    private fun findWindowHandle(windowTitle: String): HWND? {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            logger.warning("Must run on Windows to find the window")
            Notifications.add("Plugin must run on Windows operating system", "KeyStroke")
            return null
        }

        val windowFinder = WindowFinder(windowTitle.toLowerCase())
        User32.INSTANCE.EnumWindows(windowFinder, null)

        if (windowFinder.windowHandle != null) {
            return windowFinder.windowHandle
        }

        logger.warning("Failed to find window: $windowTitle")
        Notifications.add("Could not find '$windowTitle' window", "KeyStroke")
        return null
    }
}