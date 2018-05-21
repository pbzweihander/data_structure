import java.io.*;
import java.util.*;

public class SortingTest {
    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            boolean isRandom = false; // 입력받은 배열이 난수인가 아닌가?
            int[] value; // 입력 받을 숫자들의 배열
            String nums = br.readLine(); // 첫 줄을 입력 받음
            if (nums.charAt(0) == 'r') {
                // 난수일 경우
                isRandom = true; // 난수임을 표시

                String[] nums_arg = nums.split(" ");

                int numsize = Integer.parseInt(nums_arg[1]); // 총 갯수
                int rminimum = Integer.parseInt(nums_arg[2]); // 최소값
                int rmaximum = Integer.parseInt(nums_arg[3]); // 최대값

                Random rand = new Random(); // 난수 인스턴스를 생성한다.

                value = new int[numsize]; // 배열을 생성한다.
                for (int i = 0; i < value.length; i++) // 각각의 배열에 난수를 생성하여 대입
                    value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
            } else {
                // 난수가 아닐 경우
                int numsize = Integer.parseInt(nums);

                value = new int[numsize]; // 배열을 생성한다.
                for (int i = 0; i < value.length; i++) // 한줄씩 입력받아 배열원소로 대입
                    value[i] = Integer.parseInt(br.readLine());
            }

            // 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
            while (true) {

                String line = br.readLine();
                char command = line.charAt(0);

                if (command == 'X')
                    return;

                long t, dt;
                t = System.currentTimeMillis();
                int[] newvalue = doSort(value, command);
                dt = System.currentTimeMillis() - t;

                if (isRandom) {
                    // 난수일 경우 수행시간을 출력한다.
                    System.out.println(dt + " ms");
                } else {
                    // 난수가 아닐 경우 정렬된 결과값을 출력한다.
                    for (int i = 0; i < newvalue.length; i++) {
                        System.out.println(newvalue[i]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
        }
    }

    private static int[] doSort(int[] value, char command) throws IOException {
        ISort sorter;
        switch (command) {
        case 'B': // Bubble Sort
            sorter = new BubbleSort(value);
            break;
        case 'I': // Insertion Sort
            sorter = new InsertionSort(value);
            break;
        case 'H': // Heap Sort
            sorter = new HeapSort(value);
            break;
        case 'M': // Merge Sort
            sorter = new MergeSort(value);
            break;
        case 'Q': // Quick Sort
            sorter = new QuickSort(value);
            break;
        case 'R': // Radix Sort
            sorter = new RadixSort(value);
            break;
        default:
            throw new IOException("잘못된 정렬 방법을 입력했습니다.");
        }
        return sorter.sort();
    }
}
