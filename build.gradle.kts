plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.worldprotect"
version = "0.1.0"
description = "Advanced world protection plugin for Paper/Folia"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    
    // Configuration library (optional but recommended)
    implementation("dev.dejvokep:boosted-yaml:1.3.4")
    
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.5.0")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "version" to project.version,
                "description" to project.description
            )
        }
    }
    
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("WorldProtect")
        archiveVersion.set("${project.version}")
        
        // Relocate dependencies if needed
        // relocate("dev.dejvokep.boostedyaml", "com.worldprotect.lib.boostedyaml")
        
        minimize()
    }
    
    assemble {
        dependsOn(shadowJar)
    }
    
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    
    runServer {
        minecraftVersion("1.21.11")
    }
}

// Generate sources JAR
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

// Generate javadoc JAR
tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

// Artifacts configuration
artifacts {
    archives(tasks.shadowJar)
    archives(tasks.named("sourcesJar"))
    archives(tasks.named("javadocJar"))
}