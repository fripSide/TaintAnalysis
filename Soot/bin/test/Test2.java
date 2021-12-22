
public class Test2 {
	public static void main(String[] args)  {
		int a = 1, b = 2, c = 3, sink = 4, source = 5;
		if (a > 0)
			b = source;
		else
			c = b;
		sink = c;
	}
}