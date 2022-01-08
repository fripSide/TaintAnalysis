
public class Test1 {
	public static void main(String[] args)  {
		int source = args.length, a = 4, b = 2, c = 3, sink = 5;
		b = source;
		if (source > 2)
			c = a;
		else
			c = b;
		sink = c;
		System.out.println(sink);
	}
}