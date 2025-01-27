import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.0"
}

val v = "1.0.0"

group = "com.glycin"
version = v

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform  {
    pluginConfiguration {
        id = "intelliboom"
        name = "Code Exploder"
        version = v

        ideaVersion {
            sinceBuild = "232"
            untilBuild = provider { null }
        }

        vendor {
            name = "Glycin"
            url = "https://github.com/glycin"
        }

        description = """
            <h2> Blow up your code! </h2><br>
            💥💥💥 <br>
            Ever stared at your code so long in frustration, that you just wanted to blow it up?<br>
            Well, now you can! Code Exploder is the ultimate stress-relief plugin for IntelliJ IDEA.<br>
            When your code is driving you up the wall, simply hit <b>Ctrl + B</b> and watch it <b>explode into glorious chaos</b>!<br>
            Letters, symbols, and brackets fly everywhere in a satisfying burst of digital destruction, all centered around your cursor like a mini coding apocalypse.<br>
            <br>
            <h2>Key Features:</h2>
            <ul>
                <li><b>Blow Stuff Up:</b> Smash your code into pieces with a single keystroke. You can finally take revenge on that pesky recursive function!</li>
                <li><b>Cursor-Ground-Zero:</b> The explosion starts wherever your cursor is, so you can target that one annoying line of code.</li>
                <li>Did I mention that you can blow stuff up? Because whith this you can!</li>
            </ul>
            <br>
            <strong>Warning:</strong> This will change your code structure. Don't use if you can't revert back (with Git or ctrl + z, or something). Use responsibly (or not).<br>
            <br>
            <b>Blow up your code. Take revenge! <span class="highlight">💥</b>
        """.trimIndent()
    }

    publishing {}

    signing{}
}

dependencies {
    intellijPlatform{
        intellijIdeaCommunity("2024.2.3")
        bundledPlugin("com.intellij.java")
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }

    testImplementation("junit:junit:4.13.2")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

kotlin{
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
