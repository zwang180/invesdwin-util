package de.invesdwin.util.lang.uri;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import de.invesdwin.util.lang.Strings;

@Immutable
public final class URIs {

    private static final URLCodec URL_CODEC = new URLCodec();

    private URIs() {}

    public static String encode(final String url) {
        try {
            return URL_CODEC.encode(url);
        } catch (final EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(final String url) {
        try {
            return URL_CODEC.decode(url);
        } catch (final DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL asUrl(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URL(uri); //SUPPRESS CHECKSTYLE singleline
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL asUrl(final URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI asUri(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri); //SUPPRESS CHECKSTYLE singleline
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI asUriOrNull(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri); //SUPPRESS CHECKSTYLE singleline
        } catch (final URISyntaxException e) {
            return null;
        }
    }

    public static URI asUri(final URL url) {
        if (url == null) {
            return null;
        }
        try {
            return url.toURI();
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBasis(final String uri) {
        return getBasis(asUri(uri));
    }

    public static String getBasis(final URI uri) {
        return uri.toString().replace(uri.getPath(), "");
    }

    public static String getBasis(final URL url) {
        return getBasis(asUri(url));
    }

    public static Map<String, String> getQueryMap(final URL url) {
        return getQueryMap(url.getQuery());
    }

    public static Map<String, String> getQueryMap(final URI uri) {
        return getQueryMap(uri.getQuery());
    }

    /**
     * http://stackoverflow.com/questions/11733500/getting-url-parameter-in-java-and-extract-a-specific-text-from-that-
     * url
     */
    private static Map<String, String> getQueryMap(final String query) {
        final String[] params = query.split("&");
        final Map<String, String> map = new HashMap<String, String>();
        for (final String param : params) {
            final String name = Strings.substringBefore(param, "=");
            final String value = Strings.substringAfter(param, "=");
            map.put(name, value);
        }
        return map;
    }

    public static URIsConnect connect(final String uri) {
        return connect(asUrl(uri));
    }

    public static URIsConnect connect(final URI uri) {
        return connect(asUrl(uri));
    }

    public static URIsConnect connect(final URL url) {
        return new URIsConnect(url);
    }

    public static URI setPort(final URI uri, final int port) {
        try {
            //CHECKSTYLE:OFF
            final URI newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(),
                    uri.getQuery(), uri.getFragment());
            //CHECKSTYLE:ON
            return newUri;
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL setPort(final URL url, final int port) {
        try {
            //CHECKSTYLE:OFF
            final URL newUri = new URL(url.getProtocol(), url.getHost(), port, url.getFile());
            //CHECKSTYLE:ON
            return newUri;
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
