import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    while (true) {
      int n = sc.nextInt();
      if (n == 0) {
        break;
      }

      System.out.println(solve(n));
    }

    sc.close();
  }

  static String solve(int n) {
    return isCarmichael(n)
        ? String.format("The number %d is a Carmichael number.", n)
        : String.format("%d is normal.", n);
  }

  static boolean isCarmichael(int n) {
    return !isPrime(n) && IntStream.range(2, n).allMatch(x -> powMod(x, n, n) == x);
  }

  static boolean isPrime(int x) {
    for (int i = 2; i * i <= x; ++i) {
      if (x % i == 0) {
        return false;
      }
    }

    return true;
  }

  static int powMod(int base, int exponent, int m) {
    int result = 1;
    while (exponent != 0) {
      if ((exponent & 1) == 1) {
        result = multiplyMod(result, base, m);
      }

      base = multiplyMod(base, base, m);
      exponent >>= 1;
    }

    return result;
  }

  static int multiplyMod(int x, int y, int m) {
    return (int) Math.floorMod((long) x * y, m);
  }
}
