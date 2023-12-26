// https://blog.csdn.net/metaphysis/article/details/106335079

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Main {
  static final int LIMIT = 200000;
  static final int BLOCK_SIZE = 450;

  static int[] ys = new int[LIMIT];
  static int[] xs = new int[LIMIT];
  static int[][] counts = new int[BLOCK_SIZE][BLOCK_SIZE];
  static int[][] prefixSums = new int[BLOCK_SIZE][BLOCK_SIZE];

  public static void main(String[] args) throws Throwable {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      String line = br.readLine();
      if (line == null) {
        break;
      }

      StringTokenizer st = new StringTokenizer(line);
      int n = Integer.parseInt(st.nextToken());
      int m = Integer.parseInt(st.nextToken());
      int[] p = new int[n];
      for (int i = 0; i < p.length; ++i) {
        st = new StringTokenizer(br.readLine());
        p[i] = Integer.parseInt(st.nextToken());
      }
      int[] removed = new int[m];
      for (int i = 0; i < removed.length; ++i) {
        st = new StringTokenizer(br.readLine());
        removed[i] = Integer.parseInt(st.nextToken());
      }

      System.out.println(solve(p, removed));
    }
  }

  static String solve(int[] p, int[] removed) {
    init();

    long inversionNum = 0;

    for (int i = 0; i < p.length; ++i) {
      add(i, p[i] - 1);
      inversionNum += computeInversionNum(i, p[i] - 1);
    }

    long[] result = new long[removed.length];
    for (int i = 0; i < result.length; ++i) {
      result[i] = inversionNum;
      inversionNum -= computeInversionNum(xs[removed[i] - 1], removed[i] - 1);
      remove(xs[removed[i] - 1], removed[i] - 1);
    }

    return Arrays.stream(result).mapToObj(String::valueOf).collect(Collectors.joining("\n"));
  }

  static void init() {
    Arrays.fill(ys, Integer.MAX_VALUE);
    Arrays.fill(xs, Integer.MAX_VALUE);

    for (int i = 0; i < BLOCK_SIZE; ++i) {
      for (int j = 0; j < BLOCK_SIZE; ++j) {
        counts[i][j] = 0;
        prefixSums[i][j] = 0;
      }
    }
  }

  static int computeInversionNum(int x, int y) {
    return computePointNum(x, LIMIT - 1)
        + computePointNum(LIMIT - 1, y)
        - 2 * computePointNum(x, y);
  }

  static int computePointNum(int x, int y) {
    int bx = x / BLOCK_SIZE;
    int by = y / BLOCK_SIZE;

    int result = 0;
    if (bx != 0) {
      for (int i = 0; i < by; ++i) {
        result += prefixSums[bx - 1][i];
      }
    }
    for (int i = bx * BLOCK_SIZE; i <= x; ++i) {
      if (ys[i] < by * BLOCK_SIZE) {
        ++result;
      }
    }
    for (int i = by * BLOCK_SIZE; i <= y; ++i) {
      if (xs[i] <= x) {
        ++result;
      }
    }

    return result;
  }

  static void add(int x, int y) {
    ys[x] = y;
    xs[y] = x;

    ++counts[x / BLOCK_SIZE][y / BLOCK_SIZE];
    updatePrefixSum(x / BLOCK_SIZE, y / BLOCK_SIZE);
  }

  static void remove(int x, int y) {
    ys[x] = Integer.MAX_VALUE;
    xs[y] = Integer.MAX_VALUE;

    --counts[x / BLOCK_SIZE][y / BLOCK_SIZE];
    updatePrefixSum(x / BLOCK_SIZE, y / BLOCK_SIZE);
  }

  static void updatePrefixSum(int bx, int by) {
    for (int i = bx; i < BLOCK_SIZE; ++i) {
      prefixSums[i][by] = ((i == 0) ? 0 : prefixSums[i - 1][by]) + counts[i][by];
    }
  }
}
