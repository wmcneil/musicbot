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

import com.mashape.unirest.http.exceptions.UnirestException;
import fredboat.FredBoat;
import fredboat.db.DatabaseManager;
import fredboat.db.EntityReader;
import fredboat.db.EntityWriter;
import fredboat.db.entities.UConfig;
import fredboat.util.DiscordUtil;
import net.dv8tion.jda.core.entities.User;
import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.*;
import org.dmfs.oauth2.client.grants.ClientCredentialsGrant;
import org.dmfs.oauth2.client.scope.BasicScope;
import org.dmfs.rfc5545.Duration;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

@SuppressWarnings("Duplicates") //TODO: Remove this
public class OAuthManager {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(OAuthManager.class);

    private static OAuth2Client oauth = null;
    private static final HttpRequestExecutor EXECUTOR = new HttpUrlConnectionExecutor();

    //TODO: Use this
    static void start(String token, String secret) throws UnirestException {
        // Create OAuth2 provider
        OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
                URI.create("https://discordapp.com/api/oauth2/authorize"),
                URI.create("https://discordapp.com/api/oauth2/token"),
                new Duration(1, 0, 3600) /* default expiration time in case the server doesn't return any */);

        OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(
                DiscordUtil.getApplicationInfo(token).getString("id"), secret);

        oauth = new BasicOAuth2Client(
                provider,
                credentials,
                URI.create("http://localhost/") /* Redirect URL, unused */);
    }

    static UConfig handleCallback(String code) {
        try {
            // Request access token using a Client Credentials Grant
            OAuth2AccessToken token = new ClientCredentialsGrant(oauth, new BasicScope("scope")).accessToken(executor);
            if (!token.scope().hasToken("guild") || !token.scope().hasToken("identify")) {
                log.warn("Got invalid OAuth2 scopes.");
                return null;
            }

            User user = DiscordUtil.getUserFromBearer(FredBoat.getFirstJDA(), token.accessToken());

            UConfig uconfig = EntityReader.getUConfig(user.getId());

            uconfig = uconfig == null ? new UConfig() : uconfig;

            uconfig = uconfig
                    .setBearer(token.accessToken())
                    .setBearerExpiration(token.expiriationDate().getTimestamp())
                    .setRefresh(token.refreshToken())
                    .setUserId(user.getId());

            //Save to database
            EntityWriter.mergeUserConfig(uconfig, true);

            return uconfig;
        } catch (IOException | ProtocolError | ProtocolException ex) {
            throw new RuntimeException("Failed oauth access token grant", ex);
        }
    }
}
