package songbox.house.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
public class ArtistsTitle {

    private static final Pattern ARTIST_TITLE_REGEX = compile("^((\\W*\\s*)|(#\\d*;?.?))?" +
            "(?<artists>[A-Z].*)(\\s*)( - )(\\s*)(?<title>.*)$");

    private final String artists;
    private final String title;

    protected ArtistsTitle(String artists, String title) {
        this.artists = safeTrim(artists);
        this.title = safeTrim(title);
    }

    public static ArtistsTitle of(String artists, String title) {
        return new ArtistsTitle(artists, title);
    }

    @Override
    public String toString() {
        return artists + " - " + title;
    }

    public String getArtists() {
        return artists;
    }

    public String getTitle() {
        return title;
    }

    public static ArtistsTitle parse(String artistsTitleString) {
        Matcher matcher = ARTIST_TITLE_REGEX.matcher(artistsTitleString);
        if (matcher.matches()) {
            String artists = matcher.group("artists");
            String title = matcher.group("title");
            if (isNotBlank(artists) && isNotBlank(title)) {
                return of(artists, title);
            } else {
                log.warn("Can't parse artists and title from '{}' (check regex)", artistsTitleString);
            }
        }

        // TODO remove if regex will work fine
        return parseDummy(artistsTitleString);
    }

    private static ArtistsTitle parseDummy(String artistsTitleString) {
        String[] artistTitle = artistsTitleString.split("-", 2);

        String artists = null;
        String title = artistsTitleString;
        if (artistTitle.length == 2) {
            artists = artistTitle[0].trim();
            title = artistTitle[1].trim();
        } else {
            String[] splitedBySpace = artistsTitleString.split(" ");
            if (splitedBySpace.length == 2) {
                artists = splitedBySpace[0].trim();
                title = splitedBySpace[1].trim();
            }
        }

        return of(artists, title);
    }

    private String safeTrim(String str) {
        return ofNullable(str).map(String::trim).orElse(null);
    }
}
