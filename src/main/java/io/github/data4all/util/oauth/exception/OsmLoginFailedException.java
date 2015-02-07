// License: GPL. For details, see LICENSE file.
package io.github.data4all.util.oauth.exception;


public class OsmLoginFailedException extends OsmOAuthAuthorizationException{

    public OsmLoginFailedException() {
        super();
    }

    public OsmLoginFailedException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public OsmLoginFailedException(String arg0) {
        super(arg0);
    }

    public OsmLoginFailedException(Throwable arg0) {
        super(arg0);
    }
}
