import java.util.HashMap;

public class InterTest {

    public void invoke1(int a, int b) {
        int amount = 0;
        int c = a + b + amount;

        float csquare = invoke2(c);

        HelloWorld hello = new HelloWorld((int)csquare);
        hello.invoke3();

        HelloWorld h = getClass(2 * csquare);
        h.invoke(c);
        h.invoke3();

        int[] array = invoke4(2, h);
        int i = array[0];
        System.out.println(array);
        System.out.println(i);

    }

    private HelloWorld getClass(float f){
        return new HelloWorld((int)f);
    }

    private int[] invoke4(int i, HelloWorld h){
        int[] array = new int[3];
        int v = h.getField();
        array[0] = v;
        array[1] = i;
        array[2] = 0;
        return array;
    }

    public float invoke2(int c) {
        return c * c;
    }

    public static void main(String[] args) {
        InterTest t = new InterTest();
        // source
        t.invoke1(Integer.parseInt(args[0]), 2);
    }
}