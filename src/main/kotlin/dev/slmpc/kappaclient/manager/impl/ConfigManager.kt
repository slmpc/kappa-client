package dev.slmpc.kappaclient.manager.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import dev.slmpc.kappaclient.Kappa
import dev.slmpc.kappaclient.event.eventListener
import dev.slmpc.kappaclient.event.impl.CloseGameEvent
import dev.slmpc.kappaclient.helper.LoggerHelper
import dev.slmpc.kappaclient.manager.AbstractManager
import dev.slmpc.kappaclient.module.Module
import dev.slmpc.kappaclient.settings.*
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

object ConfigManager: AbstractManager() {

    private val path = File(Kappa.NAME)

    override suspend fun load() {
        initModules()
        initFriend()
        initCommand()
    }

    init {
        eventListener<CloseGameEvent> {
            unload()
        }
    }

    private fun unload() {
        saveAll()
    }

    private fun initModules() {
        if (!path.exists()) path.mkdirs()
        for (mod in ModuleManager.modules()) {
            val modPath = getModulePath(mod)
            if (!modPath.exists()) {
                saveModuleConfig(mod)
            } else {
                loadModule(mod)
            }
        }
    }

    private fun initFriend() {
        if (!path.exists()) {
            path.mkdirs()
        }
        val friendFile = File(path, "Friends.json")
        if (!friendFile.exists()) {
            friendFile.parentFile.mkdirs()
            friendFile.createNewFile()
        } else {
            val friendJson = Gson().fromJson(
                String(Files.readAllBytes(friendFile.toPath()), StandardCharsets.UTF_8),
                ArrayList::class.java
            ) ?: return
            FriendManager.friends.clear()
            friendJson.forEach {
                FriendManager.friends.add(it.toString())
            }
        }
    }

    private fun initCommand() {
        if (!path.exists()) {
            path.mkdirs()
        }
        val commandFile = File(path, "Prefix.txt")
        if (!commandFile.exists()) {
            commandFile.parentFile.mkdirs()
            commandFile.createNewFile()
            return
        }
        val prefix = commandFile.readText()
        if (prefix.isEmpty()) return
        CommandManager.setPrefix(prefix[0])
    }

    private fun saveCommand() {
        if (!path.exists()) {
            path.mkdirs()
        }
        val commandFile = File(path, "Prefix.txt")
        if (!commandFile.exists()) {
            commandFile.parentFile.mkdirs()
            commandFile.createNewFile()
        }
        commandFile.writeText(CommandManager.getPrefix().toString())
    }

    private fun saveFriends() {
        if (!path.exists()) {
            path.mkdirs()
        }
        val friendFile = File(path, "Friends.json")
        if (!friendFile.exists()) {
            friendFile.parentFile.mkdirs()
            friendFile.createNewFile()
        }
        Files.write(
            friendFile.toPath(), Gson().toJson(FriendManager.friends).toByteArray(
                StandardCharsets.UTF_8
            )
        )
    }

    private fun loadFriends() {
        val friendFile = File(path, "Friends.json")
        if (!friendFile.exists()) {
            return
        }
        val friendJson = Gson().fromJson(
            String(Files.readAllBytes(friendFile.toPath()), StandardCharsets.UTF_8),
            ArrayList::class.java
        ) ?: return
        FriendManager.friends.clear()
        friendJson.forEach {
            FriendManager.friends.add(it.toString())
        }
    }

    private fun saveModuleConfig(mod: Module) {
        val modPath = getModulePath(mod)
        if (!modPath.exists()) {
            modPath.parentFile.mkdirs()
            modPath.createNewFile()
        }
        modPath.parentFile.mkdirs()
        modPath.createNewFile()
        val moduleJson = JsonObject()
        moduleJson.addProperty("Name", mod.nameAsString())
        moduleJson.addProperty("Toggle", mod.isEnabled)
        if (mod.settings.isNotEmpty()) {
            val settingObject = JsonObject()
            for (setting in mod.settings) {
                saveSetting(setting, settingObject)
            }
            moduleJson.add("Settings", settingObject)
        }
        Files.write(
            modPath.toPath(), GsonBuilder().setPrettyPrinting().create().toJson(moduleJson).toByteArray(
                StandardCharsets.UTF_8
            )
        )
    }

    private fun loadModule(mod: Module) {
        val modPath = getModulePath(mod)
        if (!modPath.exists()) return
        val moduleJson = Gson().fromJson(
            String(Files.readAllBytes(modPath.toPath()), StandardCharsets.UTF_8),
            JsonObject::class.java
        ) ?: return
        try {
            val modName = moduleJson.get("Name").asString
            for (m in ModuleManager.modules()) {
                if (m.name == modName) {
                    val toggle = moduleJson.get("Toggle").asBoolean
                    if (toggle) mod.enable() else mod.disable()
                }
            }
        } catch (e: NullPointerException) {
            LoggerHelper.logInfo("Loading config failed! Please delete \"${Kappa.NAME}\" folder and retry")
        }

        val element = moduleJson.get("Settings") ?: return
        val settingsJson = element.asJsonObject
        if (settingsJson != null) {
            for (setting in mod.settings) {
                setSetting(setting, settingsJson)
            }
        }
    }

    private fun saveAllModules() {
        for (mod in ModuleManager.modules()) {
            saveModuleConfig(mod)
        }
    }


    private fun loadAllModules() {
        for (mod in ModuleManager.modules()) {
            loadModule(mod)
        }
    }

    private fun saveSetting(setting: AbstractSetting<*>, jsonObject: JsonObject): JsonObject {
        when (setting) {
            is BooleanSetting -> jsonObject.addProperty(setting.name.toString(), setting.value)
            is KeyBindSetting -> jsonObject.addProperty(setting.name.toString(), setting.value.keyCode)
            is FloatSetting -> jsonObject.addProperty(setting.name.toString(), setting.value)
            is DoubleSetting -> jsonObject.addProperty(setting.name.toString(), setting.value)
            is IntSetting -> jsonObject.addProperty(setting.name.toString(), setting.value)
            is LongSetting -> jsonObject.addProperty(setting.name.toString(), setting.value)
            is EnumSetting<*> -> jsonObject.addProperty(setting.name.toString(), setting.value.name)
            is TextSetting -> jsonObject.addProperty(setting.name.toString(), setting.value)
        }
        return jsonObject
    }

    private fun setSetting(setting: AbstractSetting<*>, jsonObject: JsonObject) {
        if (jsonObject.has(setting.name.toString())) {
            when (setting) {
                is BooleanSetting -> setting.value = jsonObject.get(setting.name.toString()).asBoolean
                is KeyBindSetting -> setting.value = KeyBind(KeyBind.Type.KEYBOARD, jsonObject.get(setting.name.toString()).asInt)
                is FloatSetting -> setting.value = jsonObject.get(setting.name.toString()).asFloat
                is DoubleSetting -> setting.value = jsonObject.get(setting.name.toString()).asDouble
                is IntSetting -> setting.value = jsonObject.get(setting.name.toString()).asInt
                is LongSetting -> setting.value = jsonObject.get(setting.name.toString()).asLong
                is TextSetting -> setting.value = jsonObject.get(setting.name.toString()).asString
                is EnumSetting<*> -> setting.setWithName(jsonObject.get(setting.name.toString()).asString)
            }
        }
    }

    private fun getModulePath(module: Module): File {
        return File("$path/modules/${module.category.displayName}/${module.name}.json")
    }

    fun saveAll() {
        saveAllModules()
        saveFriends()
        saveCommand()
    }

    fun loadAll() {
        loadAllModules()
        loadFriends()
    }


}