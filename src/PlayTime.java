import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;

import com.google.gson.Gson;

public class PlayTime {

	public static void main(String[] args) throws Exception {
		new PlayTime();
	}
	
	public PlayTime() throws Exception {

		Server server = new Server(8080);
		List<AbstractHandler> clist = new ArrayList();
		byte[] encoded = Files.readAllBytes(Paths.get("config.json"));
		String data = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
		
		Gson gson = new Gson();
		Map m = gson.fromJson(data,Map.class);
		
		List list = (List)m.get("web");
		SessionHandler sh = null;
		for (int i = 0; i < list.size(); i++) {
			m = (Map)list.get(i);
			Class x = Class.forName((String)m.get("className"));
			AbstractHandler h = (AbstractHandler)x.newInstance();
			List<String> contextNames = (List<String>)m.get("context");
			for (String name : contextNames) {
				ContextHandler context = new ContextHandler(name);
				context.setContextPath(name);
				context.setResourceBase((String)m.get("base"));
				context.setHandler(h);
				
					sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler
					sh.setHandler(context);
					clist.add(sh);  
					
			}
		}
		AbstractHandler handlers[] = new AbstractHandler[clist.size()];
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		for (int i = 0; i < clist.size();i++) {
			handlers[i] = clist.get(i);
		}
		contexts.setHandlers(handlers);
        server.setHandler(contexts);
 
		server.start();

	}

}