package com.klimov;


import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String o = "";
        String p = "";
        boolean a = false;
        boolean statistics = true;
        List<String> files = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o")) {
                o = args[i + 1];
            }
            if (args[i].equals("-s")) {
                statistics = true;
            } else if (args[i].equals("-f")) {
                statistics = false;
            }
            if (args[i].equals("-a")) {
                a = true;
            }
            if (args[i].equals("-p")) {
                p = args[i + 1];
            }
            Pattern pattern = Pattern.compile(".txt");
            Matcher matcher = pattern.matcher(args[i]);
            while (matcher.find()) {
                files.add(args[i]);
            }
        }

        if (files.size() > 0) {
            dataProcessing(o, p, files, a, statistics);
        } else {
            System.out.println("Error: Not found files .txt");
        }
    }

    public static void dataProcessing(String o, String p, List<String> files, boolean a, boolean statistics) {
        int countInt = 0;
        int countFloat = 0;
        int countString = 0;
        int minString = 0;
        int maxString = 0;
        List<String> doubles = new ArrayList<>();
        List<BigDecimal> doubles2 = new ArrayList<>();
        List<Integer> strings = new ArrayList<>();
        List<String> list = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            try (FileInputStream fis = new FileInputStream(files.get(i));
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(isr)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String data = line;
                    byte[] array = data.getBytes("UTF-8");
                    String str = new String(array, "UTF-8");
                    list.add(str);
                }
            } catch (IOException er) {
                System.out.println("File is not found");
            }
        }

        for (String str1 : list) {
            try {
                if (isDouble(str1)) {
                    Path floatFile = Paths.get(o + p + "floats.txt");
                    if (!Files.exists(floatFile)) {
                        Files.createFile(floatFile);
                    }
                } else if (isDigit(str1)) {
                    Path integerFile = Paths.get(o + p + "integers.txt");
                    if (!Files.exists(integerFile)) {
                        Files.createFile(integerFile);
                    }
                } else {
                    Path stringFile = Paths.get(o + p + "strings.txt");
                    if (!Files.exists(stringFile)) {
                        Files.createFile(stringFile);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error");
            }
        }

        if (Files.exists(Paths.get(o + p + "integers.txt"))) {
            try {
                FileWriter writer = new FileWriter(o + p + "integers.txt", a);
                for (String str : list) {
                    if (isDigit(str)) {
                        writer.write(str + "\n");
                        countInt++;
                        doubles.add(str);
                    }
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("write Error integer");
            }
        }

        if (Files.exists(Paths.get(o + p + "floats.txt"))) {
            try {
                FileWriter writer = new FileWriter(o + p + "floats.txt", a);
                for (String str : list) {
                    if (isDouble(str)) {
                        writer.write(str + "\n");
                        countFloat++;
                        doubles.add(str);
                    }
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("write Error float");
            }
        }

        if (Files.exists(Paths.get(o + p + "strings.txt"))) {
            try {
                FileWriter writer = new FileWriter(o + p + "strings.txt", a);
                for (String str : list) {
                    if (!(isDigit(str) || isDouble(str) || str.isEmpty())) {
                        writer.write(str + "\n");
                        countString++;
                        strings.add(str.length());
                    }
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("write Error string");
            }
        }

        BigDecimal sum = new BigDecimal("0");
        for (String sum1 : doubles) {
            BigDecimal res = new BigDecimal(sum1);
            sum = sum.add(res);
            doubles2.add(res);
        }

        if (strings.size() > 0) {
            minString = Collections.min(strings);
            maxString = Collections.max(strings);
        }
        System.out.println("Statistics: " +
                "\nFloatCount: " + countFloat +
                "\nStringCount: " + countString +
                "\nIntegerCount: " + countInt);
        if (statistics == false) {
            System.out.println(
                    "ValueMax: " + Collections.max(doubles2) +
                            "\nValueMin: " + Collections.min(doubles2) +
                            "\nValuesSum: " + sum +
                            "\nAverageValue: " + sum.divide(BigDecimal.valueOf(6), RoundingMode.CEILING) +
                            "\nMinLenghtString: " + minString +
                            "\nMaxLenghtString: " + maxString);
        }

    }

    public static boolean isDigit(String s) {
        return s.matches("-?\\d+");
    }

    public static boolean isDouble(String s) {
        return s.matches("^[-+]?[0-9]*[.,][0-9]+(?:[eE][-+]?[0-9]+)?$");
    }
}

