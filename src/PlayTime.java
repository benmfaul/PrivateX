import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;

public class PlayTime {

	public static void main(String[] args) throws Exception {
		new PlayTime();
	}
	
	public PlayTime() throws Exception {

		Server server = new Server(8080);
		
		SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler
		sh.setHandler(handler);
		
        Class exampleClass = Class.forName("HelloHandler");
        AbstractHandler ob = (AbstractHandler)exampleClass.newInstance();
		
		ContextHandler contextA = new ContextHandler("/hello");
	    contextA.setHandler(ob);
	    ContextHandler contextC = new ContextHandler("/xxx");
	    contextC.setHandler(ob);
	    
		ContextHandler contextB = new ContextHandler("/bye");
	    contextB.setHandler(new ByeHandler());

	    ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new AbstractHandler[] { contextA, contextB, contextC });
 
        server.setHandler(contexts);
 
	
		server.start();

	}

}