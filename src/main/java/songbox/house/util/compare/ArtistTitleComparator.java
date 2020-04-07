package songbox.house.util.compare;

import songbox.house.util.ArtistsTitle;

import java.util.Comparator;

import static org.apache.commons.lang3.ObjectUtils.allNotNull;

public class ArtistTitleComparator implements Comparator<ArtistsTitle> {

    private static final LevenshteinDistanceComparator COMPARATOR = new LevenshteinDistanceComparator();

    @Override
    public int compare(ArtistsTitle artistsTitle1, ArtistsTitle artistsTitle2) {
        int artists = allNotNull(artistsTitle1.getArtists(), artistsTitle2.getArtists())
                ? COMPARATOR.compare(artistsTitle1.getArtists(), artistsTitle2.getArtists())
                : 0;
        int title = allNotNull(artistsTitle1.getTitle(), artistsTitle2.getTitle())
                ? COMPARATOR.compare(artistsTitle1.getTitle(), artistsTitle2.getTitle()) :
                0;
        return (artists + title) / 2;
    }
}
