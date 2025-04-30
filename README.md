![Banner](https://i.imgur.com/2feJFS9.png)
<p align="center">
    <a href="https://www.teamabnormals.com/" align="center"><img alt="Team Abnormals Website" src="http://bit.ly/abnormalswebbadge"></a>
    <a href="https://www.teamabnormals.com/discord" align="center"><img alt="Team Abnormals Discord" src="https://img.shields.io/discord/650003402218274816?label=&color=014980&labelColor=537DB5&style=for-the-badge&logo=Discord&logoColor=DDE4EF"></a>
    <a href="https://twitter.com/TeamAbnormals" align="center"><img alt="Team Abnormals Twitter" src="https://img.shields.io/static/v1?label=&message=Follow&color=014980&labelColor=537DB5&style=for-the-badge&logo=X&logoColor=DDE4EF"></a>
    <a href="https://www.patreon.com/teamabnormals" align="center"><img alt="Team Abnormals Patreon" src="https://img.shields.io/endpoint?label=&color=014980&labelColor=537DB5&style=for-the-badge&logo=Patreon&logoColor=DDE4EF&url=https://shieldsio-patreon.vercel.app/api/?username=teamabnormals&type=patrons"></a>
</p>

![Border](https://i.imgur.com/U7uo5Va.png)

**📖 About:**

Blueprint is a mod library developed for easily accessing code shared across most Team Abnormals mods, but anyone is allowed and encouraged to use it! It has many valuable features, such as a registry helper, data syncing, various data-driven modification systems, a biome API, a trim material API, the Endimator animation API, and much more!

![Border](https://i.imgur.com/U7uo5Va.png)

**💻 For Developers:**
Adding Blueprint to your mod is quite simple!

First off you need to add Blueprint as a dependency to access the library in code. To do so, add the following into your `build.gradle`:
```
repositories {
    maven {
        url = "https://maven.teamabnormals.com"
    }
}

dependencies {
    implementation("com.teamabnormals:blueprint:<version>")
}
```
Replace `<version>` with the desired version of Blueprint, including the desired version of Minecraft.<br />
For example, `1.21.1-8.0.0` will give us `blueprint-1.21.1-8.0.0.jar`.

Next, you must add it as a dependency on NeoForge to make your mod require Blueprint when loading. In your `neoforge.mods.toml`, add the following block to the file:
```
[[dependencies.<modId>]]
    modId = "blueprint"
    type = "required"
    versionRange = "[<version>,)"
    ordering = "AFTER"
    side = "BOTH"
```
Replace `<version>` with the desired version of Blueprint.
For example, `8.0.0` will target version `8.0.0` of Blueprint.
The code block above for the `neoforge.mods.toml` is targeting the version selected and any versions beyond. If you want to target it differently, you may want to read up on the `neoforge.mods.toml` spec.

![Border](https://i.imgur.com/U7uo5Va.png)

**📦 Major Mods by Team Abnormals:**
*   [Atmospheric](https://modrinth.com/mod/atmospheric)
*   [Autumnity](https://modrinth.com/mod/autumnity)
*   [Buzzier Bees](https://modrinth.com/mod/buzzier-bees)
*   [Caverns and Chasms](https://modrinth.com/mod/caverns-and-chasms)
*   [Endergetic Expansion](https://modrinth.com/mod/endergetic)
*   [Environmental](https://modrinth.com/mod/environmental)
*   [Neapolitan](https://modrinth.com/mod/neapolitan)
*   [Savage and Ravage](https://modrinth.com/mod/savage-and-ravage)
*   [Upgrade Aquatic](https://modrinth.com/mod/upgrade-aquatic)

**🗃️ Minor Mods by Team Abnormals:**
*   [Abnormals Delight](https://modrinth.com/mod/abnormals-delight)
*   [Allurement](https://modrinth.com/mod/allurement!)
*   [Berry Good](https://modrinth.com/mod/berry-good)
*   [Boatload](https://modrinth.com/mod/boatload)
*   [Clayworks](https://modrinth.com/mod/clayworks)
*   [Gallery](https://modrinth.com/mod/gallery)
*   [Incubation](https://modrinth.com/mod/incubation)
*   [Personality](https://modrinth.com/mod/personality!)
*   [Pet Cemetery](https://modrinth.com/mod/pet-cemetery)
*   [Woodworks](https://modrinth.com/mod/woodworks)

![Border](https://i.imgur.com/U7uo5Va.png)

![Nodecraft](https://i.imgur.com/oZS1R9g.png)
