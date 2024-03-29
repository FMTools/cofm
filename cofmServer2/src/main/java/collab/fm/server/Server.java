package collab.fm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.controller.Controller;
import collab.fm.server.controller.EventHandler;

/**
 * @version 2.0
 * @author mark
 *
 */
public class Server {
	
	static Logger logger = Logger.getLogger(Server.class);
	
	private static final int PORT = 7000;
	
	private static void prepareDataBuffer() {
		ByteBuffer.setUseDirectBuffers(false);
		ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
	}
	
	private static IoAcceptor prepareServer() {
		IoAcceptor acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1,
				Executors.newCachedThreadPool());
		
		SocketAcceptorConfig cfg = (SocketAcceptorConfig)acceptor.getDefaultConfig();
		cfg.setReuseAddress(true);
		cfg.setThreadModel(ThreadModel.MANUAL);
		// Setup the data codec filter for MINA
		cfg.getFilterChain().addLast("codec", 
				new ProtocolCodecFilter(
						new TextLineEncoder(Charset.forName("UTF-8"), new LineDelimiter(Response.TERMINATOR)),
						new TextLineDecoder(Charset.forName("UTF-8"), new LineDelimiter(Request.TERMINATOR))));
		// TODO: Add a ExecutorFilter here.
		
		return acceptor;
	}
	
	public static IoAcceptor setup() {
		Controller.init();
		prepareDataBuffer();
		return prepareServer();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IoAcceptor theServer = Server.setup();
		
		try {
			InetSocketAddress addr = new InetSocketAddress(PORT);
			theServer.bind(addr, new EventHandler());
			logger.info("[fm] Server started at " + addr.toString() + ".");
		} catch (IOException e) {
			logger.fatal("Couldn't start server @ port " + PORT, e);
			theServer.unbindAll();
			//TODO: If a executor filter was built, shutdown the executor here
		}
	}

}
