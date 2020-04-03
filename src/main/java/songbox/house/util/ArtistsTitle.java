package songbox.house.util;

public class ArtistsTitle {
    private final String artists;
    private final String title;

    private ArtistsTitle(String artists, String title) {
        this.artists = artists;
        this.title = title;
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
}
