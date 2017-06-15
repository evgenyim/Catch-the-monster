package com.example.user.catchthemonster; /**
 * Created by User on 05.06.2017.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class bfs {
    public static void bfs(int n, int monster_index,int character_index,ArrayList<Integer> way, int pit_square_number, ArrayList<Integer> obstacle) {
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                graph.add(new ArrayList<Integer>());
            }
        }
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j++) {
                int base_vertex = number(i, j, n);
                if (check(i - 1, j - 1, n)){
                    if (! obstacle.contains(number(i - 1, j - 1, n)))
                        graph.get(base_vertex).add(number(i - 1, j - 1, n));
                }
                if (check(i, j - 1, n)){
                    if (! obstacle.contains(number(i, j - 1, n)))
                        graph.get(base_vertex).add(number(i, j - 1, n));
                }
                if (check(i + 1, j - 1, n)){
                    if (! obstacle.contains(number(i + 1, j - 1, n)))
                        graph.get(base_vertex).add(number(i + 1, j - 1, n));
                }
                if (check(i - 1, j, n)){
                    if (! obstacle.contains(number(i - 1, j, n)))
                        graph.get(base_vertex).add(number(i - 1, j, n));
                }
                if (check(i + 1, j, n)){
                    if (! obstacle.contains(number(i + 1, j, n)))
                        graph.get(base_vertex).add(number(i + 1, j, n));
                }
                if (check(i - 1, j + 1, n)){
                    if (! obstacle.contains(number(i - 1, j + 1, n)))
                        graph.get(base_vertex).add(number(i - 1, j + 1, n));
                }
                if (check(i, j + 1, n)){
                    if (! obstacle.contains(number(i, j + 1, n)))
                        graph.get(base_vertex).add(number(i, j + 1, n));
                }
                if (check(i + 1, j + 1, n)){
                    if (! obstacle.contains(number(i + 1, j + 1, n)))
                        graph.get(base_vertex).add(number(i + 1, j + 1, n));
                }
            }
        }
        Queue <Integer> q1 = new LinkedList<Integer>();
        int[] parent1 = new int[n * n];
        int[] distance1 = new int[n * n];
        for (int i = 0; i < n*n; i ++){
            distance1[i] = 1000000000;
        }
        q1.add(monster_index);
        distance1[monster_index] = 0;
        int v1;
        while (! q1.isEmpty()){
            v1 = q1.remove();
            for (int i = 0; i < graph.get(v1).size(); i ++){
                if (distance1[v1] + 1 < distance1[graph.get(v1).get(i)]) {
                    distance1[graph.get(v1).get(i)] = distance1[v1] + 1;
                    parent1[graph.get(v1).get(i)] = v1;
                    q1.add(graph.get(v1).get(i));
                }
            }
        }
        int without_pit_distance = distance1[character_index];
        ArrayList <Integer> way1 = new ArrayList<>();
        int prev = character_index;
        if (without_pit_distance < 1000000000)
            way1.add(prev);
        while (prev != monster_index){
            prev = parent1[prev];
            way1.add(prev);
        }


        Queue <Integer> q2 = new LinkedList<Integer>();
        int[] parent2 = new int[n * n];
        int[] distance2 = new int[n * n];
        for (int i = 0; i < n*n; i ++){
            distance2[i] = 1000000000;
        }
        q2.add(monster_index);
        distance2[monster_index] = 0;
        int v2;
        while (! q2.isEmpty()){
            v2 = q2.remove();
            if (v2 != pit_square_number) {
                for (int i = 0; i < graph.get(v2).size(); i++) {
                    if (distance2[v2] + 1 < distance2[graph.get(v2).get(i)]) {
                        distance2[graph.get(v2).get(i)] = distance2[v2] + 1;
                        parent2[graph.get(v2).get(i)] = v2;
                        q2.add(graph.get(v2).get(i));
                    }
                }
            }
        }
        ArrayList <Integer> way2 = new ArrayList<>();
        prev = character_index;
        int with_pit_distance = distance2[character_index];
        if (with_pit_distance < 1000000000)
            way2.add(prev);
        prev = character_index;
        while (prev != monster_index){
            Log.d("bbb", String.valueOf(12332));
            prev = parent2[prev];
            way2.add(prev);
        }
        if (without_pit_distance < with_pit_distance) {
            for(int i = 0;i<way1.size();i++){
                way.add(way1.get(i));
            }
        } else if(without_pit_distance == with_pit_distance){
            for(int i = 0;i<way2.size();i++){
                way.add(way2.get(i));
            }
        }
    }
    public static boolean check(int a, int b, int n){
        if (a >= 0 & a < n & b >= 0 & b < n){
            return true;
        } else {
            return false;
        }
    }
    public static int number(int i, int j, int n){
        return i * n + j;
    }
}