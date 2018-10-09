package esp8266.plugin.achdjian.it.wizard.espressif.rtos

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import esp8266.plugin.achdjian.it.settings.ESP8266SDKSettings
import esp8266.plugin.achdjian.it.settings.ESP8266SettingsState
import esp8266.plugin.achdjian.it.wizard.espressif.rtos.Constants.CONFIG_DIR
import esp8266.plugin.achdjian.it.wizard.espressif.rtos.Constants.CONFIG_FILE_NAME
import esp8266.plugin.achdjian.it.wizard.getResourceAsString
import org.apache.commons.codec.Charsets

fun createEspressifRTOSCMake(projectName: String, wizardData: MenuWizardData): String {
    var cmakelists = getResourceAsString("templates/espressif/CMakeLists.txt")
    val setting = ApplicationManager.getApplication().getComponent(ESP8266SettingsState::class.java, ESP8266SDKSettings.DEFAULT) as ESP8266SettingsState
    cmakelists = cmakelists
            .replace("__{project_name}__", projectName)
            .replace("__{ESPRESSIF_RTOS_DIR}__", "set(RTOS_DIR ${setting.espressifRtosPath})")
            .replace("__{flash_mode}__", wizardData.flashMode)
            .replace("__{flash_freq}__", wizardData.flashFreq)
            .replace("__{flash_size}__", wizardData.flashSize)
            .replace("__{esptool_port}__", wizardData.espToolPort)
            .replace("__{esptool_before}__", wizardData.espToolBefore)
            .replace("__{esptool_after}__", wizardData.espToolAfter)
            .replace("__{esptool_baudRate}__", wizardData.espToolBaudRate)
    if (wizardData.compressUpload)
        cmakelists = cmakelists.replace("__{esptool_compression}__", "-z")
    else
        cmakelists = cmakelists.replace("__{esptool_compression}__", "-u")
    return cmakelists
}

fun createEspressifRTORSubCMand(projectName: String, wizardMenu: MenuWizardData, path: VirtualFile) {
    makeSubCMake("bootloader", projectName, wizardMenu, path)
    makeSubCMake("bootloaderSupport", projectName, wizardMenu, path)
    makeSubCMake("cjson", projectName, wizardMenu, path)
    makeSubCMake("esp8266", projectName, wizardMenu, path)
    makeSubCMake("espOS", projectName, wizardMenu, path)
    makeSubCMake("freeRTOS", projectName, wizardMenu, path)
    makeSubCMake("jsmn", projectName, wizardMenu, path)
    makeSubCMake("log", projectName, wizardMenu, path)
    makeSubCMake("lwip", projectName, wizardMenu, path)
    makeSubCMake("mqtt", projectName, wizardMenu, path)
    makeSubCMake("newlib", projectName, wizardMenu, path)
    makeSubCMake("nvs_flash", projectName, wizardMenu, path)
    makeSubCMake("smartConfig", projectName, wizardMenu, path)
    makeSubCMake("spiffs", projectName, wizardMenu, path)
    makeSubCMake("spiFlash", projectName, wizardMenu, path)
    makeSubCMake("ssl", projectName, wizardMenu, path)
    makeSubCMake("tcpipAdapter", projectName, wizardMenu, path)
    makeSubCMake("util", projectName, wizardMenu, path)
}

fun createSdkConfigFile(wizardMenu: MenuWizardData, path: VirtualFile) {
    createSdkConfigFile(wizardMenu.entriesMenu, path)
}

fun createSdkConfigFile(entries: List<ConfigurationEntry>, path: VirtualFile) {
    var subFolder = path.findChild(CONFIG_DIR)
    if (subFolder == null)
        subFolder = path.createChildDirectory(null, CONFIG_DIR)

    val sdkConfig = subFolder.findOrCreateChildData(null, CONFIG_FILE_NAME)
    val configurations = HashMap<String, String>()
    entries.forEach { it.addConfigution(configurations) }
    val builder = StringBuilder()
    builder.appendln("#define CONFIG_TOOLPREFIX \"xtensa-lx106-elf-\"")
    builder.appendln("#define CONFIG_TARGET_PLATFORM_ESP8266 1")
    builder.appendln("#define CONFIG_APP_OFFSET  0x1000")
    configurations.forEach {
        builder.append("#define CONFIG_").append(it.key).append(" ").appendln(it.value)
    }
    ApplicationManager.getApplication().runWriteAction {
        sdkConfig.setBinaryContent(builder.toString().toByteArray())
    }
}

private fun makeSubCMake(subdir: String, projectName: String, wizardMenu: MenuWizardData, path: VirtualFile) {
    var cmakelists = getResourceAsString("templates/espressif/$subdir/CMakeLists.txt")
    val setting = ApplicationManager.getApplication().getComponent(ESP8266SettingsState::class.java, ESP8266SDKSettings.DEFAULT) as ESP8266SettingsState
    cmakelists = cmakelists
            .replace("__{project_name}__", projectName)
            .replace("__{ESPRESSIF_RTOS_DIR}__", "set(RTOS_DIR ${setting.espressifRtosPath})")
            .replace("__{flash_mode}__", wizardMenu.flashMode)
            .replace("__{flash_freq}__", wizardMenu.flashFreq)
            .replace("__{flash_size}__", wizardMenu.flashSize)

    val subFolder = path.createChildDirectory(null, subdir)
    val cmakeFile = subFolder.findOrCreateChildData(null, "CMakeLists.txt")
    ApplicationManager.getApplication().runWriteAction {
        cmakeFile.setBinaryContent(cmakelists.toByteArray(Charsets.UTF_8))
    }
}
