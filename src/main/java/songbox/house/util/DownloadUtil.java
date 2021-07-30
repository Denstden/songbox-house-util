package songbox.house.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.io.IOUtils.toByteArray;
import static songbox.house.util.RetryUtil.DEFAULT_RETRIES;
import static songbox.house.util.RetryUtil.getOptionalWithRetries;

@Slf4j
public class DownloadUtil {
    private static final int DEFAULT_CONNECT_TIMEOUT = 10_000;
    private static final int DEFAULT_READ_TIMEOUT = 10_000;

    public static Optional<byte[]> downloadBytes(String url) {
        return download(url, null, -1, -1);
    }

    public static Optional<byte[]> downloadBytes(String url, Proxy proxy, long bytesStart, long bytesEnd) {
        return download(url, proxy, bytesStart, bytesEnd);
    }

    private static Optional<byte[]> download(String urlStr, Proxy proxy, long bytesStart, long bytesEnd) {
        DownloadContext downloadContext = new DownloadContext(proxy, bytesStart, bytesEnd);
        return getOptionalWithRetries(DownloadUtil::doDownload, urlStr, downloadContext, DEFAULT_RETRIES, "download_bytes");
    }

    private static Optional<byte[]> doDownload(String urlStr, DownloadContext context) {
        try {
            final URL url = new URL(urlStr);
            final HttpURLConnection connection = getConnection(url, context.proxy);
            if (context.start > 0 && context.end > 0 && context.end > context.start) {
                connection.setRequestProperty("range", "bytes=" + context.start + "-" + context.end);
            }
            final InputStream inputStream = connection.getInputStream();
            final byte[] bytes = toByteArray(inputStream);
            return of(bytes);
        } catch (Exception e) {
            log.debug("Retryable exception", e);
            return empty();
        }
    }

    private static class DownloadContext {
        final Proxy proxy;
        final long start;
        final long end;

        private DownloadContext(Proxy proxy, long start, long end) {
            this.proxy = proxy;
            this.start = start;
            this.end = end;
        }
    }

    private static HttpURLConnection getConnection(URL url, Proxy proxy) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) (nonNull(proxy) ? url.openConnection(proxy) : url.openConnection());
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)");
        return connection;
    }
}
