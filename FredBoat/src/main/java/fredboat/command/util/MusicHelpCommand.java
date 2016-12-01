/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Frederik Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package fredboat.command.util;

public class MusicHelpCommand extends TextCommand {
	
	public static final String MUSIC
			="**This is the list of all the music commands I can execute, them being:** \n"
			+"`*;;play* — makes me play a song. You can either give me an url to a video(e.g.: ;;play <url>) or use it to search on youtube(e.g.: ;;play rick roll) \n"
			+"*;;list* — display a list of the current songs in the playlist \n"
			+"*;;nowplaying — displays the current playing song \n"
			+"*;;skip* — skips the current song. Please use in moderation \n"
			+"*;;stop* — stops the current song and **clears** the playlist. Reserved for moderators \n"
			+"*;;pause* — pauses the player \n"
			+"*;;unpause* — unpauses the player \n"
			+"*;;join* — makes me join your current voice channel \n"
			+"*;;leave* — makes me leave the current voice channel \n"
			+"*;;repeat* — toggles repeat mode for the current song \n"
			+"*;;shuffle* — toggles shuffle mode for the current queue \n"
			+"*;;volume* — changes the volume, volumes are 0-150, default volume is 100 \n"
      +"*;;mhelp* — makes me post this message \n"
      +"If you ping me, you can interact with my cleverbot module.";
			
	public MusicHelpCommand() {
		super(MUSIC);
	}
}

//gib dem pats to me
