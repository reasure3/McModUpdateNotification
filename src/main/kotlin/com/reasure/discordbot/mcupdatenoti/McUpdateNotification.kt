package com.reasure.discordbot.mcupdatenoti

import com.google.gson.JsonElement
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.minn.jda.ktx.jdabuilder.light
import dev.minn.jda.ktx.messages.EmbedBuilder
import dev.minn.jda.ktx.messages.InlineEmbed
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.minutes

fun main(args: Array<String>) {
    val config = dotenv()
    val token = config["DISCORD_TOKEN"] ?: ""
    val key = config["CURSEFORGE_API_KEY"] ?: ""

    if (token.isEmpty()) {
        println("No Discord Token")
        exitProcess(1)
    }
    if (key.isEmpty()) {
        println("No Curseforge Api Key")
        exitProcess(1)
    }

    // 봇 생성
    val bot = light(token) { setActivity(Activity.watching("CurseForge")) }
    bot.listener<ReadyEvent> { println("API is ready!") }

    // cursefore api 생성
    val curseforge = CurseForgeGetter(key)

    // 커맨드 등록
    bot.updateCommands {
        slash("ping", "현재 봇의 핑을 계산한다.")
        slash("mods", "미리 설정된 모드들의 최신 파일을 보여준다.") {
            option<String>("version", "특정 마크 버전의 파일만 불러온다. (all: 모든 버전, 기본값: all)")
            option<Int>("modloader", "특정 모드로더용 파일만 불러온다. (기본값: forge)") {
                choice("forge", ModLoader.FORGE.code.toLong())
                choice("fabric", ModLoader.FABRIC.code.toLong())
                choice("all", ModLoader.ALL.code.toLong())
            }
            option<Int>("release_type", "특정 릴리즈 타입의 파일만 불러온다. (기본값: release)") {
                choice("release", ReleaseType.RELEASE.type.toLong())
                choice("beta", ReleaseType.BETA.type.toLong())
                choice("alpha", ReleaseType.ALPHA.type.toLong())
                choice("all", ReleaseType.ALL.type.toLong())
            }
        }
        slash("jei", "jei의 최신 파일을 보여준다.") {
            option<String>("version", "특정 마크 버전의 파일만 불러온다. (all: 모든 버전, 기본값: all)")
            option<Int>("modloader", "특정 모드로더용 파일만 불러온다. (기본값: forge)") {
                choice("forge", ModLoader.FORGE.code.toLong())
                choice("fabric", ModLoader.FABRIC.code.toLong())
                choice("all", ModLoader.ALL.code.toLong())
            }
            option<Int>("release_type", "특정 릴리즈 타입의 파일만 불러온다. (기본값: release)") {
                choice("release", ReleaseType.RELEASE.type.toLong())
                choice("beta", ReleaseType.BETA.type.toLong())
                choice("alpha", ReleaseType.ALPHA.type.toLong())
                choice("all", ReleaseType.ALL.type.toLong())
            }
        }
        slash("top", "top의 최신 파일을 보여준다.") {
            option<String>("version", "특정 마크 버전의 파일만 불러온다. (all: 모든 버전, 기본값: all)")
            option<Int>("modloader", "특정 모드로더용 파일만 불러온다. (기본값: forge)") {
                choice("forge", ModLoader.FORGE.code.toLong())
                choice("fabric", ModLoader.FABRIC.code.toLong())
                choice("all", ModLoader.ALL.code.toLong())
            }
            option<Int>("release_type", "특정 릴리즈 타입의 파일만 불러온다. (기본값: release)") {
                choice("release", ReleaseType.RELEASE.type.toLong())
                choice("beta", ReleaseType.BETA.type.toLong())
                choice("alpha", ReleaseType.ALPHA.type.toLong())
                choice("all", ReleaseType.ALL.type.toLong())
            }
        }
        slash("patchouli", "patchouli의 최신 파일을 보여준다.") {
            option<String>("version", "특정 마크 버전의 파일만 불러온다. (all: 모든 버전, 기본값: all)")
            option<Int>("modloader", "특정 모드로더용 파일만 불러온다. (기본값: forge)") {
                choice("forge", ModLoader.FORGE.code.toLong())
                choice("fabric", ModLoader.FABRIC.code.toLong())
                choice("all", ModLoader.ALL.code.toLong())
            }
            option<Int>("release_type", "특정 릴리즈 타입의 파일만 불러온다. (기본값: release)") {
                choice("release", ReleaseType.RELEASE.type.toLong())
                choice("beta", ReleaseType.BETA.type.toLong())
                choice("alpha", ReleaseType.ALPHA.type.toLong())
                choice("all", ReleaseType.ALL.type.toLong())
            }
        }
        slash("mod", "해당 모드의 최신 파일을 보여준다.") {
            option<Int>("modid", "모드 아이디는 해당 모드의 커스포지 사이트에서 왼쪽 상단 Project ID에서 확인 가능", required = true)
            option<String>("version", "특정 마크 버전의 파일만 불러온다. (all: 모든 버전, 기본값: all)")
            option<Int>("modloader", "특정 모드로더용 파일만 불러온다. (기본값: forge)") {
                choice("forge", ModLoader.FORGE.code.toLong())
                choice("fabric", ModLoader.FABRIC.code.toLong())
                choice("all", ModLoader.ALL.code.toLong())
            }
            option<Int>("release_type", "특정 릴리즈 타입의 파일만 불러온다. (기본값: release)") {
                choice("release", ReleaseType.RELEASE.type.toLong())
                choice("beta", ReleaseType.BETA.type.toLong())
                choice("alpha", ReleaseType.ALPHA.type.toLong())
                choice("all", ReleaseType.ALL.type.toLong())
            }
        }
    }.queue()

    // ping 커맨드 동작
    bot.onCommand("ping", 2.minutes) { event ->
        val time = System.currentTimeMillis()
        event.reply("Pong: 계산 중...").setEphemeral(true)
            .flatMap { event.hook.editOriginalFormat("Pong: ${System.currentTimeMillis() - time} ms") }.queue()
    }

    bot.onCommand("mods", timeout = 3.minutes) { event ->
        val gameVersion = event.getOption("version")?.asString ?: "all"
        val loader = ModLoader.get(event, ModLoader.FORGE)
        val type = ReleaseType.get(event, ReleaseType.RELEASE)
        val jei = modEmbed(curseforge.getModInfo(238222), gameVersion, loader, type)
        val top = modEmbed(curseforge.getModInfo(245211), gameVersion, loader, type)
        val patchouli = modEmbed(curseforge.getModInfo(306770), gameVersion, loader, type)
        event.replyEmbeds(jei.build(), top.build(), patchouli.build()).queue()
    }

    bot.onCommand("jei", timeout = 3.minutes) { event ->
        val gameVersion = event.getOption("version")?.asString ?: "all"
        val loader = ModLoader.get(event, ModLoader.FORGE)
        val type = ReleaseType.get(event, ReleaseType.RELEASE)
        val jei = modEmbed(curseforge.getModInfo(238222), gameVersion, loader, type)
        event.replyEmbeds(jei.build()).queue()
    }

    bot.onCommand("top", timeout = 3.minutes) { event ->
        val gameVersion = event.getOption("version")?.asString ?: "all"
        val loader = ModLoader.get(event, ModLoader.FORGE)
        val type = ReleaseType.get(event, ReleaseType.RELEASE)
        val top = modEmbed(curseforge.getModInfo(245211), gameVersion, loader, type)
        event.replyEmbeds(top.build()).queue()
    }

    bot.onCommand("patchouli", timeout = 3.minutes) { event ->
        val gameVersion = event.getOption("version")?.asString ?: "all"
        val loader = ModLoader.get(event, ModLoader.FORGE)
        val type = ReleaseType.get(event, ReleaseType.RELEASE)
        val patchouli = modEmbed(curseforge.getModInfo(306770), gameVersion, loader, type)
        event.replyEmbeds(patchouli.build()).queue()
    }

    bot.onCommand("mod", timeout = 3.minutes) { event ->
        val modid = event.getOption("modid")?.asInt ?: 0
        val gameVersion = event.getOption("version")?.asString ?: "all"
        val loader = ModLoader.get(event, ModLoader.FORGE)
        val type = ReleaseType.get(event, ReleaseType.RELEASE)
        val mod = modEmbed(curseforge.getModInfo(modid), gameVersion, loader, type)
        event.replyEmbeds(mod.build()).queue()
    }
}

fun modEmbed(modInfo: ModInfo, gameVersionFilter: String, loaderFilter: ModLoader, typeFilter: ReleaseType) =
    EmbedBuilder {
        title = modInfo.name
        url = modInfo.link
        description = modInfo.summary
        color = modInfo.id

        if (modInfo.lastFiles != null) {
            field(inline = false) // add blank field

            val releaseFiles = mutableListOf<InlineEmbed.InlineField>()
            val betaFiles = mutableListOf<InlineEmbed.InlineField>()
            val alphaFiles = mutableListOf<InlineEmbed.InlineField>()

            for (file in modInfo.lastFiles) {
                val fileJson = file.asJsonObject

                // 게임 버전 필터 적용
                val gotGameVersion = fileJson.get("gameVersion")?.asString ?: ""
                if (!compareVersion(gameVersionFilter, gotGameVersion)) continue

                // 모드 로더 필터 적용
                val gotLoader = ModLoader[fileJson.get("modLoader")?.asIntOrNull() ?: -1]
                if (gotLoader != ModLoader.UNKNOWN) {
                    if (loaderFilter != ModLoader.ALL && gotLoader != loaderFilter) continue
                }

                val gotType = ReleaseType[fileJson.get("releaseType")?.asIntOrNull() ?: -1]

                // 릴리즈 타입 필터 적용
                if (typeFilter.contain(gotType)) {
                    val fileId = fileJson.get("fileId")?.asIntOrNull() ?: 0
                    val fileField = InlineEmbed.InlineField(
                        name = fileJson.get("filename")?.asString ?: "???",
                        value = "[${gotType.name}](${modInfo.link}/files/${fileId})",
                    )
                    // 릴리즈 타입 분류해서 집어넣기
                    when (gotType) {
                        ReleaseType.RELEASE -> releaseFiles.add(fileField)
                        ReleaseType.BETA -> betaFiles.add(fileField)
                        ReleaseType.ALPHA -> alphaFiles.add(fileField)
                        else -> {}
                    }
                }
            }

            // 필터가 ALL이면 전부 추가
            if (typeFilter == ReleaseType.ALL) {
                if (releaseFiles.isEmpty() && betaFiles.isEmpty() && alphaFiles.isEmpty()) {
                    field {
                        name = "There is no mod files."
                        value = ":("
                    }
                }
                releaseFiles.forEach { f -> builder.addField(f.name, f.value, true) }
                betaFiles.forEach { f -> builder.addField(f.name, f.value, true) }
                alphaFiles.forEach { f -> builder.addField(f.name, f.value, true) }
            } else { // 아니면 해당 필터 파일 필드 추가하기 (예를 들어 필터가 release 추가하고, 만약 release가 없으면 beta 추가)
                if (releaseFiles.isNotEmpty()) {
                    releaseFiles.forEach { f -> builder.addField(f.name, f.value, true) }
                } else if (betaFiles.isNotEmpty()) {
                    betaFiles.forEach { f -> builder.addField(f.name, f.value, true) }
                } else {
                    alphaFiles.forEach { f -> builder.addField(f.name, f.value, true) }
                    if (alphaFiles.isEmpty()) {
                        field {
                            name = "There is no mod files for $gameVersionFilter"
                            value = ":("
                        }
                    }
                }
            }
        }
    }

// 1.18.1이면 1.18.1만 허용.
// 1.18이면 1.18, 1.18.1, 1.18.2 허용
fun compareVersion(original: String, compare: String): Boolean {
    if (original.isEmpty() || original == "all") return true
    val splitVersion = original.split('.')
    if (splitVersion.size == 2) {
        val splitCompare = compare.split('.')
        if (splitCompare.size == 2) return original == compare
        if (splitCompare.size == 3)
            return splitVersion[0] == splitCompare[0] && splitVersion[1] == splitCompare[1]
        return false
    }
    return original == compare
}

fun JsonElement.asIntOrNull(): Int? {
    if (isJsonNull) return null
    return asInt
}

enum class ModLoader(val code: Int) {
    ALL(0), FORGE(1), FABRIC(4), UNKNOWN(-1);

    companion object {
        private val map = values().associateBy(ModLoader::code)
        operator fun get(value: Int) = map[value] ?: UNKNOWN
        fun get(event: GenericCommandInteractionEvent, default: ModLoader = ALL) =
            ModLoader[event.getOption("modloader")?.asInt ?: default.code]
    }
}

enum class ReleaseType(val type: Int) {
    ALL(0), RELEASE(1), BETA(2), ALPHA(3), UNKNOWN(-1);

    fun contain(type: ReleaseType) = when (this) {
        ALL -> true // ALL, RELEASE, BETA, ALPHA, UNKNOWN
        RELEASE -> type != UNKNOWN // ALL, RELEASE, BETA, ALPHA
        BETA -> type != RELEASE && type != UNKNOWN // ALL, BETA, ALPHA
        ALPHA -> type == ALL || type == ALPHA // ALL, ALPHA
        UNKNOWN -> false // Not of all
    }

    companion object {
        private val map = ReleaseType.values().associateBy(ReleaseType::type)
        operator fun get(value: Int) = map[value] ?: UNKNOWN
        fun get(event: GenericCommandInteractionEvent, default: ReleaseType = ALL) =
            ReleaseType[event.getOption("release_type")?.asInt ?: default.type]
    }
}