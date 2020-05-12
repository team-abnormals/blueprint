## Installation Information
### Adding the Curse Maven Plugin
   - This allows for the 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 to pull the jar from the Curse Maven
   - After 𝐛𝐮𝐢𝐥𝐝𝐬𝐜𝐫𝐢𝐩𝐭 in 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 put the following lines, 
      𝐩𝐥𝐮𝐠𝐢𝐧𝐬 {
         𝐢𝐝 "𝐜𝐨𝐦.𝐰𝐲𝐧𝐩𝐫𝐢𝐜𝐞.𝐜𝐮𝐫𝐬𝐞𝐦𝐚𝐯𝐞𝐧" 𝐯𝐞𝐫𝐬𝐢𝐨𝐧 "𝟐.𝟏.𝟏"
      }
### Compiling the Dependency
   - This will get the jar from the Curse Maven and deobfusticate it and add it as a library
   - In the 𝐝𝐞𝐩𝐞𝐧𝐝𝐞𝐧𝐜𝐢𝐞𝐬 in the 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 add this line,
      𝐜𝐨𝐦𝐩𝐢𝐥𝐞 𝐟𝐠.𝐝𝐞𝐨𝐛𝐟("𝐜𝐮𝐫𝐬𝐞.𝐦𝐚𝐯𝐞𝐧:𝐚𝐛𝐧𝐨𝐫𝐦𝐚𝐥𝐬-𝐜𝐨𝐫𝐞:fileid")
   - The fileid matches the file id of the file on curse, so for example for Version 1.0.0 of AC it'd use the file id 2954634
### Run the gradle processes
   - Once you've done the other steps just do the other normal gradle steps taken to setup a mod dev enviornment and AC will be added as a      dependency

## Making AC a Dependency
To make AC a Dependency for your mod requires only one simple step.
In your 𝐦𝐨𝐝𝐬.𝐭𝐨𝐦𝐥 add the following lines:
[[𝐝𝐞𝐩𝐞𝐧𝐝𝐞𝐧𝐜𝐢𝐞𝐬.<𝐦𝐨𝐝𝐢𝐝>]]
    𝐦𝐨𝐝𝐈𝐝="𝐚𝐛𝐧𝐨𝐫𝐦𝐚𝐥𝐬_𝐜𝐨𝐫𝐞"
    𝐦𝐚𝐧𝐝𝐚𝐭𝐨𝐫𝐲=𝐭𝐫𝐮𝐞
    𝐯𝐞𝐫𝐬𝐢𝐨𝐧𝐑𝐚𝐧𝐠𝐞="[x.x.x,)"
    𝐨𝐫𝐝𝐞𝐫𝐢𝐧𝐠="𝐀𝐅𝐓𝐄𝐑"
    𝐬𝐢𝐝𝐞="𝐁𝐎𝐓𝐇"
