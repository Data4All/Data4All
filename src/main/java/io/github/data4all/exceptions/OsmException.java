package io.github.data4all.exceptions;

public class OsmException extends Exception {

    private static final long serialVersionUID = 2900660063579152782L;

    public OsmException(final String string) {
        super(string);
    }

    public OsmException(final String string, final Throwable e) {
        super(string);
        initCause(e);
    }

}
