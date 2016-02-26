/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.poc.jdg.bankofchina.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * 根据-s userinfo文件开始 -e userinfo文件结束,生成userid.csv文件
 * 读取/opt/jbshome/data/userinfo/目录下csv文件
 *
 * @author maping
 */
public class GenerateUserIdCsv {

    private static final String CSV_FILE_PATH = "/opt/jbshome/data/userinfo/";
    private static int csvFileStart = 591;
    private static int csvFileEnd = 595;
    private static List<String[]> userIdList = new ArrayList<String[]>();

    public static void main(String[] args) throws Exception {
        CommandLine commandLine;
        Options options = new Options();
        options.addOption("s", true, "The start csv file number option");
        options.addOption("e", true, "The end csv file number option");
        BasicParser parser = new BasicParser();
        parser.parse(options, args);
        commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption("s")) {
                String start = commandLine.getOptionValue("s");
                if (start != null && start.length() > 0) {
                    csvFileStart = Integer.parseInt(start);
                }
            }
            if (commandLine.hasOption("e")) {
                String end = commandLine.getOptionValue("e");
                if (end != null && end.length() > 0) {
                    csvFileEnd = Integer.parseInt(end);
                }
            }
        }

        for (int i = csvFileStart; i <= csvFileEnd; i++) {
            ReadCsvFile(i);
        }

        System.out.println();
        System.out.println("%%%%%%%%% 开始为 " + csvFileStart + "-" + csvFileEnd + " 生成 userid.csv 文件,供随机读写性能测试使用 %%%%%%%%%");
        System.out.println("%%%%%%%%% userid.csv 共有 " + userIdList.size() + " 条记录 %%%%%%%%%");
        CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH + "userid.csv"));
        writer.writeAll(userIdList);
        writer.flush();
        writer.close();
    }

    private static void ReadCsvFile(int fileNumber) throws Exception {
        System.out.println("####### 开始 读取 " + fileNumber + ".csv 文件. ");
        CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH + fileNumber + ".csv"));
        String[] nextLine;
        int count = 0;
        while ((nextLine = reader.readNext()) != null) {
            if (count == 0) {
            } else {
                // nextLine[] is an array of values from the line
                String userId = nextLine[0];
                userIdList.add(new String[]{userId});
            }
            count++;
        }

        System.out.println("####### 结束 把 " + fileNumber + ".csv中的userId数据放入UserIdList");
    }

}
