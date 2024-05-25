# RomPress
RomPress is a ROM collection manager for various game consoles meant to scan, sort, rename, and compress ROM files for optimal organization and disk space.
## Planned Features and Project Overview

While many ROM collection tools already exist, many of them are daunting or outdated. With the increased popularity of RetroArch and frontends like LaunchBox and EmulationStation,
there is a greater incentive for users to store large collections of ROMs. For those who choose not to download full, pre-organized ROM sets from preservation groups, ROM folders and files
may get messy. Additionally, disk space may become a concern. RomPress aims to not only help users organize their ROM sets, but also optimize their storage space using the most up-to-date
compression formats and methods while ensuring compressed files are directly readable by emulators and that compression is fully reversible without data loss - without requiring the user to
spend any time writing shell scripts to compress ROM files into various formats.
### Scan and audit

By selecting a directory containing ROM files for a given video game console, RomPress will read each file and display its title, region, revision number, and checksums. This data will be compared against a user-specified DAT set (No-Intro, Redump, etc)
for verification of a good dump. The user can choose to delete duplicate, corrupt, or unwanted files (ex, if you aren't interested in non-English versions or unlicensed games).
### Smart sorting

I recommend creating a base directory called 'roms/' wherever you like. Within roms/, create a directory for each platform you have ROM files for. (ex, roms/Nintendo - NES, roms/Sony - Playstation)
By giving RomPress a directory for each system, it will sort your files like this by default:


	roms/
		Nintendo - NES/
			America/
				all releases exclusive to the US
			Japan/
				all releases exclusive to Japan
			Europe/
			Prototypes/
			Unlicensed/
			etc, etc
		
By using this folder structure, you can have access to every available release for a given system and quickly navigate to whichever version you prefer. It's also recommended to have directories dedicated to homebrew and ROM hack releases.
### Compression

RomPress aims to convert your collection into the smallest possible file size using the most up-to-date formats and methods. Every file compressed by RomPress should be guaranteed to be playable in most modern emulators.
Before compression, RomPress will extract any files that are already compressed into a non-ideal format, as long as the file was compressed using a reversible lossless method.

For older game consoles such as the NES, SNES, Game Boy, and most cartridge-based systems, RomPress will compress your files into the .7z format. 7z can be read by most emulators, is generally quick to decompress, and offers the best available compression for these ROMs - better than zip and rar.
It can be argued that zip is good enough, especially when file sizes are so small to begin with (we're talking about kilobytes up to a small amount of megabytes), but RomPress is all about the most optimal compression.
For disc-based games like Playstation, PS2, or Sega CD, the CHD format is generally the best choice in terms of saving disk space and compatibility with modern emulators. Additionally, RomPress will generate missing .cue files from CD dumps.

Note that for Gamecube releases, the best compression ratio is achieved by Dolphin's RVZ format. RomPress cannot compress files into RVZ - this must be done manually within Dolphin itself.

For excellent information about ROM compression and archiving, see the Emulation General wiki here: https://emulation.gametechwiki.com/index.php/Save_disk_space_for_ISOs
