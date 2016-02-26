package com.opennaru.jdg.example;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class HotRodClient {

    public static void main(String args[]) throws Exception {
        RemoteCache<String, Object> cache;

        RemoteCacheManager cacheManager = new RemoteCacheManager("IP_LIST_OF_JDG");
        cache = cacheManager.getCache("namedCache");

        System.out.println("cache=" + cache);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out
                    .println("COMMAND=put,get,remove,size,clear,putWithTime,putIfAbsent");
            System.out.print(">> ");
            String cmd = scanner.nextLine();
            cmd = cmd.toUpperCase();

            if (cmd.equals("PUT")) {
                System.out.print("KEY=");
                String key = scanner.nextLine();
                System.out.print("VALUE=");
                String value = scanner.nextLine();

                cache.put(key, value);

            } else if (cmd.equals("GET")) {
                System.out.print("KEY=");
                String key = scanner.nextLine();

                System.out.println("VALUE=" + cache.get(key));

            } else if (cmd.equals("REMOVE")) {
                System.out.print("KEY=");
                String key = scanner.nextLine();
                cache.remove(key);

            } else if (cmd.equals("CLEAR")) {
                cache.clear();

            } else if (cmd.equals("SIZE")) {
                System.out.println("SIZE=" + cache.size());

            } else if (cmd.equals("PUTWITHTIME")) {
                System.out.print("KEY=");
                String key = scanner.nextLine();
                System.out.print("VALUE=");
                String value = scanner.nextLine();

                cache.put("key", "value", 5, TimeUnit.SECONDS);
            } else if (cmd.equals("PUTIFABSENT")) {
                System.out.print("KEY=");
                String key = scanner.nextLine();
                System.out.print("VALUE=");
                String value = scanner.nextLine();

                cache.putIfAbsent(key, value);
            }

        }
    }
}
