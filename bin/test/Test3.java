
public class Test3 {
	public static void main(String[] args)  {
		int a = 1, b = 2, c = 3, sink = 4, source = 5, N = 5;
		int i = 0;
		while (i < N) {
			if (i % 2 == 0)
				b = source;
			else
				c = b;
			i++;
		}
		sink = c;
	}
}