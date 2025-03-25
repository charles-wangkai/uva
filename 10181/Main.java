// https://github.com/charles-wangkai/aoj/blob/main/ALDS1_13_C/Main.java
// https://stackoverflow.com/questions/34570344/check-if-15-puzzle-is-solvable/34570524#34570524
// https://en.wikipedia.org/wiki/Iterative_deepening_A*

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
  static final int SIZE = 4;
  static final int[] R_OFFSETS = {-1, 0, 1, 0};
  static final int[] C_OFFSETS = {0, 1, 0, -1};
  static final char[] MOVE_NAMES = {'U', 'R', 'D', 'L'};

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    int N = sc.nextInt();
    for (int tc = 0; tc < N; ++tc) {
      int[][] puzzle = new int[SIZE][SIZE];
      for (int r = 0; r < SIZE; ++r) {
        for (int c = 0; c < SIZE; ++c) {
          puzzle[r][c] = sc.nextInt();
        }
      }

      System.out.println(solve(puzzle));
    }

    sc.close();
  }

  static String solve(int[][] puzzle) {
    if (!isSolvable(puzzle)) {
      return "This puzzle is not solvable.";
    }

    State state = new State(puzzle);

    for (int rest = state.totalDistance; ; ++rest) {
      String solution = search(state, rest, new ArrayList<>());
      if (solution != null) {
        return solution;
      }
    }
  }

  static boolean isSolvable(int[][] puzzle) {
    List<Integer> values = new ArrayList<>();
    int spaceR = -1;
    for (int r = 0; r < SIZE; ++r) {
      for (int c = 0; c < SIZE; ++c) {
        if (puzzle[r][c] == 0) {
          spaceR = r;
        } else {
          values.add(puzzle[r][c]);
        }
      }
    }

    int inversionCount = 0;
    for (int i = 0; i < values.size(); ++i) {
      for (int j = i + 1; j < values.size(); ++j) {
        if (values.get(i) > values.get(j)) {
          ++inversionCount;
        }
      }
    }

    return (inversionCount + spaceR) % 2 == 1;
  }

  static String search(State state, int rest, List<Integer> path) {
    if (state.totalDistance == 0) {
      return path.stream()
          .map(direction -> MOVE_NAMES[direction])
          .map(String::valueOf)
          .collect(Collectors.joining());
    }
    if (state.totalDistance > rest) {
      return null;
    }

    for (int i = 0; i < R_OFFSETS.length; ++i) {
      int spaceR = state.spaceR;
      int spaceC = state.spaceC;
      int adjR = spaceR + R_OFFSETS[i];
      int adjC = spaceC + C_OFFSETS[i];
      if (adjR >= 0
          && adjR < SIZE
          && adjC >= 0
          && adjC < SIZE
          && (path.isEmpty() || Math.abs(i - path.get(path.size() - 1)) != 2)) {
        state.move(adjR, adjC);
        path.add(i);

        String solution = search(state, rest - 1, path);

        path.remove(path.size() - 1);
        state.move(spaceR, spaceC);

        if (solution != null) {
          return solution;
        }
      }
    }

    return null;
  }

  static class State {
    int[][] puzzle;
    int spaceR;
    int spaceC;
    int totalDistance;

    State(int[][] puzzle) {
      this.puzzle = puzzle;

      for (int r = 0; r < SIZE; ++r) {
        for (int c = 0; c < SIZE; ++c) {
          if (puzzle[r][c] == 0) {
            spaceR = r;
            spaceC = c;
          } else {
            totalDistance += computeDistance(r, c);
          }
        }
      }
    }

    int computeDistance(int r, int c) {
      return Math.abs(r - (puzzle[r][c] - 1) / SIZE) + Math.abs(c - (puzzle[r][c] - 1) % SIZE);
    }

    void move(int adjR, int adjC) {
      totalDistance -= computeDistance(adjR, adjC);

      puzzle[spaceR][spaceC] = puzzle[adjR][adjC];
      puzzle[adjR][adjC] = 0;

      totalDistance += computeDistance(spaceR, spaceC);

      spaceR = adjR;
      spaceC = adjC;
    }
  }
}