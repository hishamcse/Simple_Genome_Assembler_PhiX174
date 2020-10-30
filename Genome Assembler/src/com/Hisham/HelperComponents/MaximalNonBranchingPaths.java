package com.Hisham.HelperComponents;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// finds maximal non branching paths .involves file I/O

public class MaximalNonBranchingPaths {

    private static final String READ_PATH = "..\\DataSets & Outputs\\Maximal Non-Branching dataset.txt";
    private static final String WRITE_PATH = "..\\DataSets & Outputs\\Maximal Non-Branching Output.txt";

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

    private static void assign_degrees(Map<Integer, List<Integer>> adjOut, Map<Integer, Integer> inDegree,
                                       Map<Integer, Integer> outDegree) {
        for (Integer out : adjOut.keySet()) {
            outDegree.put(out, adjOut.getOrDefault(out, new LinkedList<>()).size());
            for (Integer in : adjOut.get(out)) {
                inDegree.put(in, inDegree.getOrDefault(in, 0) + 1);
            }
        }
    }

    private static List<List<Integer>> maximal_non_branching(Map<Integer, List<Integer>> adjOut) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, Integer> outDegree = new HashMap<>();
        assign_degrees(adjOut, inDegree, outDegree);

        List<List<Integer>> answer = new LinkedList<>();
        Set<Integer> marked = new HashSet<>();

        for (Integer v : adjOut.keySet()) {
            if (!(inDegree.getOrDefault(v, 0) == 1 && outDegree.getOrDefault(v, 0) == 1)) {
                if (outDegree.getOrDefault(v, 0) > 0) {
                    marked.add(v);
                    for (Integer out : adjOut.get(v)) {
                        List<Integer> path = new LinkedList<>();
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
        for (Integer i : adjOut.keySet()) {
            if (!marked.contains(i)) {
                List<Integer> path = new LinkedList<>();
                path.add(i);
                isolatedCycle_traversal(path, adjOut, i, marked);
                answer.add(path);
            }
        }
        return answer;
    }

    private static void contig_traversal(Map<Integer, List<Integer>> adjOut, List<Integer> path, Set<Integer> marked,
                                         int i, Map<Integer, Integer> inDegree, Map<Integer, Integer> outDegree) {
        for (Integer out : adjOut.get(i)) {
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

    private static void isolatedCycle_traversal(List<Integer> path, Map<Integer, List<Integer>> adjOut,
                                                int i, Set<Integer> marked) {
        for (Integer out : adjOut.get(i)) {
            if (!marked.contains(out)) {
                path.add(out);
                marked.add(out);
                isolatedCycle_traversal(path, adjOut, out, marked);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String[] splits = readFileToArray(READ_PATH);
        int n;
        int max = 0;
        for (String split : splits) {
            String[] from = split.split(" -> ");
            if (Integer.parseInt(from[0]) > max) {
                max = Integer.parseInt(from[0]);
            }
        }
        n = max + 1;
        Map<Integer,List<Integer>> adjOut = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            adjOut.put(i,new ArrayList<>());
        }
        for (String split : splits) {
            String[] from = split.split(" -> ");
            int x, y;
            x = Integer.parseInt(from[0]);
            String[] to = from[1].split(",");
            for (String s : to) {
                y = Integer.parseInt(s);
                adjOut.get(x).add(y);
            }
        }

        StringBuilder sb = new StringBuilder();
        List<List<Integer>> contigs_list = maximal_non_branching(adjOut);
        for(List<Integer> list:contigs_list){
            if(list.size()>1) {
                for (int i : list) {
                    sb.append(i).append(" -> ");
                }
            }
            sb.delete(sb.length()-3,sb.length()-1).append("\n");
        }
        write(sb.toString(),WRITE_PATH);
    }
}
