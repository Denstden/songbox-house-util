package songbox.house.util.compare;

import songbox.house.util.ArtistsTitle;

import java.util.Comparator;

public class ArtistTitleComparator implements Comparator<ArtistsTitle> {

    private static final LevenshteinDistanceComparator COMPARATOR = new LevenshteinDistanceComparator();

    @Override
    public int compare(ArtistsTitle artistsTitle1, ArtistsTitle artistsTitle2) {
        return COMPARATOR.compare(artistsTitle1.toString(), artistsTitle2.toString());
    }
}
