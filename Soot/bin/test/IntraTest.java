public class IntraTest {
    private int field;
    public static void main(String[] args) {
        int j, k = 0, l;
        // source
        int i = Integer.parseInt(args[0]);
        int two = 2;
        if(i % 2 == 0){
            j = i + two;
            l = j;
        }
        else{
            k = two + two;
            l = k;
            if(l > 0){
                two++;
            }
            else{
                two--;
            }
        }
        two++;
        i = two;
        // sink 1
        System.out.println(i);
        System.out.println(l);

        IntraTest t = new IntraTest();
        t.field = l + i;

        l = 2;
        int[] array = new int[2];
        array[0] = i;
        array[1] = l;

        // sink 2
        System.out.println(array);
    }

}