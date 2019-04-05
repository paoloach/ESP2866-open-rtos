package esp8266.plugin.achdjian.it.wizard.espressif.rtos

class Creator3_1 : Creator {
    override fun sourceMain_C() = "templates/espressif3_1/main.c"

    override fun config(wizardMenu: MenuWizardData): String  {
        val builder = StringBuilder()


        builder.appendln("#define CONFIG_CONSOLE_UART_NUM 1")
        builder.appendln("#define CONFIG_FREERTOS_HZ 100")
        builder.append("#define CONFIG_SPI_FLASH_FREQ ").appendln(wizardMenu.flashFreqHex)
        builder.append("#define CONFIG_SPI_FLASH_MODE ").appendln(wizardMenu.flashModeHex)
        builder.append("#define CONFIG_TASK_WDT_TIMEOUT_S ").appendln(wizardMenu.taskWdtTimeout)
        return builder.toString()
    }

    override fun createConfigFlags(wizardMenu: MenuWizardData): String {
        var flags =  " -D_POSIX_THREADS=1 -D_UNIX98_THREAD_MUTEX_ATTRIBUTES=1-DMQTT_TASK -DMQTTCLIENT_PLATFORM_HEADER=MQTTFreeRTOS.h  -DWOLFSSL_USER_SETTINGS  -D_CLOCKS_PER_SEC_=CONFIG_FREERTOS_HZ"
        if (wizardMenu.bootloaderCheckAppSum){
            flags += " -DCONFIG_ENABLE_BOOT_CHECK_SUM=1"
        }
        return flags
    }

    override fun createTemplatePath(): String  ="templates/espressif3_1"
}