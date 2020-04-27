package songbox.house.util;

import org.apache.commons.lang3.StringUtils;

public final class FileNameUtil {

    private static final String FILE_NAME_REGEX = "[!\"#$%&'()*+,\\-/:;<=>?@\\[\\]^_`{|}~]";

    private FileNameUtil(){}

    public static String getFileName(String str, int maxLengthWithoutExtension, String extension) {
        return StringUtils.substring(str.replaceAll(FILE_NAME_REGEX, ""), 0, maxLengthWithoutExtension) + extension;
    }

    public static String getMp3FileName(ArtistsTitle artistsTitle) {
        StringBuilder sb = new StringBuilder();

        String formattedArtist = artistsTitle.getArtists().trim().replaceAll(FILE_NAME_REGEX, "");
        String formattedTitle = artistsTitle.getTitle().trim().replaceAll(FILE_NAME_REGEX, "");

        sb.append(StringUtils.substring(formattedArtist, 0, 40));
        sb.append(" - ");
        sb.append(StringUtils.substring(formattedTitle, 0, 50));

        sb.append(".mp3");

        return sb.toString();
    }
}
