package server;


import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import org.apache.asyncweb.common.codec.HttpCodecFactory;

public class Main {
	private static final int PORT = 7000;
	
	public static void main(String[] args) throws Exception {
		
		//Create the server.
        SocketAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new HttpCodecFactory()));

        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setReuseAddress(true);
        acceptor.getSessionConfig().setReceiveBufferSize(1024);
        acceptor.getSessionConfig().setSendBufferSize(1024);
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.setBacklog(10240);

        acceptor.setHandler(new HttpHandler());
        acceptor.bind(new InetSocketAddress(PORT));
        
        System.out.println("Server started.");
    }
}
