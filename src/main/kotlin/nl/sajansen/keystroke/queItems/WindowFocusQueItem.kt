package nl.sajansen.keystroke.queItems

import com.sun.jna.platform.DesktopWindow
import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.User32
import objects.notifications.Notifications
import plugins.common.BasePlugin
import plugins.common.QueItem
import java.util.logging.Logger

class WindowFocusQueItem(override val plugin: BasePlugin, override val name: String) : QueItem {

    private val logger = Logger.getLogger(WindowFocusQueItem::class.java.name)

    override var executeAfterPrevious: Boolean = false

    override fun activate() {
        val window = findWindowHandle(name) ?: return

        logger.info("Set focus to window ${window.title}")
        User32.INSTANCE.SetForegroundWindow(window.hwnd)
        User32.INSTANCE.SetFocus(window.hwnd)
    }

    override fun deactivate() {}

    override fun toConfigString(): String {
        throw NotImplementedError("This method is deprecated")
    }

    override fun renderText(): String = "Focus: $name"

    private fun findWindowHandle(windowTitle: String): DesktopWindow? {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            logger.warning("Must run on Windows to find the window")
            Notifications.add("Plugin must run on Windows operating system", "EasyWorship")
            return null
        }

        val window = WindowUtils.getAllWindows(true)
            .find { it.title.contains(windowTitle) }

        if (window != null) {
            return window
        }

        logger.warning("Failed to find window: $windowTitle")
        Notifications.add("Could not find '$windowTitle' window", "KeyStroke")
        return null
    }
}