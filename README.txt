------------------------------------
      Installation Information
------------------------------------

#1 A̲d̲d̲i̲n̲g̲ ̲t̲h̲e̲ ̲C̲u̲r̲s̲e̲ ̲M̲a̲v̲e̲n̲ ̲P̲l̲u̲g̲i̲n̲
   - This allows for the 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 to pull the jar from the Curse Maven
   - After 𝐛𝐮𝐢𝐥𝐝𝐬𝐜𝐫𝐢𝐩𝐭 in 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 put the following lines, 
      𝐩𝐥𝐮𝐠𝐢𝐧𝐬 {
         𝐢𝐝 "𝐜𝐨𝐦.𝐰𝐲𝐧𝐩𝐫𝐢𝐜𝐞.𝐜𝐮𝐫𝐬𝐞𝐦𝐚𝐯𝐞𝐧" 𝐯𝐞𝐫𝐬𝐢𝐨𝐧 "𝟐.𝟏.𝟏"
      }
#2 C̲o̲m̲p̲i̲l̲i̲n̲g̲ ̲t̲h̲e̲ ̲d̲e̲p̲e̲n̲d̲e̲n̲c̲y̲
   - This will get the jar from the Curse Maven and deobfusticate it and add it as a library
   - In the 𝐝𝐞𝐩𝐞𝐧𝐝𝐞𝐧𝐜𝐢𝐞𝐬 in the 𝐛𝐮𝐢𝐥𝐝.𝐠𝐫𝐚𝐝𝐥𝐞 add this line,
      𝐜𝐨𝐦𝐩𝐢𝐥𝐞 𝐟𝐠.𝐝𝐞𝐨𝐛𝐟("𝐜𝐮𝐫𝐬𝐞.𝐦𝐚𝐯𝐞𝐧:𝐚𝐛𝐧𝐨𝐫𝐦𝐚𝐥𝐬-𝐜𝐨𝐫𝐞:fileid")
   - The fileid matches the file id of the file on curse, so for example for Version 1.0.0 of AC it'd use the file id 2954634
#3 R̲u̲n̲ ̲t̲h̲e̲ ̲g̲r̲a̲d̲l̲e̲ ̲p̲r̲o̲c̲e̲s̲s̲e̲s̲
   - Once you've done the other steps just do the other normal gradle steps taken to setup a mod dev enviornment and AC will be added as a      dependency
