// https://www.hankcs.com/program/algorithm/uva-12161-ironman-race-in-treeland.html
// https://github.com/charles-wangkai/poj/blob/main/1741/Main.java

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    int T = sc.nextInt();
    for (int tc = 0; tc < T; ++tc) {
      int n = sc.nextInt();
      int m = sc.nextInt();
      int[] a = new int[n - 1];
      int[] b = new int[n - 1];
      int[] D = new int[n - 1];
      int[] L = new int[n - 1];
      for (int i = 0; i < n - 1; ++i) {
        a[i] = sc.nextInt();
        b[i] = sc.nextInt();
        D[i] = sc.nextInt();
        L[i] = sc.nextInt();
      }

      System.out.println(String.format("Case %d: %d", tc + 1, solve(a, b, D, L, m)));
    }

    sc.close();
  }

  static int solve(int[] a, int[] b, int[] D, int[] L, int m) {
    int n = a.length + 1;

    @SuppressWarnings("unchecked")
    List<Integer>[] edgeLists = new List[n];
    for (int i = 0; i < edgeLists.length; ++i) {
      edgeLists[i] = new ArrayList<>();
    }
    for (int i = 0; i < a.length; ++i) {
      edgeLists[a[i] - 1].add(i);
      edgeLists[b[i] - 1].add(i);
    }

    return computeMaxLength(a, b, D, L, m, edgeLists, new boolean[n], new int[n], 0);
  }

  static int computeMaxLength(
      int[] a,
      int[] b,
      int[] D,
      int[] L,
      int m,
      List<Integer>[] edgeLists,
      boolean[] centroids,
      int[] subtreeSizes,
      int node) {
    buildSubtreeSizes(subtreeSizes, a, b, edgeLists, centroids, -1, node);

    int s =
        findCentroid(a, b, edgeLists, centroids, subtreeSizes, subtreeSizes[node], -1, node).node;
    centroids[s] = true;

    int result = 0;
    for (int edge : edgeLists[s]) {
      int other = (s == a[edge] - 1) ? (b[edge] - 1) : (a[edge] - 1);
      if (!centroids[other]) {
        result =
            Math.max(
                result, computeMaxLength(a, b, D, L, m, edgeLists, centroids, subtreeSizes, other));
      }
    }

    List<Path> processed = new ArrayList<>();
    for (int edge : edgeLists[s]) {
      int other = (s == a[edge] - 1) ? (b[edge] - 1) : (a[edge] - 1);
      if (!centroids[other]) {
        List<Path> paths = new ArrayList<>();
        searchPaths(paths, a, b, D, L, m, edgeLists, centroids, D[edge], L[edge], s, other);
        paths = clean(paths);

        int pathIndex = paths.size() - 1;
        for (Path p : processed) {
          while (pathIndex != -1 && paths.get(pathIndex).damage + p.damage > m) {
            --pathIndex;
          }

          if (pathIndex != -1) {
            result = Math.max(result, p.length + paths.get(pathIndex).length);
          }
        }

        processed.addAll(paths);
        processed = clean(processed);
      }
    }

    centroids[s] = false;

    return result;
  }

  static List<Path> clean(List<Path> paths) {
    Collections.sort(
        paths,
        Comparator.<Path, Integer>comparing(path -> path.damage)
            .thenComparing(Comparator.<Path, Integer>comparing(path -> path.length).reversed()));

    List<Path> result = new ArrayList<>();
    for (Path path : paths) {
      if (result.isEmpty()
          || (path.damage != result.get(result.size() - 1).damage
              && path.length > result.get(result.size() - 1).length)) {
        result.add(path);
      }
    }

    return result;
  }

  static void searchPaths(
      List<Path> paths,
      int[] a,
      int[] b,
      int[] D,
      int[] L,
      int m,
      List<Integer>[] edgeLists,
      boolean[] centroids,
      int damage,
      int length,
      int parent,
      int node) {
    if (damage <= m) {
      paths.add(new Path(damage, length));

      for (int edge : edgeLists[node]) {
        int other = (node == a[edge] - 1) ? (b[edge] - 1) : (a[edge] - 1);
        if (other != parent && !centroids[other]) {
          searchPaths(
              paths,
              a,
              b,
              D,
              L,
              m,
              edgeLists,
              centroids,
              damage + D[edge],
              length + L[edge],
              node,
              other);
        }
      }
    }
  }

  static Outcome findCentroid(
      int[] a,
      int[] b,
      List<Integer>[] edgeLists,
      boolean[] centroids,
      int[] subtreeSizes,
      int totalSize,
      int parent,
      int node) {
    Outcome result = new Outcome(Integer.MAX_VALUE, -1);
    int restSize = totalSize - 1;
    int maxSubtreeSize = 0;
    for (int edge : edgeLists[node]) {
      int other = (node == a[edge] - 1) ? (b[edge] - 1) : (a[edge] - 1);
      if (other != parent && !centroids[other]) {
        result =
            min(
                result,
                findCentroid(a, b, edgeLists, centroids, subtreeSizes, totalSize, node, other));

        maxSubtreeSize = Math.max(maxSubtreeSize, subtreeSizes[other]);
        restSize -= subtreeSizes[other];
      }
    }

    maxSubtreeSize = Math.max(maxSubtreeSize, restSize);
    result = min(result, new Outcome(maxSubtreeSize, node));

    return result;
  }

  static Outcome min(Outcome outcome1, Outcome outcome2) {
    return (outcome1.maxSubtreeSize < outcome2.maxSubtreeSize) ? outcome1 : outcome2;
  }

  static void buildSubtreeSizes(
      int[] subtreeSizes,
      int[] a,
      int[] b,
      List<Integer>[] edgeLists,
      boolean[] centroids,
      int parent,
      int node) {
    subtreeSizes[node] = 1;

    for (int edge : edgeLists[node]) {
      int other = (node == a[edge] - 1) ? (b[edge] - 1) : (a[edge] - 1);
      if (other != parent && !centroids[other]) {
        buildSubtreeSizes(subtreeSizes, a, b, edgeLists, centroids, node, other);
        subtreeSizes[node] += subtreeSizes[other];
      }
    }
  }
}

class Outcome {
  int maxSubtreeSize;
  int node;

  Outcome(int maxSubtreeSize, int node) {
    this.maxSubtreeSize = maxSubtreeSize;
    this.node = node;
  }
}

class Path {
  int damage;
  int length;

  Path(int damage, int length) {
    this.damage = damage;
    this.length = length;
  }
}