[![Build Status](https://ci.cloudnetservice.eu/buildStatus/icon?job=CloudNetService/v2-signsystem/master)](https://ci.cloudnetservice.eu/job/CloudNetService/job/v2-signsystem/master)
[![star this repo](http://githubbadges.com/star.svg?user=CloudNetService&repo=v2-signsystem)](https://github.com/CloudNetService/v2-signsystem)
[![fork this repo](http://githubbadges.com/fork.svg?user=CloudNetService&repo=v2-signsystem)](https://github.com/CloudNetService/v2-signsystem/fork)
[![GitHub license](https://img.shields.io/github/license/CloudNetService/v2-signsystem.svg)](https://github.com/CloudNetService/v2-signsystem/blob/master/LICENSE)

[![DepShield Badge](https://depshield.sonatype.org/badges/CloudNetService/v2-signsystem/depshield.svg)](https://depshield.github.io)
[![GitHub issues](https://img.shields.io/github/issues/CloudNetService/v2-signsystem.svg)](https://github.com/CloudNetService/v2-signsystem/issues)
[![GitHub contributors](https://img.shields.io/github/contributors/CloudNetService/v2-signsystem.svg)](https://github.com/CloudNetService/v2-signsystem/graphs/contributors)
[![Github All Releases](https://img.shields.io/github/downloads/CloudNetService/v2-signsystem/total.svg)](https://github.com/CloudNetService/v2-signsystem/releases)
[![GitHub release](https://img.shields.io/github/release/CloudNetService/v2-signsystem.svg)](https://github.com/CloudNetService/v2-signsystem/releases)


# Notify System | The Cloud Network Environment Technology 2 Sign System
![Image of CloudNet](https://cdn.discordapp.com/attachments/325383142464552972/354670548292206594/CloudNet.png)

This is the Sign System for CloudNet 2.2.0
 

 ### Support
 
  * Spigot-Support » 1.7.10 - 1.14
    * PaperSpigot, TacoSpigot, Hose, Torch
  * BungeeCord-Support » 1.7.10 - 1.14
    * Flexpipe, HexaCord, Waterfall, TraverTine
    
### Discord
 *  [Discord Invite](https://discord.gg/CPCWr7w)
 
### Developer
If you would like to contribute to this repository, feel free to fork the repo and then create a pull request to our current dev branch. 
  
Maven:
```xml
<repositories>
    <repository>
        <id>cloudnet-repo</id>
        <url>https://cloudnetservice.eu/repositories</url>
    </repository>
</repositories>

<dependencies>
    <!-- Core of the Sign System -->
    <dependency>
        <groupId>eu.cloudnetservice</groupId>
        <artifactId>SignCore</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    <!-- Spigot/BungeeCord -->
    <dependency>
        <groupId>eu.cloudnetservice</groupId>
        <artifactId>SignPlugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```