package collab.fm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.mina.common.ByteBuffer; 
import org.apache.mina.common.IoAcceptor; 
import org.apache.mina.common.SimpleByteBufferAllocator; 
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.*;
import org.apache.mina.transport.socket.nio.SocketAcceptor; 
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import collab.fm.server.controller.*;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;

public class Server {
	
	private static final int PORT = 8000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ByteBuffer.setUseDirectBuffers(false);
		ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
		
		IoAcceptor acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1,
				Executors.newCachedThreadPool());
		SocketAcceptorConfig cfg = (SocketAcceptorConfig)acceptor.getDefaultConfig();
		cfg.setReuseAddress(true);
		cfg.setThreadModel(ThreadModel.MANUAL);
		cfg.getFilterChain().addLast("codec", 
				new ProtocolCodecFilter(
						new TextLineEncoder(Charset.forName("UTF-8"), new LineDelimiter(Response.TERMINATOR)),
						new TextLineDecoder(Charset.forName("UTF-8"), new LineDelimiter(Request.TERMINATOR))));
		//TODO: setup controller
		Controller controller = null; 
		///~
		try {
			acceptor.bind(new InetSocketAddress(PORT), new EventHandler(controller));
		} catch (IOException e) {
			e.printStackTrace();
			acceptor.unbindAll();
			//TODO: if a executor filter is built, shutdown the executor here
		}
	}

}
