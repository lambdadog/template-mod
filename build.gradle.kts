import net.minecraftforge.gradle.common.util.MinecraftExtension
import java.text.SimpleDateFormat
import java.util.*

buildscript {
    repositories {
        maven("https://maven.minecraftforge.net")
        maven("https://maven.parchmentmc.org")
        mavenCentral()
    }
    dependencies {
        classpath(
            group = "net.minecraftforge.gradle",
            name = "ForgeGradle",
            version = "5.1.+"
        )
        classpath(
            group = "org.jetbrains.kotlin",
            name = "kotlin-gradle-plugin",
            version = "1.8.21"
        )
        classpath(
            group = "org.parchmentmc",
            name = "librarian",
            version = "1.+"
        )
    }
}

apply(plugin = "kotlin")
apply(plugin = "net.minecraftforge.gradle")
apply(plugin = "org.parchmentmc.librarian.forgegradle")

plugins {
    eclipse
    `maven-publish`
    `java-library`
    kotlin("jvm") version "1.8.21"
}

object ModInfo {
    const val NAME = "templateMod"
    const val ID = "template_mod"
    const val AUTHOR = "pea"
}

version = "0.1.0"
group = "sh.pea.${ModInfo.NAME}" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

println(
    "Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
        "java.vendor"
    ) + ") Arch: " + System.getProperty("os.arch")
)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

val Project.minecraft: MinecraftExtension
    get() = extensions.getByType()

minecraft.run {
    mappings("parchment", "2022.11.06-1.18.2")

    runs.create("client") {
        workingDirectory(project.file("run"))
        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")
        property("forge.enabledGameTestNamespaces", ModInfo.ID)
        this.mods {
            this.create(ModInfo.NAME) {
                this.source(sourceSets.main.get())
            }
        }
    }

    runs.create("server") {
        workingDirectory(project.file("run"))
        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")
        property("forge.enabledGameTestNamespaces", ModInfo.ID)
        this.mods {
            this.create(ModInfo.NAME) {
                this.source(sourceSets.main.get())
            }
        }
    }

    runs.create("gameTestServer") {
        workingDirectory(project.file("run"))
        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")
        property("forge.enabledGameTestNamespaces", ModInfo.ID)
        this.mods {
            this.create(ModInfo.NAME) {
                this.source(sourceSets.main.get())
            }
        }
    }

    runs.create("data") {
        workingDirectory(project.file("run"))
        property("forge.logging.markers", "REGISTRIES")
        property("forge.logging.console.level", "debug")
        args(
            "--mod",
            ModInfo.ID,
            "--all",
            "--output",
            file("src/generated/resources"),
            "--existing",
            file("src/main/resources")
        )
        this.mods {
            this.create(ModInfo.NAME) {
                this.source(sourceSets.main.get())
            }
        }
    }
}

sourceSets.main.configure {
    resources.srcDirs("src/generated/resources/")
}

repositories {
    mavenCentral()
    maven(
        url = "https://thedarkcolour.github.io/KotlinForForge/"
    )
}

dependencies {
    minecraft("net.minecraftforge:forge:1.18.2-40.2.0")

    implementation("thedarkcolour:kotlinforforge:3.12.0")

    // Real mod deobf dependency examples - these get remapped to your current mappings
    // compileOnly fg.deobf("mezz.jei:jei-${mc_version}:${jei_version}:api") // Adds JEI API as a compile dependency
    // runtimeOnly fg.deobf("mezz.jei:jei-${mc_version}:${jei_version}") // Adds the full JEI mod as a runtime dependency
    // implementation fg.deobf("com.tterrag.registrate:Registrate:MC${mc_version}-${registrate_version}") // Adds registrate as a dependency

    // Examples using mod jars from ./libs
    // implementation fg.deobf("blank:coolmod-${mc_version}:${coolmod_version}")

    // For more info...
    // http://www.gradle.org/docs/current/userguide/artifact_dependencies_tutorial.html
    // http://www.gradle.org/docs/current/userguide/dependency_management.html
}

// Example for how to get properties into the manifest for reading at runtime.
tasks.withType<Jar> {
    archiveBaseName.set(ModInfo.NAME)
    manifest {
        val map = HashMap<String, String>()
        map["Specification-Title"] = ModInfo.ID
        map["Specification-Vendor"] = ModInfo.AUTHOR
        map["Specification-Version"] = "1" // We are version 1 of ourselves
        map["Implementation-Title"] = project.name
        map["Implementation-Version"] = archiveBaseName.get()
        map["Implementation-Vendor"] = ModInfo.AUTHOR
        map["Implementation-Timestamp"] = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(map)
    }

    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

fun DependencyHandler.minecraft(
    dependencyNotation: Any
): Dependency = add("minecraft", dependencyNotation)!!
