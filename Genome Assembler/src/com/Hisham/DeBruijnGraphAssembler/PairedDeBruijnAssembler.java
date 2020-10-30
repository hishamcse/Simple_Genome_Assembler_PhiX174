package com.Hisham.DeBruijnGraphAssembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// lets imagine the dataset is tip and bubble free. Involves file I/O
// input file will look like this : 4 2
//                                  AAC|GTT
//                                  CCT|AGT
// .........

public class PairedDeBruijnAssembler {

    private static Map<String, List<String>> adjOut;
    private static Map<String, List<String>> adjIn;

    private static Stack<String> path = null;

    private static final String READ_PATH = "..\\DataSets & Outputs\\test.txt";
    private static final String WRITE_PATH = "..\\DataSets & Outputs\\test out.txt";

    static class Vertex {
        String str1;
        String str2;

        public Vertex(String str1, String str2) {
            this.str1 = str1;
            this.str2 = str2;
        }
    }

    public static String[] readFileToArray(String path) throws IOException {
        String file = Files.readString(Path.of(path), StandardCharsets.UTF_8);
        return file.split("\r\n");
    }

    public static void write(String str, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(str);
        writer.flush();
        writer.close();
    }

    private static void DeBruijn_Graph_Construction(List<Vertex> k_mers, int k) {
        adjOut = new LinkedHashMap<>();
        adjIn = new LinkedHashMap<>();

        for (Vertex k_mer : k_mers) {
            String from1 = k_mer.str1.substring(0, k - 1);
            String from2 = k_mer.str2.substring(0, k - 1);
            String to1 = k_mer.str1.substring(1);
            String to2 = k_mer.str2.substring(1);

            String from = from1 + "|" + from2;
            String to = to1 + "|" + to2;

            List<String> set = adjOut.get(from);
            if (set != null) {
                set.add(to);
                adjOut.put(from, set);
            } else {
                List<String> newSet = new ArrayList<>();
                newSet.add(to);
                adjOut.put(from, newSet);
            }

            List<String> set2 = adjIn.get(to);
            if (set2 != null) {
                set2.add(from);
                adjIn.put(to, set2);
            } else {
                List<String> newSet = new ArrayList<>();
                newSet.add(from);
                adjIn.put(to, newSet);
            }
        }
    }

    public static void EulerianPath(Map<String, List<String>> adjIn, Map<String, List<String>> adjOut) {
        String s = nonIsolatedVertex(adjOut);
        for (String v : adjOut.keySet()) {
            if (adjIn.get(v) == null || adjOut.get(v).size() > adjIn.get(v).size()) {
                s = v;
            }
        }

        Map<String, Iterator<String>> adj = new LinkedHashMap<>();
        for (String v : adjOut.keySet()) {
            adj.put(v, adjOut.get(v).iterator());
        }

        Stack<String> stack = new Stack<>();
        stack.push(s);
        path = new Stack<>();
        while (!stack.isEmpty()) {
            String v = stack.pop();
            while (adj.get(v) != null && adj.get(v).hasNext()) {
                stack.push(v);
                v = adj.get(v).next();
            }
            path.push(v);
        }
    }

    private static String nonIsolatedVertex(Map<String, List<String>> adjOut) {
        for (String v : adjOut.keySet()) {
            if (adjOut.get(v).size() > 0) {
                return v;
            }
        }
        return null;
    }

    private static String Reconstruct_From_Prefixes(List<Vertex> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            sb.append(list.get(i).str1.charAt(0));
        }
        sb.append(list.get(list.size() - 1).str1);
        return sb.toString();
    }

    private static String Reconstruct_From_Suffixes(List<Vertex> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size() - 1; i++) {
            sb.append(list.get(i).str2.charAt(0));
        }
        sb.append(list.get(list.size() - 1).str2);
        return sb.toString();
    }

    private static String find_Reconstructed_String(List<Vertex> list, int k, int d) {
        String pref = Reconstruct_From_Prefixes(list);
        String suf = Reconstruct_From_Suffixes(list);
        return pref + suf.substring(suf.length() - k - d);
    }

    private static List<Vertex> Vertex_Construction(List<String> path) {
        List<Vertex> vertices = new ArrayList<>();
        for (int i = path.size() - 1; i >= 0; i--) {
            String[] strings = path.get(i).split("\\|");
            vertices.add(new Vertex(strings[0], strings[1]));
        }
        return vertices;
    }

    private static String find_Assembled_Genome(List<Vertex> k_mers, int k, int d) {
        DeBruijn_Graph_Construction(k_mers, k);
        EulerianPath(adjIn, adjOut);
        List<Vertex> vertices = Vertex_Construction(path);
        return find_Reconstructed_String(vertices, k, d);
    }

    public static void main(String[] args) throws IOException {
        String[] splits = readFileToArray(READ_PATH);
        String[] numbers = splits[0].split(" ");
        int k = Integer.parseInt(numbers[0]);
        int d = Integer.parseInt(numbers[1]);
        System.out.println(k + " " + d);
        List<Vertex> list = new ArrayList<>();
        for (int i = 1; i < splits.length; i++) {
            String[] strings = splits[i].split("\\|");
            Vertex vertex = new Vertex(strings[0], strings[1]);
            System.out.println(strings[0] + " " + strings[1]);
            list.add(vertex);
        }
        write(find_Assembled_Genome(list, k, d), WRITE_PATH);
    }
}