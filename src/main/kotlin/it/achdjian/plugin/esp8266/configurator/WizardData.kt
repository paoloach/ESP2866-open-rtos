package it.achdjian.plugin.esp8266.configurator

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import it.achdjian.plugin.esp8266.settings.ESP8266SDKSettings
import it.achdjian.plugin.esp8266.settings.ESP8266SettingsState
import it.achdjian.plugin.esp8266.actions.Settings
import it.achdjian.plugin.esp8266.entry_type.*
import it.achdjian.plugin.esp8266.entry_type.ConfigElements.configElements
import it.achdjian.plugin.esp8266.settings.getESP8266Setting
import it.achdjian.plugin.espparser.*
import java.io.File

class WizardData() {
    lateinit var entries: List<ConfigurationEntry>
    lateinit private var configList: List<EspressifConfig>
    var error = ""

    companion object {
        private val LOG = Logger.getInstance(Settings::class.java)
    }

    init {
        updateEntries()
    }

    fun updateEntries() {
        val state =getESP8266Setting()

        val idfPath = File(state.espressifRtosPath)
        val kConfig = File(idfPath, "Kconfig")
        val componentDir = File(idfPath, "components")
        val componentKConfig = componentDir.walk().filter { file -> file.name == "Kconfig" }.toList()
        val componentKConfigProjbuild = componentDir.walk().filter { file -> file.name == "Kconfig.projbuild" }.toList()
        val sourcesList = mapOf(
            "COMPONENT_KCONFIGS" to componentKConfig.toList(),
            "COMPONENT_KCONFIGS_PROJBUILD" to componentKConfigProjbuild.toList()
        )

        try {
            val mainMenu = MainMenu(kConfig.readLines(), sourcesList, ReadFile())
            configList = createConfigList(mainMenu)
            entries = createMenuEntries(mainMenu, configList)

        } catch (e: Exception){
            error = e.message?:"an exception of type ${e.javaClass.name}"
            LOG.error(e)
        }

    }
}

fun createMenuEntries(
    mainMenu: MainMenu,
    configList: List<EspressifConfig>
): List<ConfigurationEntry> {

    configElements = createConfigElements(configList)

    configList.forEach { espressifConfig ->
        espressifConfig.select.forEach {
            configElements[it] ?.let {configEntry->
                if (configEntry is BoolConfigEntry){
                    configEntry.forceTrueBy.add(SimpleExpression(espressifConfig.internalName))
                }
            }
        }
    }

    return mainMenu.menuElements.map {
        if (it.value is EspressifMenu) {
            val espressifMenu = it.value as EspressifMenu
            val list = createSubList(espressifMenu)
            SubMenuEntry(it.key, espressifMenu.visibleIf, list)
        } else {
            createElement(it.value)
        }
    }
}

fun createConfigElements(configList: List<EspressifConfig>): Map<String, SdkConfigEntry> {
    return configList.map { it.name to createConfigElement(it) }.toMap()
}

private fun createConfigList(mainMenu: MainMenu): List<EspressifConfig> {
    val list = mutableListOf<EspressifConfig>()

    mainMenu.menuElements.forEach {
        list.addAll(it.value.configs)
    }

    return list
}

private fun createSubList(
    menu: EspressifMenu
): MutableList<ConfigurationEntry> {
    val elementList = menu.elements.map { createElement(it) }
    val list = menu.subMenus.map { createElement(it) }.toMutableList()
    list.addAll(elementList)
    return list
}

private fun createElement(
    espressifElement: EspressifMenuElement
): ConfigurationEntry {
    when (espressifElement) {
        is EspressifMenuConfig -> {
            val subList = espressifElement.elements.map { createElement(it) }
            val subMenu =
                SubMenuConfigEntry(espressifElement.name, espressifElement.text, subList, espressifElement.multiDefault)
            subMenu.dependsOn = joinDependsOn(espressifElement.dependsOn)
            return subMenu
        }
        is EspressifConfig -> {
            configElements[espressifElement.name]?.let {
                it.dependsOn = joinDependsOn(espressifElement.dependsOn)
                return it
            } ?: throw RuntimeException("Unknow type: ${espressifElement::class}")
        }
        is EspressifChoice -> {
            val choices = espressifElement.listConfig.map { configElements[it.name] as BoolConfigEntry }

            val choice = ChoiceConfigEntry(
                espressifElement.prompt,
                espressifElement.name,
                espressifElement.help,
                choices,
                espressifElement.defaultList.map { Value(SimpleExpression(it.value), it.condition) }.toList(),
                joinDependsOn(espressifElement.dependsOn)
            )

            return choice
        }
        is EspressifMenu -> {
            val subList = espressifElement.elements.map { createElement(it) }.toMutableList()
            subList.addAll(espressifElement.subMenus.map { createElement(it) })
            val subMenu = SubMenuEntry(espressifElement.name, espressifElement.visibleIf, subList)
            subMenu.dependsOn = joinDependsOn(espressifElement.dependsOn)

            subList.forEach {
                it.dependsOn = AndOper(subMenu.dependsOn, it.dependsOn)
            }

            return subMenu
        }
        else -> throw RuntimeException("Invalid menu element: ${espressifElement.name}")
    }
}


private fun joinDependsOn(expressions: Set<Expression>): Expression {
    if (expressions.isEmpty())
        return emptyExpression
    else if (expressions.size == 1)
        return expressions.first()
    else {
        var expr = expressions.first()
        expressions.drop(1).forEach {
            expr = AndOper(expr, it)
        }
        return expr
    }
}


private fun createConfigElement(config: EspressifConfig): SdkConfigEntry {
    val sdkConfig = when (config.type) {
        ConfigType.Undefined ->
            throw RuntimeException("undefine type for config ${config.name}")
        ConfigType.String ->
            StringConfigEntry(config.text, config.name, config.help, config.multiDefault)
        ConfigType.Int ->
            IntConfigEntry(config.text, config.name, config.help, config.multiDefault, config.min, config.max)
        ConfigType.Boolean ->
            BoolConfigEntry(config.text, config.name, config.help, config.multiDefault)
        ConfigType.Hex ->
            HexConfigEntry(config.text, config.name, config.help, config.multiDefault, config.min, config.max)
    }

    sdkConfig.dependsOn = joinDependsOn(config.dependsOn)
    sdkConfig.promptIf = joinDependsOn(config.promptIf)

    return sdkConfig
}

