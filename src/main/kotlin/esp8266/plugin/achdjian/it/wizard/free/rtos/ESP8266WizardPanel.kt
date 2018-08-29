package esp8266.plugin.achdjian.it.wizard.free.rtos

import esp8266.plugin.achdjian.it.ui.panel
import esp8266.plugin.achdjian.it.wizard.FlashSize
import esp8266.plugin.achdjian.it.wizard.WizardData
import java.awt.BorderLayout
import java.awt.event.ItemEvent
import javax.swing.JPanel


class ESP8266WizardPanel(clionPanel: JPanel, val wizardData: WizardData) : JPanel(BorderLayout()) {
    companion object {
        val extraModules = arrayListOf("ad770x", "ads111x", "bearssl", "bh1750", "bme680", "bmp180", "bmp280", "ccs811", "cpp_support", "crc_generic",
                "dhcpserver", "dht", "ds1302", "ds1307", "ds18b20", "ds3231", "dsm", "fatfs", "fonts", "fram",
                "hd44780", "hmc5883l", "http-parser", "http_client_ota", "httpd", "i2c", "i2s_dma", "ina3221", "jsmn", "l3gd20h",
                "libesphttpd", "lis3dh", "lis3mdl", "lsm303d", "max7219", "mbedtls", "mcp4725", "mdnsresponder", "ms561101ba03", "multipwm @ 44ecea5",
                "onewire", "paho_mqtt_c", "pca9685", "pcf8574", "pcf8591", "pwm", "sdio", "sht3x", "sntp",
                "softuart", "spiffs", "ssd1306", "stdin_uart_interrupt", "timekeeping", "tsl2561", "tsl4531", "ultrasonic", "wificfg", "ws2812",
                "ws2812_i2s")
        val availableSize = FlashSize.values().map { it.strSize() }.toTypedArray()
        val availableMode = arrayOf("qio", "qout", "dio")
        val availableSpeed = arrayOf("80", "40", "26", "20")
    }

    init {
        add(clionPanel, BorderLayout.PAGE_START)
        val p = panel("ESP 8266 Free RTOS configuration") {
            row("flash Size") { comboBox(availableSize, "512KB", { wizardData.flashSize = FlashSize.getElement(it.item.toString()) }) }
            row("Flash Mode") { comboBox(availableMode, "qio", { wizardData.flashMode = it.item.toString() }) }
            row("Flash Speed") { comboBox(availableSpeed, "40", { wizardData.flashSpeed = it.item.toString() }) }
            row("ESP port") { textArea("/dev/ttyUSB0", { wizardData.espPort = it?.document.toString() }) }
            row() { checkBox("Float Support", { wizardData.floatSupport = it.stateChange == ItemEvent.SELECTED }) }
            row() { checkBox("OTA Support", { wizardData.otaSupport = it.stateChange == ItemEvent.SELECTED }) }
        }

        add(p)

        add(panel(title = "Extras") {
            extraModules.forEach { name -> row() { checkBox(name, { wizardData.extras[name] = it.stateChange == ItemEvent.SELECTED }) } }
        }, BorderLayout.PAGE_END)
    }
}