/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.marshaller;

/**
 *
 * @author maping
 */
import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;
import com.redhat.poc.jdg.bankofchina.model.Person;

public class PersonMarshaller implements MessageMarshaller<Person> {

    @Override
    public Class<? extends Person> getJavaClass() {
        return Person.class;
    }

    @Override
    public String getTypeName() {
        return "model.Person";
    }

    @Override
    public Person readFrom(
            org.infinispan.protostream.MessageMarshaller.ProtoStreamReader reader)
            throws IOException {

        return new Person(reader.readString("hairColor"),
                reader.readInt("age"),
                reader.readString("lastName"),
                reader.readString("firstName")
        );
    }

    @Override
    public void writeTo(
            org.infinispan.protostream.MessageMarshaller.ProtoStreamWriter writer,
            Person person) throws IOException {
        writer.writeString("hairColor", person.getHairColor());
        writer.writeInt("age", person.getAge());
        writer.writeString("lastName", person.getLastName());
        writer.writeString("firstName", person.getFirstName());

    }

}
