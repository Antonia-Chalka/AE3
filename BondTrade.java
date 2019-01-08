import java.util.ArrayList;
import java.util.Collections;

public class BondTrade {
	private ArrayList<String> headers;
	private ArrayList<Double> yield;
	private ArrayList<Double> daysToMaturity;
	private ArrayList<Double> amountCHF;
	private ArrayList<ArrayList<Double>> allData;
	private int rowCounter;;
	
	public BondTrade(){
		headers = new ArrayList<String>();
		yield = new ArrayList<Double>();
		daysToMaturity = new ArrayList<Double>();
		amountCHF = new ArrayList<Double>();
		allData = new ArrayList<ArrayList<Double>>();
		rowCounter = 0;
	}
	
	public void addData(String fileline){
		String[] filelineArray = fileline.split(",");
		
		if (rowCounter == 0){ //add headers to the headers Array list
			for (int i=0; i < filelineArray.length; i++){
				headers.add(filelineArray[i].toString());
			}
			rowCounter++;
			
		} else { //add values to Array lists, including profit
			double yieldValue = Double.parseDouble(filelineArray[0]);
			double daysToMaturityValue = Double.parseDouble(filelineArray[1]);
			double amountCHFValue = Double.parseDouble(filelineArray[2]);
			
			yield.add(yieldValue);
			daysToMaturity.add(daysToMaturityValue);
			amountCHF.add(amountCHFValue);
			
			rowCounter++;
		}
		
		allData.add(yield);
		allData.add(amountCHF);
		allData.add(daysToMaturity);
	}
	
	public int getRowCounter(){
		return rowCounter;
	}
	
	public ArrayList<String> getHeaders() {
		return headers;
	}
	
	public ArrayList<ArrayList<Double>> createData(){
		
		return allData;
	}
	
	
	/* 0 yield
	 * 1 daysToMaturity
	 * 2 amountCHF
	 */
	
	public String getBondDetails(double x, double y, int xColumn, int yColumn){
		
		int index = 0;
		
		
		String details = "Details of selected Trade: \n"
				+ headers.get(0) + ": " + yield.get(index) + " \n"
				+ headers.get(1)  + ": " + daysToMaturity.get(index) + " \n"
				+ headers.get(2)  + ": "+ amountCHF.get(index) + " \n";
		return details;
	}
	
	//If in the future more columns are to be added, the if else statements could be replaced by a switch statement.
	public double getColumnValue(int column, int index) {
		double value =	allData.get(column).get(index);
		return value;
	}
	
	public double getMaxValue(int column){
		double value = Collections.max(allData.get(column));
		return value;
	}
	
	public double getMinValue(int column){
		double value = Collections.min(allData.get(column));
		return value;
	}
	
}
