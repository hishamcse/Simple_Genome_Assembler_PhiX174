package com.Hisham.OverlapGraphAssembler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/* Overlap Graph Construction from 1618 reads */

public class OverlapGraph {

    private static final int MER_SIZE = 12;
    private static final int TOTAL_ERROR_FREE_READ = 1618;

    static class Vertex implements Comparable<Vertex> {
        int id;
        String read;
        int weight;

        public Vertex(int id, String read, int weight) {
            this.id = id;
            this.read = read;
            this.weight = weight;
        }

        @Override
        public int compareTo(Vertex o) {
            return Integer.compare(o.weight, this.weight);
        }
    }

    private static int overLapLength(String s1, String s2) {
        int len = 0;
        int suffixId = 0, prefixId = 0;

        while (prefixId < s1.length()) {
            int id = prefixId;
            while (id < s1.length() && s1.charAt(id++) == s2.charAt(suffixId++)) {
                len++;
            }
            if (id == s1.length()) {
                break;
            }
            len = 0;
            suffixId = 0;
            prefixId++;
        }
        return len;
    }

    public static ArrayList<Vertex>[] buildSortedOverlapGraph(Vertex[] vertices) {

        ArrayList<Vertex>[] adj = (ArrayList<Vertex>[]) new ArrayList[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            adj[i] = new ArrayList<>();
        }
        for (int i = 0; i < vertices.length - 1; i++) {
            for (int j = i + 1; j < vertices.length; j++) {
                int len = overLapLength(vertices[i].read, vertices[j].read);
                if (len > MER_SIZE) {
                    adj[i].add(new Vertex(j, vertices[j].read, len));
                }
                int revLen = overLapLength(vertices[j].read, vertices[i].read);
                if (revLen > MER_SIZE) {
                    adj[j].add(new Vertex(i, vertices[i].read, revLen));
                }
            }
        }

        for (ArrayList<Vertex> list : adj) {
            Collections.sort(list);
        }

        return adj;
    }

    public static void main(String[] args) throws IOException {
        FastScanner scanner = new FastScanner();
        Vertex[] vertices = new Vertex[TOTAL_ERROR_FREE_READ];
        for (int i = 0; i < TOTAL_ERROR_FREE_READ; i++) {
            vertices[i] = new Vertex(i, scanner.next(), 0);
        }
        ArrayList<Vertex>[] graph = buildSortedOverlapGraph(vertices);
        System.out.println(Arrays.toString(graph));
    }

    static class FastScanner {

        private final BufferedReader reader;
        private StringTokenizer tokenizer;

        public FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        public String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }
    }
}