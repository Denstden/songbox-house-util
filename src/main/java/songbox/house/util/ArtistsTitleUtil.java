package songbox.house.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static songbox.house.util.ArtistsTitle.of;

@Slf4j
public class ArtistsTitleUtil {

    private static final Pattern ARTIST_TITLE_REGEX = compile("^((\\W*\\s*)|(#\\d*;?.?))?" +
            "(?<artists>[A-Z].*)(\\s*)( - )(\\s*)(?<title>.*)$");

    public static ArtistsTitle extractArtistTitle(String query) {
        Matcher matcher = ARTIST_TITLE_REGEX.matcher(query);
        if (matcher.matches()) {
            String artists = matcher.group("artists");
            String title = matcher.group("title");
            if (isNotBlank(artists) && isNotBlank(title)) {
                return of(artists, title);
            } else {
                log.warn("Can't parse artists and title from '{}' (check regex)", query);
            }
        }

        // TODO remove if regex will work fine
        return extractDummy(query);
    }

    private static ArtistsTitle extractDummy(String query) {
        String[] artistTitle = query.split("-", 2);

        String artists = null;
        String title = query;
        if (artistTitle.length == 2) {
            artists = artistTitle[0].trim();
            title = artistTitle[1].trim();
        } else {
            String[] splitedBySpace = query.split(" ");
            if (splitedBySpace.length == 2) {
                artists = splitedBySpace[0].trim();
                title = splitedBySpace[1].trim();
            }
        }

        return of(artists, title);
    }
}
