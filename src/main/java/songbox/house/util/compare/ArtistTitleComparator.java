package songbox.house.util.compare;

import songbox.house.util.ArtistsTitle;

import java.util.Comparator;

public class ArtistTitleComparator implements Comparator<ArtistsTitle> {

    private static final LevenshteinDistanceComparator COMPARATOR = new LevenshteinDistanceComparator();

    @Override
    public int compare(ArtistsTitle artistsTitle1, ArtistsTitle artistsTitle2) {
        int artists = COMPARATOR.compare(artistsTitle1.getArtists(), artistsTitle2.getArtists());
        int title = COMPARATOR.compare(artistsTitle1.getTitle(), artistsTitle2.getTitle());
        return (artists + title) / 2;
    }
}
