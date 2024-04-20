import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

public class Main {
  public static void main(String[] args) throws Throwable {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      StringTokenizer st = new StringTokenizer(br.readLine());
      int N = Integer.parseInt(st.nextToken());
      if (N == 0) {
        break;
      }

      double[] xs = new double[N];
      double[] ys = new double[N];
      for (int i = 0; i < N; ++i) {
        st = new StringTokenizer(br.readLine());
        xs[i] = Double.parseDouble(st.nextToken());
        ys[i] = Double.parseDouble(st.nextToken());
      }

      System.out.println(solve(xs, ys));
    }
  }

  static String solve(double[] xs, double[] ys) {
    double minDistance =
        computeMinDistance(
            IntStream.range(0, xs.length)
                .mapToObj(i -> new Point(xs[i], ys[i]))
                .sorted(Comparator.comparing(point -> point.x))
                .toArray(Point[]::new),
            0,
            xs.length - 1);

    return (minDistance < 10000) ? String.format("%.4f", minDistance) : "INFINITY";
  }

  static double computeMinDistance(Point[] points, int beginIndex, int endIndex) {
    if (beginIndex == endIndex) {
      return Double.MAX_VALUE;
    }

    int middleIndex = (beginIndex + endIndex) / 2;
    double result =
        Math.min(
            computeMinDistance(points, beginIndex, middleIndex),
            computeMinDistance(points, middleIndex + 1, endIndex));

    double result_ = result;
    Point[] candidates =
        IntStream.rangeClosed(beginIndex, endIndex)
            .mapToObj(i -> points[i])
            .filter(point -> Math.abs(point.x - points[middleIndex].x) < result_)
            .sorted(Comparator.comparing(point -> point.y))
            .toArray(Point[]::new);
    for (int i = 0; i < candidates.length; ++i) {
      for (int j = i + 1;
          j < candidates.length && candidates[j].y - candidates[i].y < result;
          ++j) {
        result =
            Math.min(
                result,
                Math.sqrt(
                    (candidates[j].x - candidates[i].x) * (candidates[j].x - candidates[i].x)
                        + (candidates[j].y - candidates[i].y)
                            * (candidates[j].y - candidates[i].y)));
      }
    }

    return result;
  }
}

class Point {
  double x;
  double y;

  Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
}