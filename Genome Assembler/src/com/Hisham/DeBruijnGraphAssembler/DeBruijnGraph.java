package com.Hisham.DeBruijnGraphAssembler;

import java.util.*;

/* DeBruijn Graph Construction from 5396 reads */

public class DeBruijnGraph {

    private static final int TOTAL_K_MERS = 5396;

    private static Map<String, List<String>> DeBruijn_Graph_Construction(String[] k_mers) {

        Map<String, List<String>> adj = new LinkedHashMap<>();

        for (String k_mer : k_mers) {
            String vertex = k_mer.substring(0, k_mer.length() - 1);
            String edge = k_mer.substring(1);

            List<String> set = adj.get(vertex);
            if (set != null) {
                set.add(edge);
                adj.put(vertex, set);
            } else {
                Vector<String> newSet = new Vector<>();
                newSet.add(edge);
                adj.put(vertex, newSet);
            }
        }
        return adj;
    }

    private static void find_Assembled_Genome(String[] k_mers) {

        Map<String, List<String>> graph = DeBruijn_Graph_Construction(k_mers);
        System.out.println(graph);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] all = new String[TOTAL_K_MERS];
        for (int i = 0; i < TOTAL_K_MERS; i++) {
            all[i] = scanner.nextLine();
        }
        find_Assembled_Genome(all);
    }
}
