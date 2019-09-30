import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 
 */

/**
 * @author Yash Sheth
 *
 */
public class TestFileGenerator {


	public static void main(String[] args) throws IOException {
		Random r=new Random();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("TestFile30000.txt")));		
		int basket_count = 1000;
		out.println(basket_count+" "+300);
		
		System.out.println("Baskets # "+basket_count);
		List<Integer> basket_items=new ArrayList<>();
		
		for(int i=0;i<100;i++){
			basket_items.add(new Integer(i));
		}
		
		for(int i=1;i<=basket_count;i++){
			out.print(i+",");
			int val = r.nextInt((100 - 3) + 1) + 3; //3-100  items per basket
			Collections.shuffle(basket_items);
			for(int j=0;j<val;j++){
				int item=basket_items.get(j);
				out.print(item+",");
			}
			out.println("");
		}
		out.close();
		System.out.println("DONE:");
	}
}
