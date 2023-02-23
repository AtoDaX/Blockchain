import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String in[] = scanner.nextLine().split("");
        Deque<String > chek = new ArrayDeque<>();
        String open = "([{";
        String closing = ")]}";
        for (String c: in){
            if (open.contains(c)){
                chek.push(c);
            }
        }
    }
}