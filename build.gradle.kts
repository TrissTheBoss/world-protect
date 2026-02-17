plugins {
    id("java")
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
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    
    // Configuration library
    implementation("dev.dejvokep:boosted-yaml:1.3.4")
    
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testCompileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
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
                "pluginName" to rootProject.name,
                "pluginVersion" to project.version,
                "pluginDescription" to project.description,
                "pluginAuthor" to "TrissTheBoss",
                "pluginWebsite" to "https://github.com/TrissTheBoss/world-protect"
            )
        }
    }
    
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("WorldProtect")
        archiveVersion.set("${project.version}")
        
        // Disable minimization as it may cause issues with Java 21
        // minimize()
        
        // Configure for Java 21 compatibility
        manifest {
            attributes(
                "Multi-Release" to "true"
            )
        }
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
}