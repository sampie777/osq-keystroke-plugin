package nl.sajansen.keystroke

import gui.MainFrame
import java.util.*

object PluginInfo {
    private val properties = Properties()
    val version: String
    val author: String

    init {
        properties.load(KeyStrokePlugin::class.java.getResourceAsStream("/nl/sajansen/keystroke/plugin.properties"))
        version = properties.getProperty("version")
        author = properties.getProperty("author")
    }
}