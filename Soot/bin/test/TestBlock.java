public class TestBlock {
	public static void main(String[] args) {
		int v;
		try {
			v = args.length;
			String arg = args[1];
			System.out.println(arg);
		} catch (Exception ex) {
			v = -1;
			System.out.println(ex);
		} finally {
			v = 0;
		}
		System.out.println("Final Result:" + v);
	}
}