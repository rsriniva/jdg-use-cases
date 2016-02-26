/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author maping
 */
@Indexed
public class Person {

    @Field
    private String hairColor;
    @Field
    private int age;
    @Field
    private String lastName;
    @Field
    private String firstName;

    public Person(String hairColor, int age, String lastName, String firstName) {

        this.hairColor = hairColor;
        this.age = age;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);

        sb.append(firstName + " ");
        sb.append(lastName + " is ");
        sb.append(age + " and has ");
        sb.append(hairColor + " hair.");

        return sb.toString();
    }
}
