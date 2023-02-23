import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Deque<String> a = new ArrayDeque<>();
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i<n; i++){
            a.push(scanner.nextLine());
        }
        for (int i = 0; i < n; i++){
            System.out.println(a.pop());
        }

    }
}