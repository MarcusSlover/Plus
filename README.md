# 🔮 Plus

<p align="center">
<img src="https://github.com/MarcusSlover/Plus/blob/master/shulker-export.png"/ style="width: 160px;height: 160px; image-rendering: pixelated;">
<br/>
📦 A light set of tools used for programming Minecraft plugins.<br/>
</p>

# 💻 For Developers

This library is designed to work specifically with PaperMC version 1.21.1.<br/>
Running on Java 21. Use with other Minecraft versions at your own risk.

Based on our testing, version 4.2.0 of Plus is the most compatible with older versions of Minecraft.
If you are using a Minecraft version other than 1.21.1, we recommend using Plus version 4.3.1.
For Minecraft 1.21.1 or newer, use the latest version of Plus for optimal compatibility.

## 🐘 Gradle
### Groovy
```gradle
repositories {
    maven { url = 'https://s01.oss.sonatype.org/content/repositories/snapshots/' }
}

dependencies {
    implementation 'com.marcusslover:plus:4.3.1-SNAPSHOT'
}
```
### Kotlin
```kotlin
repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("com.marcusslover:plus:4.3.1-SNAPSHOT")
}
```
## 🦢 Maven
```xml
<repository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
</repository>

<dependency>
    <groupId>com.marcusslover</groupId>
    <artifactId>plus</artifactId>
    <version>4.3.1-SNAPSHOT</version>
</dependency>
```
