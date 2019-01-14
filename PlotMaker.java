import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial") //suppressed warning as it is not necessary for this exercise
public class PlotMaker extends JComponent{
	
	//following values are arbitrary, assigned to make the graph conform to specifications and for looking aesthetically pleasing 
	private final int BORDERGAP = 55; //the 'gap' between the edge of the plot panel and the axis
	private final int HATCHEDLINEOFFSET = 5; //how 'long' a hatched line is
	private final int NUMOFHATCHES = 10; //how many hatched lines there are, per axis
	private final int YLABELOFFSET = 5; //used to nicely align the y axis labels
	private final int XLABELOFFSET = 45; //used to nicely align the x axis labels
	private final double DOTPOSITIONCORRECTION = 5; //used to make sure that the scaled x and y positions the centre of the graph dots instead of being the 'edge' by default 
	
	//Values relating to graph data points
	private BondTradeData myBondTrades; //used to obtain raw values
	private int xColumn, yColumn; //used to indicate which category (e.h. yield) to pick values from for each axis. are initialised automatically
	private int height, width; //height and width of the graph
	private double xMax, xMin, xValuesRange, xGraphValuesRange; //used to scale the labels and points appropriately for the x axis
	private double yMax, yMin, yValuesRange, yGraphValuesRange; //used to scale the labels and points appropriately for the y axis
	
	private ArrayList<Double> dataPointInfo; //an array of graph point coordinates and their raw values; used to load data into array list below, initialised at each graph point
	private ArrayList<ArrayList<Double>> pairedRawAndCoordinateData;//list of graph point coordinates and their raw values, initialised at each redraw
	private ArrayList<Ellipse2D.Double> shapes; //array list of all dots on graph, used for mouse clicker, initialised at each redraw
	
	//Constructor
	public PlotMaker(){
	}
	
	//Setter for bond trades; this variables are then used to create the graph
	public void loadData(BondTradeData myBondTrade) {
		this.myBondTrades = myBondTrade;
		repaint();
	}
	
	/* Paint method. If a bond trade is loaded into a plotmaker instance (myBondTrades != null), then only will it draw a scatteplot  
	 * 1. it calls a method that performs calculation for the data points (Max, Min, range of values etc)
	 * 2. It draws the axis, hatched lines and labels
	 * 3. For each point in the scatterplot:
	 * 		3.1 scale the raw values into x and y coordinates
	 * 		3.2 create a shape that will be the dot to the graph
	 * 		3.3 add the shape and its related values into the appropriate arrays, to be used when retrieving details of a specific trade (ie of  a specific dot/shape)
	 */
	public void paintComponent(Graphics g){
		 super.paintComponent(g);
		 Graphics2D g2 = (Graphics2D)g;
		 g2.setColor(Color.BLACK);
		
		
		if (myBondTrades != null) { //only paint when a bond trade is loaded into the graph
			performCalculations();	// calculate variables	 
			drawGraphComponents(g2); //draw axis/boxed area, hatched lines, and labels
			
			for (int index = 0; index < myBondTrades.getRowCounter(); index++){ //loop for each point
				//obtain raw values for x and y and scale them
				double x = calculateGraphxCoordinates(index); 
				double y = calculateGraphyCoordinates(index);	
				
				
				//offset to x and y coordinates of shape to make the plot look prettier
				Ellipse2D.Double e = new Ellipse2D.Double(x-(DOTPOSITIONCORRECTION/2), y-(DOTPOSITIONCORRECTION/2), DOTPOSITIONCORRECTION, DOTPOSITIONCORRECTION); 
				g2.draw(e);
				g2.fill(e);
				
				shapes.add(e);
				addPairedData(e,myBondTrades.getColumnValue(xColumn, index), myBondTrades.getColumnValue(yColumn, index));
				
			}
		}
		
	}

	//This method calculates and initialises values necessary for scaling and for relating raw values to their scatterplot dots
	private void performCalculations() {
		//Needs to be reinitialized any time the graph is redrawn
		pairedRawAndCoordinateData = new ArrayList<ArrayList<Double>>();
		shapes = new ArrayList<>();
	
		height = this.getHeight();
		width = this.getWidth();
		
		xMax = myBondTrades.getMaxValue(xColumn);
		xMin = myBondTrades.getMinValue(xColumn);
	
		yMax = myBondTrades.getMaxValue(yColumn);
		yMin = myBondTrades.getMinValue(yColumn);
		
		//to be used in scaling using the 'rule of three', essentially the range of values for raw data
		xValuesRange = xMax - xMin;
		yValuesRange = yMax - yMin;
		
		//to be used in scaling using the 'rule of three', essentially the range of values for the scatterplot area
		xGraphValuesRange = width - 2 * BORDERGAP;
		yGraphValuesRange = height - 2 * BORDERGAP;	
	}
	
	//This method draws the axis, boxed aeras, labels and hatched lines
	private void drawGraphComponents(Graphics2D g2) {
		//Draw box in graph area
		g2.drawLine(BORDERGAP, height - BORDERGAP, BORDERGAP, BORDERGAP); //y axis
	    g2.drawLine(BORDERGAP, height - BORDERGAP, width - BORDERGAP, height - BORDERGAP); //x axis
	    g2.drawLine(BORDERGAP, BORDERGAP, width-BORDERGAP, BORDERGAP);  //line to complete box area
	    g2.drawLine(width-BORDERGAP, BORDERGAP, width-BORDERGAP, height-BORDERGAP); //line to complete box area
	    
	    //Calculate the offset between each value of the labels (eg an offset for one would have xLabel1 =1, xLabel2 = 2 etc)
	    double yLabelOffset = (yMax-yMin)/(NUMOFHATCHES); 
	    double xLabelOffset = (xMax-xMin)/(NUMOFHATCHES);
	    
	    //Draw hatched lines and text across the two axis
	    for (int i = 0; i <= NUMOFHATCHES; i++) { //Y axis hatches
	        int x1 = BORDERGAP-HATCHEDLINEOFFSET; //find the endpoint from the hatch as it extends from the axis
	        
	        //mathematical expression to determine position along y axis of each hatched line
	        int y = (int) (height - (BORDERGAP + ((double) (height - (BORDERGAP * 2)) * i) / NUMOFHATCHES )); 
	        
	        /*To draw a hatch, the following have been calculated: x0, y0, x1, y1
	         * x0: bordergap as each hatched line would start from the axis
	         * x1: as mentioned above
	         * The y coordinates of a y axis gap won't change (straight line), so: y0 = y1 = y
	         * y has been calculated as mentioned above
	         */
	        g2.drawLine(BORDERGAP, y, x1, y);
	        
	        double yLabel = yMin + yLabelOffset * i; //calculate label value at hatch line position
	        
	        //add values to each hatched line and format with appropriate coordinate offsets
			g2.drawString(String.format("%.1f", yLabel), x1-XLABELOFFSET, (int) (y + YLABELOFFSET)); 
	      }

	    for (int i = 0; i <= NUMOFHATCHES; i++) { //x axis hatches
	    	//mathematical expression to determine position along x axis of each hatched line
	        int x = (int) (BORDERGAP + ((double) (width - (BORDERGAP * 2)) / NUMOFHATCHES)*i); 
	        
	        int y0 = height - BORDERGAP; //account for the fact that in swing y=0 is at the top of the panel instead of the bottom
	        int y1 = y0 + HATCHEDLINEOFFSET;  //find the endpoint from the hatch as it extends from the axis
	        
	        /*To draw a hatch, the following have been calculated: x0, y0, x1, y1
	         *The x coordinates of a x axis gap won't change (straight line), so: x0 = x1 = x
	         * x has been calculated as mentioned above 
	         * y1: as mentioned above
	         * y0: as mentioned above 
	         */
	        g2.drawLine(x, y0, x, y1); 
	        
	        double xLabel =  xMin + xLabelOffset * i;    //calculate plot value at hatch line position
			g2.drawString(String.format("%.1f", xLabel), x - 5, y1+15); //add values close to each hatched line
	      }
	}
	
	/* This method scales the value to an x coordinate in the scatterplot. There are three steps to the process:
	* 1. Obtain the raw value and find its offset from the minimal value
	* 2. Scale using 'rule of three', i.e.:
	* 	 xResult /xGraphValuesRange = xAbsoluteRawValue / xValuesRange => xResult = (xAbsoluteRawValue * xGraphValuesRange) / xValuesRange
	* 3. Correct the position of the x value due to the gaps between the plot's border area and the axis
	*/
	private double calculateGraphxCoordinates(int index){
		double xAbsoluteRawValue = myBondTrades.getColumnValue(xColumn, index) - xMin; //find distance of raw value from the minimum value in its column
		double xResult = (xAbsoluteRawValue * xGraphValuesRange) / xValuesRange; //scale using rule of three
		double xCoordinate = xResult + BORDERGAP; //account for the border areas to find the actual spot on the plot the value belongs to
		return xCoordinate;
	}
	
	/*This method scales the value to an y coordinate in the scatterplot. There are three steps to the process:
	* 1. Obtain the raw value and find its offset from the minimal value
	* 2. Scale using 'rule of three', i.e.:
	* 	 yResult /yGraphValuesRange = yAbsoluteRawValue / yValuesRange => yResult = (yAbsoluteRawValue * yGraphValuesRange) / yValuesRange
	* 3. Correct the position of the y value due to the gaps between the plot's border area and the axis
	 */
	private double calculateGraphyCoordinates(int index){
		double yPoint = myBondTrades.getColumnValue(yColumn, index) - yMin; //find distance of raw value from the minimum value in its column
		double yResult = (yPoint * yGraphValuesRange) / yValuesRange;  //scale using rule of three
		double y = (height - yResult) - BORDERGAP; //account for the border areas to find the actual spot on the plot the value belongs to
		return y;
	}
	
	
	//This method creates an array that lists a shape's (ie point on the scatterplot) x and y coordinates, along with the original x and y values
	private void addPairedData(Ellipse2D.Double e, double rawX, double rawY) {		
		dataPointInfo = new ArrayList<Double>(); //initialise for each point		
		dataPointInfo.add(e.x);
		dataPointInfo.add(e.y);
		dataPointInfo.add(rawX);
		dataPointInfo.add(rawY);
		pairedRawAndCoordinateData.add(dataPointInfo); //add array to collection of array lists
	}
		
	/*This method takes in the coordinates of a clicked shape (determined my mouselistener in AE3)
	 * The method uses the pairedRawAndCoordinateData to locate with in it an array that contains the shape's x and y coordinates
	 * When such an array is found, the methods then obtains the 'raw' values associated with the shape's x and y coordinates and
	 * goes through the bond trade data to find the index in terms of bond trade. 
	 * In the end, the method returns the index of the bond data of the clicked shape, allowing the location of the trade details in other methods
	 * If the shape's index cannot be determined, the method return -1
	 */
	
	public int locateClickedShapeIndex(double xClickedShape, double yClickedShape){
		int index = -1;
		//find x and y raw values by locating matches in ArrayList of Array list (3rd and 4th value)
		for (int i = 0; i < pairedRawAndCoordinateData.size(); i++) { //loop for each data point
			
			//check if the x and y coordinates of the shape match the x and y coordinates of the shape clicked by the mouse
			if (pairedRawAndCoordinateData.get(i).get(0) == xClickedShape && pairedRawAndCoordinateData.get(i).get(1) == yClickedShape) { 
				
				for (int indexBond =0; indexBond < myBondTrades.getRowCounter(); indexBond++) { //if shape coordinates match, then loop again for each point/trade
					
					//check if the stored raw values for the clicked shape matches those of a bond trade in the appropriate columns
					if (pairedRawAndCoordinateData.get(i).get(2) == myBondTrades.getColumnValue(xColumn, indexBond) 
							&& pairedRawAndCoordinateData.get(i).get(3) == myBondTrades.getColumnValue(yColumn, indexBond)) {	
						index = indexBond;	
						break;
					}	
				}
				
			}
		}
		return index; //index of values of the clicked shape
	}
	
	//Getter for array list of plot shapes, used in order to see if user click has occurred in any of the graph dots
	public ArrayList<Ellipse2D.Double> getPlotShapes(){
		return shapes;
	}
	
	//Setter for the x axis/column of the graph. Used by comboboxes
	public void setXColumn(int x) {
		xColumn = x;
	}
	
	//Setter for the y axis of the graph. Used by comboboxes
	public void setYColumn(int y) {
		yColumn = y;
	}
	
}
