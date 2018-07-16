package demo;

/*
 2    * Copyright 2012 The Netty Project
 3    *
 4    * The Netty Project licenses this file to you under the Apache License,
 5    * version 2.0 (the "License"); you may not use this file except in compliance
 6    * with the License. You may obtain a copy of the License at:
 7    *
 8    *   http://www.apache.org/licenses/LICENSE-2.0
 9    *
 10   * Unless required by applicable law or agreed to in writing, software
 11   * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 12   * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 13   * License for the specific language governing permissions and limitations
 14   * under the License.
 15   */
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;

/**
 * A simple HTTP client that prints out the content of the HTTP response to
 * {@link System#out} to test {@link HttpSnoopServer}.
 */
public final class HttpSnoopClient {

	static final String URL = System.getProperty("url", "http://m01.drplant.com.cn:8080/");

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 1000; i++) {
			main1(args);
		}
	}

	public static void main1(String[] args) throws Exception {
		URI uri = new URI(URL);
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			if ("http".equalsIgnoreCase(scheme)) {
				port = 80;
			} else if ("https".equalsIgnoreCase(scheme)) {
				port = 443;
			}
		}

		if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		// Configure SSL context if necessary.
		final boolean ssl = "https".equalsIgnoreCase(scheme);
		final SslContext sslCtx;
		if (ssl) {
			sslCtx = SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} else {
			sslCtx = null;
		}

		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new HttpSnoopClientInitializer(sslCtx));

			// Make the connection attempt.
			Channel ch = b.connect(host, port).sync().channel();

			// Prepare the HTTP request.
			HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0,//
					HttpMethod.GET,//
					uri.getRawPath());
			request.headers().set(HttpHeaderNames.HOST, host);
			request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
			request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);

			// Set some example cookies.
			request.headers().set(
					HttpHeaderNames.COOKIE,
					ClientCookieEncoder.STRICT.encode(new DefaultCookie("my-cookie", "foo"),
							new DefaultCookie("another-cookie", "bar")));

			// Send the HTTP request.
			ch.writeAndFlush(request);

			// Wait for the server to close the connection.
			ChannelFuture cf = ch.closeFuture().sync();
			cf.get();
			ch.close();
		} finally {
			// Shut down executor threads to exit.
			group.shutdownGracefully();
		}
	}
}
