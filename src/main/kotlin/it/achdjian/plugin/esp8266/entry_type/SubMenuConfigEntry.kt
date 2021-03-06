package it.achdjian.plugin.esp8266.entry_type

import it.achdjian.plugin.espparser.SimpleExpression

class SubMenuConfigEntry(
    val name: String,
    text: String,
    override val subMenu: List<ConfigurationEntry>,
    private var values: List<Value>
) :
    ConfigurationEntry(text, ""), MenuEntry {
    private val listeners = mutableListOf<(value: Boolean) -> Unit>()

    var value: Boolean
        get() {
            if (!enabled) {
                return false
            }
            values.forEach {
                if (eval(it.condition)) {
                    return eval(it.value)
                }
            }
            return false
        }
        set(newVal) {
            values = listOf(Value(SimpleExpression(newVal.toString())))
            listeners.forEach { it(newVal) }
        }

    override fun addConfiguration(configurations: MutableList<Pair<String, String>>) {
        if (value)
            configurations.add(Pair(name, "1"))
        subMenu.forEach {
            it.addConfiguration(configurations)
        }
    }

    override fun set(key: String, newValue: String) = subMenu.forEach { it.set(key, newValue) }
}