package com.example.andrea.lyrics.utils;

import com.example.andrea.lyrics.model.Lyrics;

/**
 * Created by andrea on 26/03/17.
 */

public class HtmlParser {

    private static final String LYRICS_START = "<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->";
    private static final String LYRICS_END = "<!-- MxM banner -->";
    private static final String COMMENT_START = "<!--";
    private static final String COMMENT_END = "-->";
    private static final String ARTIST_NAME = "ArtistName";
    private static final String SONG_NAME = "SongName";

    public static Lyrics parseSourceCode(String html) {
        Lyrics lyrics = new Lyrics();

        if (html.equals(LyricsDownloader.NOT_FOUND_CODE)) {
            lyrics.setLyrics(html);
            lyrics.setArtistName("noartist");
            lyrics.setSongName("nosong");
            return lyrics;
        }

        // artist name
        int artistNameStart = html.indexOf(ARTIST_NAME);
        int artistNameEnd = html.indexOf(";", artistNameStart);
        String artistNameLine = html.substring(artistNameStart, artistNameEnd);
        String artistName = artistNameLine.split("=")[1].trim().replace("\"", "");

        // song name
        int songNameStart = html.indexOf(SONG_NAME);
        int songNameEnd = html.indexOf(";", songNameStart);
        String songNameLine = html.substring(songNameStart, songNameEnd);
        String songName = songNameLine.split("=")[1].trim().replace("\"", "");

        // lyrics
        int lyricsStart = html.indexOf(LYRICS_START);
        html = html.replace(html.substring(0, lyricsStart), "");
        int lyricsEnd = html.indexOf(LYRICS_END);
        html = html.replace(html.substring(lyricsEnd), "");
        // remove 'Usage of azlyrics is prohibited...' comment
        int toRemoveStart = html.indexOf(COMMENT_START);
        int toRemoveEnd = html.indexOf(COMMENT_END) + COMMENT_END.length();
        html = html.replace(html.substring(toRemoveStart, toRemoveEnd), "");

        lyrics.setLyrics(html);
        lyrics.setArtistName(artistName);
        lyrics.setSongName(songName);
        return lyrics;
    }


}
