/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author maping
 */

public class BeerDatabase {

    private List<Person> persons = new ArrayList<Person>();

    public BeerDatabase() {
        persons.add(new Person("brown", 45, "Doe", "John"));
        persons.add(new Person("red", 39, "Doe", "Jane"));
        persons.add(new Person("brown", 24, "Smith", "Margaret"));
        persons.add(new Person("blonde", 29, "McLeod", "Dude"));
        persons.add(new Person("black", 63, "Marigold", "Judith"));
        persons.add(new Person("black", 75, "Hargroves", "Jane"));
        persons.add(new Person("blonde", 12, "Smith", "Joel"));
        persons.add(new Person("red", 18, "Spears", "Matthew"));
    }

    public List<Person> getPeople() {
        return persons;
    }

}
