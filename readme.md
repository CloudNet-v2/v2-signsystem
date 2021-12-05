# This project is archived! Please consider using [CloudNet v3](https://github.com/CloudNetService/CloudNet-v3). The CloudNet v2 repository can be found [here](https://github.com/CloudNetService/CloudNet).


# Sign System | The Cloud Network Environment Technology 2

![Image of CloudNet](https://cdn.discordapp.com/attachments/325383142464552972/354670548292206594/CloudNet.png)

This is the Sign System for CloudNet 2.2.0
 
---
 ### Support
 
 #### Minecraft-Support
 | Minecraft-Server-Version | 1.8.X | 1.9.X | 1.10.X | 1.11.X | 1.12.X | 1.13.X | 1.14.X | 1.15.X |
 |----------------|-------|-------|--------|--------|--------|--------|--------|--------|
 | [Spigot](https://www.spigotmc.org/wiki/about-spigot/) | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 | [PaperSpigot](https://github.com/PaperMC/Paper) | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 | [Hose(Not Tested)](https://github.com/softpak/HOSE) | :interrobang: | :interrobang: | :interrobang: | :x: | :x: | :x: | :x: | :x: |
 | [Akarin(Not Tested)](https://github.com/Akarin-project/Akarin) | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :x: |
 | [Glowstone(Not Tested)](https://www.glowstone.net/) | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: |


 #### Proxy-Support
 | Proxy-Version(Latest Only) | 1.8.X | 1.9.X | 1.10.X | 1.11.X | 1.12.X | 1.13.X | 1.14.X | 1.15.X |
 |----------------|-------|-------|--------|--------|--------|--------|--------|--------|
 | [BungeeCord](https://github.com/SpigotMC/BungeeCord) | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 | [Waterfall](https://github.com/PaperMC/Waterfall) | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 | [Travertine](https://github.com/PaperMC/Travertine) | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 | [Hexacord](https://github.com/HexagonMC/BungeeCord) | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: | :heavy_check_mark: |
 | [FlameCord(Not Tested)](https://www.mc-market.org/resources/13492/) | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: | :interrobang: |
 
 #### CloudNet-Support
 | CloudNet-Support | Supported | 
 |------------------|-----------|
 | 2.1.17 below | :x: |
 | 2.2 above| :heavy_check_mark: |
 | Complete generation 3 | :x: |
  
___
    
### Discord
 *  [Discord Invite](https://discord.gg/CPCWr7w)
 
---
### Developer
If you would like to contribute to this repository, feel free to fork the repo and then create a pull request to our current dev branch. 
 
##### Maven:
```xml
<repositories>
    <repository>
        <id>cloudnet-repo</id>
        <url>https://repo.cloudnetservice.eu/repository/snapshots</url>
    </repository>
</repositories>

<dependencies>
    <!-- RootProject -->
    <dependency>
        <groupId>eu.cloudnetservice</groupId>
        <artifactId>SignSystem</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

##### Gradle:
```groovy
repositories {
    maven {
        url "https://repo.cloudnetservice.eu/repository/snapshots"
    }
}
dependencies {
    compileOnly group: 'eu.cloudnetservice', name: 'SignSystem', version: '1.0-SNAPSHOT'
}
```
