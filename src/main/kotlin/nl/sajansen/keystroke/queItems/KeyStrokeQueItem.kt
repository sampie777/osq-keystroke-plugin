package nl.sajansen.keystroke.queItems

import nl.sajansen.keystroke.KeyStrokePlugin
import objects.notifications.Notifications
import objects.que.JsonQue
import plugins.common.BasePlugin
import plugins.common.QueItem
import java.awt.Robot
import java.awt.event.KeyEvent
import java.util.logging.Logger
import javax.swing.JComponent

class KeyStrokeQueItem(override val plugin: BasePlugin, val keyEvent: KeyEvent) : QueItem {

    private val logger = Logger.getLogger(KeyStrokeQueItem::class.java.name)

    companion object {
        fun keyEventToString(e: KeyEvent): String {
            return listOf(
                KeyEvent.getKeyModifiersText(e.modifiers),
                KeyEvent.getKeyText(e.keyCode)
            )
                .filter { !it.isBlank() }
                .joinToString("+")
        }

        fun fromJson(plugin: KeyStrokePlugin, jsonQueItem: JsonQue.QueItem): KeyStrokeQueItem {
            val keyEvent = KeyEvent(
                object : JComponent(){},
                jsonQueItem.data["id"]!!.toInt(),
                jsonQueItem.data["when"]!!.toLong(),
                jsonQueItem.data["modifiers"]!!.toInt(),
                jsonQueItem.data["keyCode"]!!.toInt(),
                jsonQueItem.data["keyChar"]!!.first()
            )

            return KeyStrokeQueItem(plugin, keyEvent)
        }
    }

    override val name: String = keyEventToString(keyEvent)
    override var executeAfterPrevious = false

    override fun renderText(): String = "Key: $name"

    override fun activate() {
        try {
            val robot = Robot()
            if (keyEvent.modifiers.and(KeyEvent.CTRL_MASK) != 0) {
                robot.keyPress(KeyEvent.VK_CONTROL)
            }
            if (keyEvent.modifiers.and(KeyEvent.SHIFT_MASK) != 0) {
                robot.keyPress(KeyEvent.VK_SHIFT)
            }
            if (keyEvent.modifiers.and(KeyEvent.ALT_MASK) != 0) {
                robot.keyPress(KeyEvent.VK_ALT)
            }

            robot.keyPress(keyEvent.keyCode)
            robot.delay(50)
            robot.keyRelease(keyEvent.keyCode)

            if (keyEvent.modifiers.and(KeyEvent.ALT_MASK) != 0) {
                robot.keyRelease(KeyEvent.VK_ALT)
            }
            if (keyEvent.modifiers.and(KeyEvent.SHIFT_MASK) != 0) {
                robot.keyRelease(KeyEvent.VK_SHIFT)
            }
            if (keyEvent.modifiers.and(KeyEvent.CTRL_MASK) != 0) {
                robot.keyRelease(KeyEvent.VK_CONTROL)
            }
        } catch (e: Exception) {
            logger.warning("Failed to execute key stroke $name")
            e.printStackTrace()
            Notifications.add("Failed to execute key stroke $name", "KeyStroke")
        }
    }

    override fun deactivate() {}

    override fun toConfigString(): String {
        throw NotImplementedError("This method is deprecated")
    }

    override fun toJson(): JsonQue.QueItem {
        val jsonItem = super.toJson()
        jsonItem.data["id"] = keyEvent.id.toString()
        jsonItem.data["when"] = keyEvent.`when`.toString()
        jsonItem.data["modifiers"] = keyEvent.modifiers.toString()
        jsonItem.data["keyCode"] = keyEvent.keyCode.toString()
        jsonItem.data["keyChar"] = keyEvent.keyChar.toString()
        return jsonItem
    }
}