/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.function;

import com.redhat.poc.jdg.bankofchina.model.Person;
import com.redhat.poc.jdg.bankofchina.model.PersonDatabase;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;

/**
 * 缓存数据查询测试
 *
 * @author maping
 */
import static org.junit.Assert.*;

public class TestCase46Local {

    public static void main(String[] args) {

        Cache<Integer, Person> cache = getCache();
        try {
            // Load the cache with people
            List<Person> people = new PersonDatabase().getPeople();
            Random random = new Random(System.currentTimeMillis());
            for (Person person : people) {
                cache.put(random.nextInt(1000), person);
            }

            SearchManager searchManager = Search.getSearchManager(cache);

            // query for blondes
            Query query = searchManager.buildQueryBuilderForClass(Person.class)
                    .get().keyword().onField("hairColor").matching("blonde")
                    .createQuery();

            List<Object> blondes = searchManager.getQuery(query, Person.class)
                    .list();
            assertEquals(2, blondes.size());
            System.out.println("##### 查询 hairColor=blonde 的Person: \n" + blondes);

            // query for people who are 45 or older
            query = searchManager.buildQueryBuilderForClass(Person.class).get()
                    .range().onField("age").above(45).createQuery();

            List<Object> adults = searchManager.getQuery(query, Person.class)
                    .list();
            assertEquals(3, adults.size());
            System.out.println("##### 查询 age>=45 的Person: \n" + adults);

            // query for people whose first name has 'a' as second letter and age < 40
            QueryBuilder qb = searchManager.buildQueryBuilderForClass(Person.class).get();
            query = qb
                    .bool()
                    .must(
                            qb.keyword().wildcard().onField("firstName").matching("?a*").createQuery()
                    )
                    .must(
                            qb.range().onField("age").below(40).excludeLimit().createQuery()
                    )
                    .createQuery();
            List<Object> ms = searchManager.getQuery(query, Person.class).list();

            assertEquals(3, ms.size());
            System.out.println("##### 查询 firstNameage like '?a*' 并且 age<40 的Person: \n" + ms);

        } finally {
            cache.getCacheManager().stop();
        }
    }

    public static Cache<Integer, Person> getCache() {
        Properties props = new Properties();
        props.put("default.directory_provider", "ram");
        
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.indexing().enable()
                .indexLocalOnly(false)
                .withProperties(props);
        return new DefaultCacheManager(builder.build()).getCache();
    }

}
