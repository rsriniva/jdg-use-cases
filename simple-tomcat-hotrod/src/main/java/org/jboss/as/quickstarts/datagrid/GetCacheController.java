package org.jboss.as.quickstarts.datagrid;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.client.hotrod.RemoteCache;

@WebServlet(name = "GetCacheController", urlPatterns = {"/getcache/*"})
public class GetCacheController extends HttpServlet {

    @Inject
    private RemoteCacheContainerProvider provider;
    
    private RemoteCache<String, Object> cache;
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String cacheName = request.getParameter("cache");
            System.out.println("Looking up cache===>"+cacheName);
            
            cache = provider.getCacheContainer().getCache(cacheName);
            System.out.println("Found remote cache===>"+cache);

            final PrintWriter out = response.getWriter();

            out.println("Cache size is : \n" + cache.size());
            
            out.println("The following keys exist in the cache...\n");
            
            Set<String> keys = cache.keySet();
			for (String key : keys) {
			   out.println(key);
			}
        }
        
    }
