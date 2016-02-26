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
import com.redhat.poc.jdg.bankofchina.model.UserBaseInfo;
import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;

public class UserBaseInfoMarshaller implements MessageMarshaller<UserBaseInfo> {

    @Override
    public Class<? extends UserBaseInfo> getJavaClass() {
        return UserBaseInfo.class;
    }

    @Override
    public String getTypeName() {
        return "model.UserBaseInfo";
    }

    @Override
    public UserBaseInfo readFrom(
            org.infinispan.protostream.MessageMarshaller.ProtoStreamReader reader)
            throws IOException {

        return new UserBaseInfo(reader.readString("userId"),
                reader.readString("imsi"),
                reader.readString("msisdn"),
                reader.readString("homeCity"),
                reader.readString("homeCountry"),
                reader.readString("testFlag")
        );
    }

    @Override
    public void writeTo(
            org.infinispan.protostream.MessageMarshaller.ProtoStreamWriter writer,
            UserBaseInfo user) throws IOException {
        writer.writeString("userId", user.getUserId());
        writer.writeString("imsi", user.getImsi());
        writer.writeString("msisdn", user.getMsisdn());
        writer.writeString("homeCity", user.getHomeCity());
        writer.writeString("testFlag", user.getTestFlag());
    }

}
