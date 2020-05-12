## Installation Information
### Adding the Curse Maven Plugin
   - This allows for the 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 to pull the jar from the Curse Maven
   - After 𝐛𝐮𝐢𝐥𝐝𝐬𝐜𝐫𝐢𝐩𝐭 in 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 put the following lines,
      ```
      plugins {
         id "com.wynprice.cursemaven" version "2.1.1"
      }
      ```
### Compiling the Dependency
   - This will get the jar from the Curse Maven and deobfusticate it and add it as a library
   - In the 𝐝𝐞𝐩𝐞𝐧𝐝𝐞𝐧𝐜𝐢𝐞𝐬 in the 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 add this line,
      ```compile fg.deobf("curse.maven:abnormals-core:fileid")```
   - The fileid matches the file id of the file on curse, so for example for Version 1.0.0 of AC it'd use the file id 2954634
### Run the gradle processes
   - Once you've done the other steps just do the other normal gradle steps taken to setup a mod dev enviornment and AC will be added as     a dependency

## Making AC a Dependency
To make AC a Dependency for your mod requires only one simple step.
In your 𝐦𝐨𝐝𝐬.𝐭𝐨𝐦𝐥 add the following lines:
```
[[dependencies.<modid>]]
    modId="abnormals_core"
    mandatory=true
    versionRange="[x.x.x,)"
    ordering="AFTER"
    side="BOTH"
```
