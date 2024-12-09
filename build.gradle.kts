import net.fabricmc.loom.configuration.providers.minecraft.mapped.AbstractMappedMinecraftProvider.RemappedJars
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapTaskConfiguration

plugins {
    id("fabric-loom") version "1.8.10"
    id("maven-publish")
    java
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

loom {
    mods {
        register("redstonesmp") {
            sourceSet("main")
        }
    }
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
    maven("https://jitpack.io")
    maven("https://cursemaven.com")
    maven("https://maven.kyrptonaught.dev")

    maven {
        name = "Ladysnake Mods"
        url = uri("https://maven.ladysnake.org/releases")
    }
    }

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation("eu.pb4:placeholder-api:2.4.1+1.21")
    modImplementation("curse.maven:origins-391943:5777233")

    val ccaVersion = property("cca_version") as String
    modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-base:$ccaVersion")
//    include("org.ladysnake.cardinal-components-api:cardinal-components-base:$ccaVersion")

    modImplementation(files("libs/apoli-2.12.0-alpha.12+mc.1.21.x.jar"))
    modImplementation(files("libs/calio-1.14.0-alpha.7+mc.1.21.x.jar"))
    modImplementation(files("libs/styled-chat-2.6.0+1.21.jar"))
    modImplementation(files("libs/factions-mc1.21-2.6.3.jar"))


    modImplementation("net.kyrptonaught:customportalapi:0.0.1-beta67-1.21")
    include("net.kyrptonaught:customportalapi:0.0.1-beta67-1.21")

    implementation("org.mongodb:mongo-java-driver:${project.property("mongo")}")
    include("org.mongodb:mongo-java-driver:${project.property("mongo")}")

    compileOnly("net.luckperms:api:${project.property("luckperms_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

tasks.register("copyJarToFiles", Copy::class) {
    //remapJar
    from (tasks.named("remapJar"))
    into("C:/Users/griff/Desktop/smp/mods")
}