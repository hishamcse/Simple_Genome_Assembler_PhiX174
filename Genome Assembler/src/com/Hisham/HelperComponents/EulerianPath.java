package com.Hisham.HelperComponents;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

// involves file I/O
// input file will look like this : 2->3->4->1

public class EulerianPath {

    private static final String READ_PATH = "..\\DataSets & Outputs\\test.txt";
    private static final String WRITE_PATH = "..\\DataSets & Outputs\\test out.txt";

    private final Stack<Integer> path;

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


    public EulerianPath(ArrayList<Integer>[] adjIn, ArrayList<Integer>[] adjOut) {

        int s = nonIsolatedVertex(adjOut);
        for (int v = 0; v < adjOut.length; v++) {
            if (adjOut[v].size() > adjIn[v].size()) {
                s = v;
            }
        }
        if (s == -1) {
            s = 0;
        }

        Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[adjOut.length];
        for (int v = 0; v < adjOut.length; v++)
            adj[v] = adjOut[v].iterator();

        Stack<Integer> stack = new Stack<>();
        stack.push(s);
        path = new Stack<>();
        while (!stack.isEmpty()) {
            int v = stack.pop();
            while (adj[v].hasNext()) {
                stack.push(v);
                v = adj[v].next();
            }
            path.push(v);
        }
    }

    private int nonIsolatedVertex(ArrayList<Integer>[] adjOut) {
        for (int v = 0; v < adjOut.length; v++) {
            if (adjOut[v].size() > 0) {
                return v;
            }
        }
        return -1;
    }

    public boolean hasEulerianPath() {
        return path != null;
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
        ArrayList<Integer>[] adjIn = (ArrayList<Integer>[]) new ArrayList[n];
        ArrayList<Integer>[] adjOut = (ArrayList<Integer>[]) new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjIn[i] = new ArrayList<>();
            adjOut[i] = new ArrayList<>();
        }
        for (String split : splits) {
            String[] from = split.split(" -> ");
            int x, y;
            x = Integer.parseInt(from[0]);
            String[] to = from[1].split(",");
            for (String s : to) {
                y = Integer.parseInt(s);
                adjOut[x].add(y);
                adjIn[y].add(x);
            }
        }

        EulerianPath eulerianPath = new EulerianPath(adjIn, adjOut);
        if (!eulerianPath.hasEulerianPath()) {
            System.out.println(0);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = eulerianPath.path.size() - 1; i >= 0; i--) {
                sb.append(eulerianPath.path.get(i)).append("->");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            System.out.println(sb.toString());
            write(sb.toString(), WRITE_PATH);
        }
    }
}