package com.Hisham.HelperComponents;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// find all the contigs from inputted k_mers
// involves file I/O

public class ContigGeneration {

    private static Map<String, List<String>> adjOut;

    private static final String READ_PATH = "..\\DataSets & Outputs\\Contig dataset_299_05.txt";
    private static final String WRITE_PATH = "..\\DataSets & Outputs\\Contig Output.txt";

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

    private static void DeBruijn_Graph_Construction(String[] k_mers) {
        adjOut = new LinkedHashMap<>();
        Map<String, List<String>> adjIn = new LinkedHashMap<>();

        for (String k_mer : k_mers) {
            String vertex = k_mer.substring(0, k_mer.length() - 1);
            String edge = k_mer.substring(1);

            List<String> set = adjOut.get(vertex);
            if (set != null) {
                set.add(edge);
                adjOut.put(vertex, set);
            } else {
                List<String> newSet = new ArrayList<>();
                newSet.add(edge);
                adjOut.put(vertex, newSet);
            }

            List<String> set2 = adjIn.get(edge);
            if (set2 != null) {
                set2.add(vertex);
                adjIn.put(edge, set2);
            } else {
                List<String> newSet = new ArrayList<>();
                newSet.add(vertex);
                adjIn.put(edge, newSet);
            }
        }
    }

    private static void assign_degrees(Map<String, List<String>> adjOut, Map<String, Integer> inDegree, Map<String, Integer> outDegree) {
        for (String out : adjOut.keySet()) {
            outDegree.put(out, adjOut.getOrDefault(out, new LinkedList<>()).size());
            for (String in : adjOut.get(out)) {
                inDegree.put(in, inDegree.getOrDefault(in, 0) + 1);
            }
        }
    }

    private static List<List<String>> generate_Contigs(Map<String, List<String>> adjOut) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, Integer> outDegree = new HashMap<>();
        assign_degrees(adjOut, inDegree, outDegree);

        List<List<String>> answer = new LinkedList<>();
        Set<String> marked = new HashSet<>();

        for (String v : adjOut.keySet()) {
            if (!(inDegree.getOrDefault(v, 0) == 1 && outDegree.getOrDefault(v, 0) == 1)) {
                if (outDegree.getOrDefault(v, 0) > 0) {
                    marked.add(v);
                    for (String out : adjOut.get(v)) {
                        List<String> path = new LinkedList<>();
                        path.add(v);
                        marked.add(out);
                        if (inDegree.getOrDefault(out, 0) == 1 && outDegree.getOrDefault(out, 0) == 1) {
                            path.add(out);
                            contig_traversal(adjOut, path, marked, out, inDegree, outDegree);
                        } else {
                            path.add(out);
                        }
                        answer.add(path);
                    }
                }
            }
        }

        // isolated cycles
        for (String str : adjOut.keySet()) {
            if (!marked.contains(str)) {
                List<String> path = new LinkedList<>();
                path.add(str);
                isolatedCycle_traversal(path, adjOut, str, marked);
                answer.add(path);
            }
        }
        return answer;
    }

    private static void contig_traversal(Map<String, List<String>> adjOut, List<String> path, Set<String> marked,
                                         String str, Map<String, Integer> inDegree, Map<String, Integer> outDegree) {
        for (String out : adjOut.get(str)) {
            marked.add(out);
            if (inDegree.getOrDefault(out, 0) == 1 && outDegree.getOrDefault(out, 0) == 1) {
                path.add(out);
                contig_traversal(adjOut, path, marked, out, inDegree, outDegree);
            } else {
                path.add(out);
                return;
            }
        }
    }

    private static void isolatedCycle_traversal(List<String> path, Map<String, List<String>> adjOut,
                                                String str, Set<String> marked) {
        for (String out : adjOut.get(str)) {
            if (!marked.contains(out)) {
                path.add(out);
                marked.add(out);
                isolatedCycle_traversal(path, adjOut, out, marked);
            }
        }
    }

    private static String GenomePath_Construction(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            sb.append(list.get(i).substring(list.get(i).length() - 1));
        }
        return sb.toString();
    }

    private static String find_Contigs(String[] k_mers) {
        DeBruijn_Graph_Construction(k_mers);
        List<List<String>> answer = generate_Contigs(adjOut);
        System.out.println(answer);
        StringBuilder sb = new StringBuilder();
        for (List<String> list : answer) {
            sb.append(GenomePath_Construction(list)).append(" ");
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String[] splits = readFileToArray(READ_PATH);
        write(find_Contigs(splits), WRITE_PATH);
    }
}