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
import fredboat.util.DiscordUtil;
import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.*;
import org.dmfs.rfc5545.Duration;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class OAuthManager {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(OAuthManager.class);

    private static OAuth2Client oauth = null;
    private static final HttpRequestExecutor EXECUTOR = new HttpUrlConnectionExecutor();

    //TODO: Use this
    public static void start(String token, String secret) throws UnirestException {
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
                URI.create("http://localhost") /* Redirect URL, unused */);
    }
}
