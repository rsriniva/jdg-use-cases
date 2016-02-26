package org.jboss.as.quickstarts.datagrid;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.inject.Inject;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.client.hotrod.RemoteCache;

@WebServlet(name = "PutCacheController", urlPatterns = {"/putcache/*"})
public class PutCacheController extends HttpServlet {

    @Inject
    private RemoteCacheContainerProvider provider;
    
    private RemoteCache<String, Object> cache;
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String cacheName = request.getParameter("cache");
            String count = request.getParameter("count");
            
            System.out.println("Putting " + Integer.parseInt(count) +" items into cache===>"+cacheName);
            
            cache = provider.getCacheContainer().getCache(cacheName);
            
            for (int i = 0; i < Integer.parseInt(count); i++) {
			cache.put(genRand(),genRand());	
			try
			{		
				Thread.sleep(2);
			}
			catch (java.lang.InterruptedException intr)
			{
				System.out.println("Interrupt Exception "+ intr);
			}
		}

            final PrintWriter out = response.getWriter();

            out.println("Added " + count + " items into cache");
            
        }
        
        private String genRand()
        {
			return UUID.randomUUID().toString();
		}
        
    }
