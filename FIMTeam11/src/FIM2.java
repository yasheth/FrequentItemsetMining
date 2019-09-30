import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Yash Sheth, Samip Thakkar
 *
 */
public class FIM2 {

	/**
	 * @param args
	 */

	public static HashMap<Integer, ArrayList<Integer>> input_data = new HashMap<>();
	public static HashMap<Integer, Integer> item_quantity = new HashMap<>();
	public static HashMap<Integer, Integer> frequent_items = new HashMap<Integer, Integer>();
	public static List<Integer> frequent_item_set = new ArrayList<>();
	public static ArrayList<List<Integer>> next_frequent_itemset = new ArrayList<>();
	public static int total_basket;
	public static int threshold;
	public static BufferedWriter out;

	public static void main(String[] args) throws IOException {
		long start_time = System.currentTimeMillis();

		String file_path = "TestFile30000.txt";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file_path));

			String line;
			line = reader.readLine();
			String[] details_array = line.split(" ");

			total_basket = Integer.valueOf(details_array[0]);
			threshold = Integer.valueOf(details_array[1]);

			while ((line = reader.readLine()) != null) {
				String[] items_array = line.split(",");

				int basket_number = Integer.valueOf(items_array[0]);

				ArrayList<Integer> item_list = new ArrayList<>();
				for (int i = 1; i < items_array.length; i++) {
					int item = Integer.valueOf(items_array[i]);
					item_list.add(item);
					if (item_quantity.containsKey(item))
						item_quantity.put(item, item_quantity.get(item) + 1);
					else
						item_quantity.put(item, 1);
				}
				input_data.put(basket_number, item_list);
				total_basket = input_data.size();
			}
			reader.close();
		}

		catch (NumberFormatException | IOException e) {

		}

		File f=new File("Output.txt");
		out= new BufferedWriter(new FileWriter(f));

		displayFile(total_basket, threshold);

		initalFrequentItemSet(total_basket, threshold);

		apriori(total_basket, threshold);

		out.close();
		long end_time = System.currentTimeMillis();
		long total_time = end_time - start_time;
		System.out.println("Total Execution time is " + total_time + " ms");
	}

	public static void displayFile(int total_basket, int min_sup) {
		System.out.println("No of Baskets = " + total_basket);
		System.out.println("Items and its occurrence " + item_quantity);
		System.out.println("Minimum Support = " + min_sup);
	}

	public static void initalFrequentItemSet(int total_basket, int min_sup) throws IOException {

		
		for (int i : item_quantity.keySet()) {
			int count_occurnece = item_quantity.get(i);
			if (count_occurnece >= min_sup) {
				out.append("{ " + i + " } - " + count_occurnece);
				out.newLine();
				frequent_item_set.add(i);
			}
		}
		out.newLine();
	}

	public static void apriori(int total_basket, int min_sup) throws IOException {

		int item_set_number = 1;
		do {
			item_set_number++;
			generatePairs(item_set_number, min_sup);
		} while (next_frequent_itemset.size() != 0);

	}

	public static void generatePairs(int item_set_number, int min_sup) throws IOException {
		HashMap<Integer, ArrayList<List<Integer>>> pairmap = new HashMap<>();
		HashMap<Integer, Integer> hashcount = new HashMap<>();

		if (item_set_number == 2) {
			ArrayList<List<Integer>> second_frequent_itemset = new ArrayList<>();
			for (int i = 0; i < frequent_item_set.size(); i++) {
				for (int j = i + 1; j < frequent_item_set.size(); j++) {

					ArrayList<Integer> temp_pair_list = new ArrayList<Integer>();
					ArrayList<List<Integer>> mapvalues = new ArrayList<>();

					int x = frequent_item_set.get(i);
					int y = frequent_item_set.get(j);

					temp_pair_list.add(x);
					temp_pair_list.add(y);

					int hash_value = (x * y) % 53;

					if (pairmap.containsKey(hash_value)) {
						mapvalues = pairmap.get(hash_value);
						mapvalues.add(temp_pair_list);
						pairmap.put(hash_value, mapvalues);
					} else {
						mapvalues.add(temp_pair_list);
						pairmap.put(hash_value, mapvalues);
					}
					int count = 0;

					for (int loop = 1; loop <= total_basket; loop++) {
						if (input_data.get(loop).contains(x) && input_data.get(loop).contains(y)) {
							count++;
						}
					}

					if (hashcount.containsKey(hash_value)) {
						hashcount.put(hash_value, hashcount.get(hash_value) + count);
					} else {
						hashcount.put(hash_value, count);
					}
				}
			}
			for (int hash_value = 0; hash_value < 53; hash_value++) {
				if (pairmap.containsKey(hash_value)) {

					ArrayList<List<Integer>> mapvalues = pairmap.get(hash_value);

					if (hashcount.get(hash_value) >= min_sup) {
						for (int j = 0; j < mapvalues.size(); j++) {
							List<Integer> temp = mapvalues.get(j);
							Collections.sort(temp);

							int check_1 = temp.get(0);
							int check_2 = temp.get(1);
							int count = 0;

							for (int loop = 1; loop <= total_basket; loop++) {
								if (input_data.get(loop).contains(check_1) && input_data.get(loop).contains(check_2)) {
									count++;
								}
							}

							if (count >= min_sup) {
								out.append(temp + " - " + count);
								out.newLine();
								second_frequent_itemset.add(temp);
							} 
						}
					}
				}
			}
			next_frequent_itemset = second_frequent_itemset;

		} else {
			ArrayList<List<Integer>> temp_frequent_itemset = new ArrayList<>();
			int val = next_frequent_itemset.get(0).size();
			List<Integer> list1, list2;
			for (int i = 0; i < next_frequent_itemset.size(); i++) {
				list1 = next_frequent_itemset.get(i);

				for (int j = i + 1; j < next_frequent_itemset.size(); j++) {

					List<Integer> temp = new ArrayList<>();
					int common_value_count = 0;
					boolean first_val=true;
					list2 = next_frequent_itemset.get(j);


					for(int fv=0;fv<list2.size()-1;fv++){
						if(!list1.get(fv).equals(list2.get(fv)))
							first_val=false;	
					}


					if(first_val){
						for (int it : list1) {
							temp.add(it);
							if (list2.contains(it)) {
								common_value_count++;
							}
						}

						if (common_value_count == val - 1) {
							for (int it : list2) {
								if (!temp.contains(it)) {
									temp.add(it);
								}
							}
						}
						
						if (temp.size() == val + 1) {
							Collections.sort(temp);
							if(!temp_frequent_itemset.contains(temp)){
								int count = 0;
								for (int loop = 1; loop <= total_basket; loop++) {
									if (input_data.get(loop).containsAll(temp))
										count++;
								}
								if (count >= min_sup) {
									out.append(temp + " - " + count);
									out.newLine();
									temp_frequent_itemset.add(temp);
								}
							}
						}
					}
				}
			}
			next_frequent_itemset = temp_frequent_itemset;
		}
		System.out.println("FREQUENT ITEM SET: "+item_set_number+" : "+next_frequent_itemset.size());
		out.newLine();
	}
}
