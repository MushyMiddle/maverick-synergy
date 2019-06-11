/**
 * (c) 2002-2019 JADAPTIVE Limited. All Rights Reserved.
 *
 * This file is part of the Maverick Synergy Java SSH API.
 *
 * Maverick Synergy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Maverick Synergy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.sshtools.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sshtools.client.components.DiffieHellmanEcdhNistp256;
import com.sshtools.client.components.DiffieHellmanEcdhNistp384;
import com.sshtools.client.components.DiffieHellmanEcdhNistp521;
import com.sshtools.client.components.DiffieHellmanGroup14Sha1JCE;
import com.sshtools.client.components.DiffieHellmanGroup14Sha256JCE;
import com.sshtools.client.components.DiffieHellmanGroup15Sha512JCE;
import com.sshtools.client.components.DiffieHellmanGroup16Sha512JCE;
import com.sshtools.client.components.DiffieHellmanGroup17Sha512JCE;
import com.sshtools.client.components.DiffieHellmanGroup18Sha512JCE;
import com.sshtools.client.components.DiffieHellmanGroup1Sha1JCE;
import com.sshtools.client.components.DiffieHellmanGroupExchangeSha1JCE;
import com.sshtools.client.components.DiffieHellmanGroupExchangeSha256JCE;
import com.sshtools.client.components.Rsa1024Sha1;
import com.sshtools.client.components.Rsa2048Sha256;
import com.sshtools.common.knownhosts.HostKeyVerification;
import com.sshtools.common.logger.Log;
import com.sshtools.common.nio.ConnectRequestFuture;
import com.sshtools.common.nio.DefaultSocketConnectionFactory;
import com.sshtools.common.nio.ProtocolEngine;
import com.sshtools.common.nio.SocketConnectionFactory;
import com.sshtools.common.nio.SshEngine;
import com.sshtools.common.nio.SshEngineContext;
import com.sshtools.common.ssh.AbstractRequestFuture;
import com.sshtools.common.ssh.ChannelFactory;
import com.sshtools.common.ssh.Connection;
import com.sshtools.common.ssh.ConnectionManager;
import com.sshtools.common.ssh.ForwardingManager;
import com.sshtools.common.ssh.GlobalRequestHandler;
import com.sshtools.common.ssh.SshContext;
import com.sshtools.common.ssh.components.ComponentFactory;
import com.sshtools.common.ssh.components.ComponentManager;
import com.sshtools.common.ssh.components.SshKeyExchange;
import com.sshtools.common.ssh.components.jce.JCEComponentManager;

/**
 * Holds the configuration for an SSH connection.
 */
public class SshClientContext extends SshContext {

	List<ClientAuthenticator> authenticators = new ArrayList<ClientAuthenticator>();

	BannerDisplay bannerDisplay;

	String username;

	TransportProtocolClient transport;
	
	Collection<ClientStateListener> stateListeners = new ArrayList<ClientStateListener>();
	
	int subsystemCacheSize = 65535 * 10;
	
	ChannelFactory<SshClientContext> channelFactory = new DefaultClientChannelFactory();
	
	Map<String, GlobalRequestHandler<SshClientContext>> globalRequestHandlers = Collections
			.synchronizedMap(new HashMap<String, GlobalRequestHandler<SshClientContext>>());

	SocketConnectionFactory socketConnectionFactory = new DefaultSocketConnectionFactory();
	
	private HostKeyVerification hkv = null;

	ForwardingManager<SshClientContext> forwardingManager;
	ConnectionManager<SshClientContext> connectionManager;
	
	static ForwardingManager<SshClientContext> defaultForwardingManager 
				= new ForwardingManager<SshClientContext>();
	static ConnectionManager<SshClientContext> defaultConnectionManager 
				= new ConnectionManager<SshClientContext>("client");
	
	public SshClientContext(SshEngine daemon, ComponentManager componentManager) throws IOException {
		super(componentManager);
		this.daemon = daemon;
	}
	
	public SshClientContext(SshEngine daemon) throws IOException {
		this(daemon, ComponentManager.getDefaultInstance());
	}

	public ProtocolEngine createEngine(ConnectRequestFuture connectFuture) throws IOException {
		return transport = new TransportProtocolClient(this, connectFuture);
	}
	
	public final SshEngine getEngine() {
		return daemon;
	}
	
	/**
	 * Set the username for this connection.
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Get the username of this connection.
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	public void addStateListener(ClientStateListener stateListener) {
		this.stateListeners.add(stateListener);
	}

	public Collection<ClientStateListener> getStateListeners() {
		return stateListeners;
	}
	
	@Override
	public ForwardingManager<SshClientContext> getForwardingManager() {
		return forwardingManager == null ? defaultForwardingManager : forwardingManager;
	}
	
	public void setForwardingManager(ForwardingManager<SshClientContext> forwardingManager) {
		this.forwardingManager = forwardingManager;
	}
	
	public void keysExchanged(boolean first) {
		if(first) {
			transport.startService(new AuthenticationProtocolClient(
					transport,
					SshClientContext.this,
					username));
		}
	}

	protected void configureKeyExchanges() {
		
		JCEComponentManager.getDefaultInstance().loadExternalComponents("kex-client.properties", keyExchanges);
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group-exchange-sha256",
				DiffieHellmanGroupExchangeSha256JCE.class)) {
			keyExchanges.add("diffie-hellman-group-exchange-sha256", DiffieHellmanGroupExchangeSha256JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group14-sha256", DiffieHellmanGroup14Sha256JCE.class)) {
			keyExchanges.add("diffie-hellman-group14-sha256", DiffieHellmanGroup14Sha256JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group15-sha512", DiffieHellmanGroup15Sha512JCE.class)) {
			keyExchanges.add("diffie-hellman-group15-sha512", DiffieHellmanGroup15Sha512JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group16-sha512", DiffieHellmanGroup16Sha512JCE.class)) {
			keyExchanges.add("diffie-hellman-group16-sha512", DiffieHellmanGroup16Sha512JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group17-sha512", DiffieHellmanGroup17Sha512JCE.class)) {
			keyExchanges.add("diffie-hellman-group17-sha512", DiffieHellmanGroup17Sha512JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group18-sha512", DiffieHellmanGroup18Sha512JCE.class)) {
			keyExchanges.add("diffie-hellman-group18-sha512", DiffieHellmanGroup18Sha512JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group14-sha1", DiffieHellmanGroup14Sha1JCE.class)) {
			keyExchanges.add("diffie-hellman-group14-sha1", DiffieHellmanGroup14Sha1JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("ecdh-sha2-nistp256", DiffieHellmanEcdhNistp256.class)) {
			keyExchanges.add("ecdh-sha2-nistp256", DiffieHellmanEcdhNistp256.class);
		}
		
		if (testClientKeyExchangeAlgorithm("ecdh-sha2-nistp384", DiffieHellmanEcdhNistp384.class)) {
			keyExchanges.add("ecdh-sha2-nistp384", DiffieHellmanEcdhNistp384.class);
		}

		if (testClientKeyExchangeAlgorithm("ecdh-sha2-nistp521", DiffieHellmanEcdhNistp521.class)) {
			keyExchanges.add("ecdh-sha2-nistp521", DiffieHellmanEcdhNistp521.class);
		}

		if (testClientKeyExchangeAlgorithm(Rsa2048Sha256.RSA_2048_SHA256, Rsa2048Sha256.class)) {
			keyExchanges.add(Rsa2048Sha256.RSA_2048_SHA256, Rsa2048Sha256.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group-exchange-sha1", DiffieHellmanGroupExchangeSha1JCE.class)) {
			keyExchanges.add("diffie-hellman-group-exchange-sha1", DiffieHellmanGroupExchangeSha1JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm("diffie-hellman-group1-sha1", DiffieHellmanGroup1Sha1JCE.class)) {
			keyExchanges.add("diffie-hellman-group1-sha1", DiffieHellmanGroup1Sha1JCE.class);
		}
		
		if (testClientKeyExchangeAlgorithm(Rsa1024Sha1.RSA_1024_SHA1, Rsa1024Sha1.class)) {
			keyExchanges.add(Rsa1024Sha1.RSA_1024_SHA1, Rsa1024Sha1.class);
		}

	}

	public boolean testClientKeyExchangeAlgorithm(String name, Class<? extends SshKeyExchange<? extends SshContext>> cls) {
		
		SshKeyExchange<? extends SshContext> c = null;
		try {

			c = cls.newInstance();

			if (!JCEComponentManager.getDefaultInstance().supportedDigests().contains(c.getHashAlgorithm()))
				throw new Exception("Hash algorithm " + c.getHashAlgorithm() + " is not supported");

			c.test();
		} catch (Exception e) {
			if(Log.isDebugEnabled())
				Log.debug("   " + name + " (client) will not be supported: " + e.getMessage());
			return false;
		} catch (Throwable e) {
			// a null pointer exception will be caught at the end of the keyex
			// call when transport.sendmessage is called, at this point the
			// algorithm has not thrown an exception so we ignore this excpected
			// exception.
		}

		if(Log.isDebugEnabled())
			Log.debug("   " + name + " (client) will be supported using JCE Provider "
					+ c.getProvider());

		return true;
	}

	public String getSupportedPublicKeys() {
		return listPublicKeys(supportedPublicKeys().toArray());
	}

	@Override
	public ComponentFactory<SshKeyExchange<?>> supportedKeyExchanges() {
		return keyExchanges;
	}

	@Override
	public String getPreferredPublicKey() {
		return prefPublicKey;
	}

	@Override
	public ConnectionManager<SshClientContext> getConnectionManager() {
		return connectionManager == null ? defaultConnectionManager : connectionManager;
	}

	public void setConnectionManager(
			ConnectionManager<SshClientContext> connectionManager) {
		this.connectionManager = connectionManager;
	}

	public void addAuthenticator(ClientAuthenticator auth) {
		authenticators.add(auth);
	}

	public List<ClientAuthenticator> getAuthenticators() {
		return authenticators;
	}

	public BannerDisplay getBannerDisplay() {
		return bannerDisplay;
	}

	public void setBannerDisplay(BannerDisplay bannerDisplay) {
		this.bannerDisplay = bannerDisplay;
	}

	public int getSubsystemCacheSize() {
		return subsystemCacheSize;
	}
	
	public void setSubsystemCacheSize(int subsystemCacheSize) {
		this.subsystemCacheSize = subsystemCacheSize;
	}

	@Override
	public ChannelFactory<SshClientContext> getChannelFactory() {
		return channelFactory;
	}

	@Override
	public SshEngineContext getDaemonContext() {
		return daemon.getContext();
	}

	public void addGlobalRequestHandler(GlobalRequestHandler<SshClientContext> handler) {
		for (int i = 0; i < handler.supportedRequests().length; i++) {
			globalRequestHandlers.put(handler.supportedRequests()[i], handler);
		}
	}

	@Override
	public GlobalRequestHandler<SshClientContext> getGlobalRequestHandler(String name) {
		return globalRequestHandlers.get(name);
	}

	@Override
	public SocketConnectionFactory getSocketConnectionFactory() {
		return socketConnectionFactory;
	}

	public int getSessionMaxPacketSize() {
		return sessionMaxPacketSize;
	}

	public int getSessionMaxWindowSize() {
		return sessionMaxWindowSize;
	}

	public HostKeyVerification getHostKeyVerification() {
		return hkv;
	}

	public void setHostKeyVerification(HostKeyVerification  hkv) {
		this.hkv = hkv;
	}

	public AbstractRequestFuture authenticate(Connection<?> con, PasswordAuthenticator authenticator) {

		if(transport==null) {
			throw new IllegalStateException("You cannot call authenticate until the connection has been established");
		}
		
		if(!(transport.getActiveService() instanceof AuthenticationProtocolClient)) {
			throw new IllegalStateException("You cannot call authenticate until the connection has been established");
		}
		
		((AuthenticationProtocolClient) transport.getActiveService()).doAuthentication(authenticator);
		return authenticator;
	}
}
