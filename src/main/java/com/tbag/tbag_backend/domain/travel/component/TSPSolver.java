package com.tbag.tbag_backend.domain.travel.component;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class TSPSolver {

    public int[] findBestRoute(int[][] adjacencyMatrix) {
        int n = adjacencyMatrix.length;
        int[][] memo = new int[n][1 << n];
        int[][] parent = new int[n][1 << n];

        for (int[] row : memo) {
            Arrays.fill(row, Integer.MAX_VALUE / 2);
        }

        for (int i = 0; i < n; i++) {
            memo[i][1 << i] = 0;
        }

        for (int mask = 0; mask < (1 << n); mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    for (int j = 0; j < n; j++) {
                        if ((mask & (1 << j)) == 0) {
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

        int minCost = Integer.MAX_VALUE;
        int endNode = -1;
        int finalMask = (1 << n) - 1;

        for (int i = 0; i < n; i++) {
            if (memo[i][finalMask] < minCost) {
                minCost = memo[i][finalMask];
                endNode = i;
            }
        }

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
