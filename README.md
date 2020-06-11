![Banner](https://i.imgur.com/vCbZh9W.png)
[![Minecraft Abnormals Discord](https://img.shields.io/discord/321736665058115604?label=&color=014980&labelColor=c1c1c1&style=for-the-badge&logo=Discord&logoColor=014980)](https://discord.gg/UejgrBn) [![Minecraft Abnormals Twitter](https://img.shields.io/twitter/follow/mcabnormals?label=&color=014980&labelColor=c1c1c1&style=for-the-badge&logo=Twitter&logoColor=014980)](https://twitter.com/mcabnormals) [![MCA Patreon](https://img.shields.io/endpoint?label=&color=014980&labelColor=c1c1c1&style=for-the-badge&logo=Patreon&logoColor=014980&url=https://shieldsio-patreon.herokuapp.com/minecraftabnormals)](https://www.patreon.com/minecraftabnormals)
![](https://i.imgur.com/5rYQHXf.png)
## **📖 About**
Abnormals Core is a mod library developed for easily accessing code which is shared across most Minecraft Abnormals mods without needing to copy classes across each project. This library is developed by [SmellyModder (Luke Tonon)](https://twitter.com/TononLuke) and [bageldotjpg](https://twitter.com/bageldotjpg). It comes with many useful features, such as an easier way to automate registering blocks, items, etc, and the animation library called Endimator.

![](https://i.imgur.com/5rYQHXf.png)
## **💻 For Developers**
In order to use Abnormals Core in your mod, you need to add it to your `build.gradle`.

### **Curse Maven Plugin**
In order to get the library from Curse, you'll need to add the Curse Maven plugin in your `build.gradle`.

After the `buildscript` section on the top of your `build.gradle`, paste in the following block:

    plugins {
           id "com.wynprice.cursemaven" version "2.1.1"
    }
### **Adding the Dependency**
You'll next need to add Abnormals Core as a dependency on your project and tell Forge to deobfuscate it. In the `dependencies` block paste in the following:

    implementation fg.deobf("curse.maven:abnormals-core:curseFileID")
*The Curse fileId can be found on the end of the link of the file on CurseForge. For example, the fileId of `2954634` would add Abnormals Core 1.0.0 as a dependency.*

### **Refresh Gradle**
Once you've done the steps above, you now need to reimport the Gradle project. This can differ from IDE to IDE, so we can't give explicit details on how. On most IDEs, just follow the steps you would when setting up your mod environment.

### **Adding Abnormals Core as a Forge Dependency**
You'll need to tell Forge that Abnormals Core is a hard-dependency for your mod, this means that Abnormals Core will be **required** to run your mod. At the bottom of your `mods.toml` file paste the following:
```
[[dependencies.<modid>]]
    modId="abnormals_core"
    mandatory=true
    versionRange="[x.x.x,)"
    ordering="AFTER"
    side="BOTH"
```
Replace `<modid>` with your mod's id of course. Set the `versionRange` to the version you chose on CurseForge.

That's it! You should now have Abnormals Core in your workspace and as a required dependency. Any issues you find should be reported to the [Issue Tracker](https://github.com/minecraftabnormals/Abnormals-Core/issues).

![](https://i.imgur.com/5rYQHXf.png)
## **📦 Official MCA Mods**

-   [Atmospheric](https://www.curseforge.com/minecraft/mc-mods/atmospheric)
-   [Autumnity](https://www.curseforge.com/minecraft/mc-mods/autumnity)
-   [Bamboo Blocks](https://www.curseforge.com/minecraft/mc-mods/bamboo-blocks)
-   [Berry Good](https://www.curseforge.com/minecraft/mc-mods/berry-good)
-   [Bloomful](https://www.curseforge.com/minecraft/mc-mods/bloomful)
-   [Buzzier Bees](https://www.curseforge.com/minecraft/mc-mods/buzzier-bees)
-   [Endergetic Expansion](https://www.curseforge.com/minecraft/mc-mods/endergetic)
-   [Savage & Ravage](https://www.curseforge.com/minecraft/mc-mods/savage-and-ravage)
-   [Swamp Expansion](https://www.curseforge.com/minecraft/mc-mods/swamp-expansion)
-   [Upgrade Aquatic](https://www.curseforge.com/minecraft/mc-mods/upgrade-aquatic)

![](https://i.imgur.com/5rYQHXf.png)
[![Wiki](https://i.imgur.com/RyeQAUa.png)](https://wiki.minecraftabnormals.com)
