package nl.sajansen.keystroke

import nl.sajansen.keystroke.queItems.KeyStrokeQueItem
import objects.que.JsonQue
import java.awt.event.KeyEvent
import javax.swing.JLabel
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyStrokeQueItemTest {

    @Test
    fun testKeyEventToString() {
        val keyEvent = KeyEvent(JLabel(), 0, 0, 0, 86, '')
        assertEquals("V", KeyStrokeQueItem.keyEventToString(keyEvent))

        val keyEvent1 = KeyEvent(JLabel(), 0, 0, 2, 86, '')
        assertEquals("Ctrl+V", KeyStrokeQueItem.keyEventToString(keyEvent1))

        val keyEvent2 = KeyEvent(JLabel(), 0, 0, 11, 86, '')
        assertEquals("Ctrl+Alt+Shift+V", KeyStrokeQueItem.keyEventToString(keyEvent2))
    }

    @Test
    fun testFromJson() {
        val jsonQueItem = JsonQue.QueItem(
            "",
            "",
            "Ctrl+V",
            false,
            hashMapOf(
                "id" to "1",
                "when" to "2",
                "modifiers" to "2",
                "keyCode" to "86",
                "keyChar" to "\u0016"
            )
        )

        val queItem = KeyStrokeQueItem.fromJson(KeyStrokePlugin(), jsonQueItem)

        assertEquals("Ctrl+V", queItem.name)
        assertEquals(1, queItem.keyEvent.id)
        assertEquals(2, queItem.keyEvent.`when`)
        assertEquals(2, queItem.keyEvent.modifiers)
        assertEquals(86, queItem.keyEvent.keyCode)
        assertEquals('', queItem.keyEvent.keyChar)
    }
    
    @Test
    fun testToJson() {
        val keyEvent = KeyEvent(JLabel(), 1, 2, 2, 86, '')
        val queItem = KeyStrokeQueItem(KeyStrokePlugin(), keyEvent)
        
        val jsonQueItem = queItem.toJson()
        
        assertEquals("Ctrl+V", jsonQueItem.name)
        assertEquals("1", jsonQueItem.data["id"])
        assertEquals("2", jsonQueItem.data["when"])
        assertEquals("2", jsonQueItem.data["modifiers"])
        assertEquals("86", jsonQueItem.data["keyCode"])
        assertEquals("\u0016", jsonQueItem.data["keyChar"])
    }
}