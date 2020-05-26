package nl.sajansen.keystroke

import handles.QueItemTransferHandler
import nl.sajansen.keystroke.queItems.KeyStrokeQueItem
import nl.sajansen.keystroke.queItems.WindowFocusQueItem
import objects.que.Que
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

class SourcePanel(private val plugin: KeyStrokePlugin) : JPanel() {

    init {
        initGui()
    }

    private fun initGui() {
        layout = BorderLayout(10, 10)
        border = EmptyBorder(10, 10, 0, 10)

        val titleLabel = JLabel("Actions")
        add(titleLabel, BorderLayout.PAGE_START)

        val itemListPanel = JPanel(GridLayout(0, 1))
        itemListPanel.add(createWindowFocusInput())
        itemListPanel.add(createKeyStrokeInput())

        val scrollPanelInnerPanel = JPanel(BorderLayout())
        scrollPanelInnerPanel.add(itemListPanel, BorderLayout.PAGE_START)
        val scrollPanel = JScrollPane(scrollPanelInnerPanel)
        scrollPanel.border = null
        add(scrollPanel, BorderLayout.CENTER)
    }

    private fun createWindowFocusInput(): JPanel {
        val panel = JPanel(BorderLayout(5, 5))
        panel.border = CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color(180, 180, 180)),
            EmptyBorder(8, 0, 15, 0)
        )

        val textField = JTextField()

        val addButton = JButton("+")
        addButton.toolTipText = "Click or drag to add"
        addButton.transferHandler = QueItemTransferHandler()

        addButton.addActionListener {
            if (textField.text.isEmpty()) {
                return@addActionListener
            }

            val queItem = WindowFocusQueItem(plugin, textField.text)
            textField.text = ""

            Que.add(queItem)
            GUI.refreshQueItems()
        }
        addButton.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (textField.text.isEmpty()) {
                    return
                }

                val queItem = WindowFocusQueItem(plugin, textField.text)
                textField.text = ""

                val transferHandler = (e.source as JButton).transferHandler as QueItemTransferHandler
                transferHandler.queItem = queItem
                transferHandler.exportAsDrag(e.source as JComponent, e, TransferHandler.COPY)
            }
        })

        panel.add(JLabel("Focus window"), BorderLayout.PAGE_START)
        panel.add(textField, BorderLayout.CENTER)
        panel.add(addButton, BorderLayout.LINE_END)
        return panel
    }

    private fun createKeyStrokeInput(): JPanel {
        val panel = JPanel(BorderLayout(5, 5))
        panel.border = EmptyBorder(10, 0, 10, 0)

        val textField = JTextField()
        val keyListener = object : KeyListener {
            var lastCharKeyEvent: KeyEvent? = null

            override fun keyTyped(e: KeyEvent) {}

            override fun keyPressed(e: KeyEvent) {}

            override fun keyReleased(e: KeyEvent) {
                if (!e.keyChar.isDefined()) {
                    return
                }

                lastCharKeyEvent = e
                textField.text = KeyStrokeQueItem.keyEventToString(e)
            }
        }
        textField.addKeyListener(keyListener)

        val addButton = JButton("+")
        addButton.toolTipText = "Click or drag to add"
        addButton.transferHandler = QueItemTransferHandler()

        addButton.addActionListener {
            if (textField.text.isEmpty() || keyListener.lastCharKeyEvent == null) {
                return@addActionListener
            }

            val queItem = KeyStrokeQueItem(plugin, keyListener.lastCharKeyEvent!!)
            textField.text = ""

            Que.add(queItem)
            GUI.refreshQueItems()
        }
        addButton.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (textField.text.isEmpty() || keyListener.lastCharKeyEvent == null) {
                    return
                }

                val queItem = KeyStrokeQueItem(plugin, keyListener.lastCharKeyEvent!!)
                textField.text = ""

                val transferHandler = (e.source as JButton).transferHandler as QueItemTransferHandler
                transferHandler.queItem = queItem
                transferHandler.exportAsDrag(e.source as JComponent, e, TransferHandler.COPY)
            }
        })

        panel.add(JLabel("Key stroke"), BorderLayout.PAGE_START)
        panel.add(textField, BorderLayout.CENTER)
        panel.add(addButton, BorderLayout.LINE_END)
        return panel
    }
}