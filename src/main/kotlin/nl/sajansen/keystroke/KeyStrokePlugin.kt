package nl.sajansen.keystroke

import nl.sajansen.keystroke.queItems.KeyStrokeQueItem
import nl.sajansen.keystroke.queItems.WindowFocusQueItem
import objects.que.JsonQueue
import objects.que.QueItem
import plugins.common.QueItemBasePlugin
import java.awt.Color
import javax.swing.Icon
import javax.swing.JComponent

class KeyStrokePlugin : QueItemBasePlugin {
    override val name: String = "KeyStrokePlugin"
    override val description: String = "Plugin to create queue items which will execute key strokes"
    override val version: String = PluginInfo.version
    override val icon: Icon? = null

    override val tabName: String = "KeyStroke"
    internal val quickAccessColor = Color(255, 252, 229)

    override fun sourcePanel(): JComponent {
        return SourcePanel(this)
    }

    override fun configStringToQueItem(value: String): QueItem {
        throw NotImplementedError("This method is deprecated")
    }

    override fun jsonToQueItem(jsonQueItem: JsonQueue.QueueItem): QueItem {
        return when (jsonQueItem.className) {
            WindowFocusQueItem::class.java.simpleName -> WindowFocusQueItem(this, jsonQueItem.name)
            KeyStrokeQueItem::class.java.simpleName -> KeyStrokeQueItem.fromJson(this, jsonQueItem)
            else -> throw IllegalArgumentException("Invalid $name queue item: ${jsonQueItem.className}")
        }
    }
}