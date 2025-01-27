import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.0"
}

val v = "0.0.1"

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
           <head>
    <meta charset="UTF-8">
    <meta name="viewport", initial-scale=1.0">
    <title>Code Exploder</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            margin: 20px;
            line-height: 1.6;
        }
        h1 {
            color: #ff5722;
            text-align: center;
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        h2 {
            color: #333;
            font-size: 1.8em;
            margin-top: 20px;
        }
        p {
            font-size: 1.1em;
        }
        .highlight {
            color: #ff5722;
            font-weight: bold;
        }
        .warning {
            background-color: #fff3e0;
            padding: 10px;
            border-left: 5px solid #ff9800;
            margin: 20px 0;
            font-size: 1em;
        }
    </style>
</head>
<body>
    <h1>Code Exploder</h1>
    <p>Ever stared at your code so long that you just wanted to blow it up? 
    Well, now you can! Code Exploder is the ultimate stress-relief plugin for IntelliJ IDEA. 
    When your code is driving you up the wall, simply hit <span class="highlight">Ctrl + B</span> and watch it <span class="highlight">explode into glorious chaos</span>! 
    Letters, symbols, and brackets fly everywhere in a satisfying burst of digital destruction, all centered around your cursor like a mini coding apocalypse.</p>

    <p>But don’t worry—your code isn’t gone for good. After the explosion, the pieces gently float back into place, reassembling themselves like nothing ever happened. 
    It’s the perfect way to vent your frustration without actually breaking anything (or anyone).</p>

    <h2>Key Features:</h2>
    <ul>
        <li><span class="highlight">Blow Stuff Up:</span> Smash your code into pieces with a single keystroke. You can finally take revenge on that pesky recursive function!</li>
        <li><span class="highlight">Cursor-Ground-Zero:</span> The explosion starts wherever your cursor is, so you can target that one annoying line of code.</li>
        <li><span class="highlight">Did I mention that you can blow stuff up? Because whith this you can!</li>
    </ul>

    <div class="warning">
        <strong>Warning:</strong> This will change your code structure. Don't use if you can't revert back (with Git or ctrl + z, or something). Use responsibly (or not).
    </div>

    <p>Blow up your code. Take revenge! <span class="highlight">💥</span></p>
</body>
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
