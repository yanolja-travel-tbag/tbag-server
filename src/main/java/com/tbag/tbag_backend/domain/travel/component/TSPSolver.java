package com.tbag.tbag_backend.domain.travel.component;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class TSPSolver {

    private int[][] distanceMatrix;
    private boolean[] visited;
    private int n;
    private int bestCost = Integer.MAX_VALUE;
    private int[] bestPath;


//    public int[] findBestRoute(int[][] distanceMatrix) {
//        this.distanceMatrix = distanceMatrix;
//        this.n = distanceMatrix.length;
//        this.visited = new boolean[n];
//        this.bestPath = new int[n];
//
//        int[] currentPath = new int[n];
//        Arrays.fill(currentPath, -1);
//
//        visited[0] = true;
//        currentPath[0] = 0;
//        dfs(0, 1, 0, currentPath);
//
//        return bestPath;
//    }
//
//    private void dfs(int currentNode, int count, int cost, int[] currentPath) {
//        if (count == n && distanceMatrix[currentNode][0] > 0) {
//            int totalCost = cost + distanceMatrix[currentNode][0];
//            if (totalCost < bestCost) {
//                bestCost = totalCost;
//                System.arraycopy(currentPath, 0, bestPath, 0, n);
//            }
//            return;
//        }
//
//        for (int i = 0; i < n; i++) {
//            if (!visited[i] && distanceMatrix[currentNode][i] > 0) {
//                visited[i] = true;
//                currentPath[count] = i;
//                dfs(i, count + 1, cost + distanceMatrix[currentNode][i], currentPath);
//                visited[i] = false;
//                currentPath[count] = -1;
//            }
//        }
//    }


    public int[] findBestRoute(int[][] adjacencyMatrix) {
        int n = adjacencyMatrix.length;
        int[][] memo = new int[n][1 << n];
        int[][] parent = new int[n][1 << n];

        // 초기화
        for (int[] row : memo) {
            Arrays.fill(row, Integer.MAX_VALUE / 2); // Integer.MAX_VALUE / 2를 사용하여 오버플로우 방지
        }

        // dp 함수 정의
        for (int i = 0; i < n; i++) {
            memo[i][1 << i] = 0; // 시작 노드의 초기 비용은 0
        }

        for (int mask = 0; mask < (1 << n); mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) { // i가 mask에 포함되어 있으면
                    for (int j = 0; j < n; j++) {
                        if ((mask & (1 << j)) == 0) { // j가 mask에 포함되어 있지 않으면
                            int nextMask = mask | (1 << j);
                            int newCost = memo[i][mask] + adjacencyMatrix[i][j];
                            if (newCost < memo[j][nextMask]) {
                                memo[j][nextMask] = newCost;
                                parent[j][nextMask] = i;
                            }
                        }
                    }
                }
            }
        }

        // 최적 경로 비용 찾기
        int minCost = Integer.MAX_VALUE;
        int endNode = -1;
        int finalMask = (1 << n) - 1;

        for (int i = 0; i < n; i++) {
            if (memo[i][finalMask] < minCost) {
                minCost = memo[i][finalMask];
                endNode = i;
            }
        }

        // 경로 재구성
        int[] path = new int[n];
        int currentNode = endNode;
        int currentMask = finalMask;

        for (int i = n - 1; i >= 0; i--) {
            path[i] = currentNode;
            currentNode = parent[currentNode][currentMask];
            currentMask ^= (1 << path[i]);
        }

        return path;
    }

}
