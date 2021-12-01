public class HelloWorld {

	private int field;
	//private int field1;

	public HelloWorld(){

	}

	public HelloWorld(int i) {
		field = i;
	}

	/*public HelloWorld(int i,int j) {
		field = i;
		field1 = j;
	}*/

	public void invoke(int i) {
		if (i == 1)
			hello();
		else
			nohello();

	}

	public void invoke3() {
		System.out.println(this.field);
		//System.out.println(this.field1);
	}

	public void hello() {
		System.out.println("Hello world!");
	}

	public void nohello() {
		System.out.println();
	}

	public int getField() {
		return field;
	}
	public void setField(int field) {
		this.field = field;
	}


}