/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.util;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.annotation.TopologyChanged;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachelistener.event.TopologyChangedEvent;
import org.jboss.logging.Logger;

/**
 * An Infinispan listener that simply logs cache entries being created and
 * removed
 *
 * @author maping
 *
 */
@Listener
public class LoggingListener {

    private Logger log = Logger.getLogger(LoggingListener.class);

    @CacheEntryCreated
    public void observeAdd(CacheEntryCreatedEvent<String, String> event) {
        if (event.isPre()) {
            return;
        }
        System.out.println("####### 缓存条目增加: " + event.getKey() + " , " + event.getCache());

        log.infof("Cache entry %s added in cache %s", event.getKey(), event.getCache());
    }

    @CacheEntryModified
    public void observeUpdate(CacheEntryModifiedEvent<String, String> event) {
        if (event.isPre()) {
            return;
        }
        System.out.println("####### 缓存条目修改: " + event.getKey() + " , " + event.getCache());

        log.infof("Cache entry %s = %s modified in cache %s", event.getKey(), event.getValue(), event.getCache());
    }

    @CacheEntryRemoved
    public void observeRemove(CacheEntryRemovedEvent<String, String> event) {
        if (event.isPre()) {
            return;
        }
        System.out.println("####### 缓存条目删除: " + event.getKey() + " , " + event.getCache());

        log.infof("Cache entry %s removed in cache %s", event.getKey(), event.getCache());
    }

    @TopologyChanged
    public void observeTopologyChange(TopologyChangedEvent<String, String> event) {
        if (event.isPre()) {
            return;
        }
        System.out.println("####### 缓存节点增加新成员: " + event.getCache().getName() + " , " + event.getConsistentHashAtEnd().getMembers());

        log.infof("Cache %s topology changed, new membership is %s", event.getCache().getName(), event.getConsistentHashAtEnd().getMembers());
    }
}
