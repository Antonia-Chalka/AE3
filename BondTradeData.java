import java.util.ArrayList;
import java.util.Collections;

public class BondTradeData {
	private ArrayList<String> headers;
	private ArrayList<Double> column1;
	private ArrayList<Double> column2;
	private ArrayList<Double> column3;
	private ArrayList<ArrayList<Double>> allBondTradeData;
	private int rowCounter;
	
	//Initialize class variables
	public BondTradeData(){
		headers = new ArrayList<String>();
		column1 = new ArrayList<Double>();
		column2 = new ArrayList<Double>();
		column3 = new ArrayList<Double>();
		allBondTradeData = new ArrayList<ArrayList<Double>>();
		rowCounter = -1;
	}
	
	/* Splits file line across commas to get an array of strings for each line.
	 * If processing the first line of the file (kept in track by rowcounter), add headers to the headers Array list,
	 * otherwise, parse values from strings into doubles and insert them in the appropriate arraylist.
	 * Keep track of how many rows in total exist via rowcounter, which is increased by 1 for every file line processed.
	 * Finally, add all the columns into an arraylist, which is helpful for a couple methods further down the line.
	 */
	public void addData(String fileline){
		String[] filelineArray = fileline.split(",");
		
		if (rowCounter == -1){ //add headers to the headers Array list and sets row counter to 0
			for (int i=0; i < filelineArray.length; i++){
				headers.add(filelineArray[i].toString());
			}
			rowCounter++;
			
		} else { //parse values from strings into doubles and insert them in the appropriate arraylist
			column1.add(Double.parseDouble(filelineArray[0]));
			column2.add(Double.parseDouble(filelineArray[1]));
			column3.add(Double.parseDouble(filelineArray[2]));		
			rowCounter++;
		}	
		allBondTradeData.add(column1);
		allBondTradeData.add(column2);
		allBondTradeData.add(column3);
	}
	
	//Simple getter for the total number of trade values 
	public int getRowCounter(){
		return rowCounter;
	}
	
	//Getter for headers, which are returned in an array string; used to set up combo box items
	public ArrayList<String> getHeaders() {
		return headers;
	}
	
	/* This method can return a specific value of a specific bond trade. 
	 * The type of value (e.g. yield) is specified by column, and the bond trade is specified by the index
	 * An ArrayList<ArrayList<double>> is used in here and in the next two methods so as to not have to unnecessarily complicate the code with if/else or switch statements 
	 */
	public double getColumnValue(int column, int index) {
		return allBondTradeData.get(column).get(index);
	}
	
	//Returns biggest value of a specified type of value (e.g. yield) across all bond trades.
	public double getMaxValue(int column){
		double value = Collections.max(allBondTradeData.get(column));
		return value;
	}
	
	//Returns smallest value of a specified type of value (e.g. yield) across all bond trades.
	public double getMinValue(int column){
		double value = Collections.min(allBondTradeData.get(column));
		return value;
	}
	
	// This method returns a 4-line string which contains the details of a bond value at a specified position, in a 'header: value,'  format
	public String getBondDetails(int index){	
		String details = "Details of selected Trade: \n"
				+ headers.get(0) + ": " + column1.get(index) + "," + System.lineSeparator()
				+ headers.get(1)  + ": " + column2.get(index) + "," + System.lineSeparator()
				+ headers.get(2)  + ": "+ column3.get(index) + ".";
		return details;
	}	
}
