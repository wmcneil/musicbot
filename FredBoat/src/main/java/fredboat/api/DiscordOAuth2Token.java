/*
 * MIT License
 *
 * Copyright (c) 2016 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fredboat.api;

import fredboat.db.entities.UConfig;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2Scope;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc5545.DateTime;

public class DiscordOAuth2Token implements OAuth2AccessToken {

    private final String bearer;
    private final String refresh;
    private final long expiration;

    public DiscordOAuth2Token(UConfig config) {
        bearer = config.getBearer();
        refresh = config.getRefresh();
        expiration = config.getBearerExpiration() * 1000;
    }

    @Override
    public String accessToken() throws ProtocolException {
        return bearer;
    }

    @Override
    public String tokenType() throws ProtocolException {
        return null;
    }

    @Override
    public boolean hasRefreshToken() {
        return true;
    }

    @Override
    public String refreshToken() throws ProtocolException {
        return refresh;
    }

    @Override
    public DateTime expiriationDate() throws ProtocolException {
        return new DateTime(expiration);
    }

    @Override
    public OAuth2Scope scope() throws ProtocolException {
        return new BasicScope("identify");
    }
}
