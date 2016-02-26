/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.model;

import java.io.Serializable;

/**
 *
 * @author maping
 */
public class Data1M implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] data;

    public Data1M() {
        this.data = new byte[1024 * 1024];
        for (int i = 0; i < 1024 * 1024; i++) {
            data[i] = 0;
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
