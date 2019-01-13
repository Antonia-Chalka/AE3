import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial") //suppressed warning as it is not necessary for this exercise
public class PlotMaker extends JComponent{
	
	//TODO fix this mess/assign set up as final
	private BondTradeData myBondTrades;
	private int xColumn, yColumn;
	private int axisGap, height, width, hatchedLineOffset, numofhatches, yOffsetforletters,xOffsetforletters;
	private double xMax, yMax, xMin, yMin, pointDimensions;
	private double xValuesRange, yValuesRange, xGraphValuesRange, yGraphValuesRange;
	
	
	private ArrayList<Ellipse2D.Double> shapes;
	private ArrayList<Double> dataPointInfo; //TODO maybe remove?
	private ArrayList<ArrayList<Double>> AlldataPointInfo;
	
	//TODO see if more needs to be added + comment
	public PlotMaker(){
		xColumn = 0;
		yColumn = 0;
		shapes = new ArrayList<>();
	}
	
	//TODO comment
	public void loadData(BondTradeData myBondTrade) {
		this.myBondTrades = myBondTrade;
		repaint();
	}
	
	//TODO jesuschrist
	private void calculateDimensions() {
		AlldataPointInfo = new ArrayList<ArrayList<Double>>();
	
		height = this.getHeight();
		width = this.getWidth();
		axisGap = 55;
		hatchedLineOffset = 5;
		numofhatches =10;
		yOffsetforletters = 5;
		xOffsetforletters = 45;
		pointDimensions = 5;
		xMax = myBondTrades.getMaxValue(xColumn);
		xMin = myBondTrades.getMinValue(xColumn);
	
		yMax = myBondTrades.getMaxValue(yColumn);
		yMin = myBondTrades.getMinValue(yColumn);
		xValuesRange = xMax - xMin;
		yValuesRange = yMax - yMin;
		
		xGraphValuesRange = width - 2 * axisGap;
		yGraphValuesRange = height - 2 * axisGap;
		
	}
	
	//TODO Comment the shit out of this
	private void drawAxis(Graphics2D g2) {
		//Draw box in graph area
		g2.drawLine(axisGap, height - axisGap, axisGap, axisGap); //y axis
	    g2.drawLine(axisGap, height - axisGap, width - axisGap, height - axisGap); //x axis
	    g2.drawLine(axisGap, axisGap, width-axisGap, axisGap); 
	    g2.drawLine(width-axisGap, axisGap, width-axisGap, height-axisGap);
	    
	    double yLabelOffset = (yMax-yMin)/(numofhatches); 
	    double xLabelOffset = (xMax-xMin)/(numofhatches);
	    
	    //Draw hatched lines and text across the two axis
	    //Y axis: (y of hatched line does not change (y0))
	    for (int i = 0; i <= numofhatches; i++) {
	        int x1 = axisGap-hatchedLineOffset;
	        int y0 = (int) (height - (axisGap + ((double) (height - (axisGap * 2)) * i) / numofhatches ));
	        g2.drawLine(axisGap, y0, x1, y0); //draw hatched line

	        double yLabel = yMin + yLabelOffset * i; //calculate plot value at hatch line position
			g2.drawString(String.format("%.1f", yLabel), x1-xOffsetforletters, (int) (y0 + yOffsetforletters)); //add values close to each hatched line
	      }

	    //X axis (x of hatched line does not change (x0))
	    for (int i = 0; i <= numofhatches; i++) {
	        int x0 = (int) (axisGap + ((double) (width - (axisGap * 2)) / numofhatches)*i);      
	        int y0 = height - axisGap;
	        int y1 = y0 + hatchedLineOffset;
	        g2.drawLine(x0, y0, x0, y1); //draw hatched line
	         
	        double xLabel =  xMin + xLabelOffset * i;    //calculate plot value at hatch line position
			g2.drawString(String.format("%.1f", xLabel), x0 - 5, y1+15); //add values close to each hatched line
	      }
		
	}
	
	/*TODO comment the shot out of this too
	* Scale using 'rule of three': the value of one unknown quantity in a proportion is found by multiplying the denominator of each ratio by the numerator of the other
	* Alternatively:  Point/ValuesRange = Result/GraphValuesRange => Result = (Point*GraphValuesRange)/ValuesRange (RENAME)
	*/
	
	private double calculateGraphxCoordinates(int index){
		double xPoint = myBondTrades.getColumnValue(xColumn, index) - xMin;
		double xResult = (xPoint * xGraphValuesRange) / xValuesRange;
		double x = xResult + axisGap;
		return x;
	}
	
	/*TODO comment the shot out of this too
	 * 
	 * Scaled using 'rule of three' described above.
	 * mention difference is because the 0 point of the y scale is at the top of the graph (need to invert it)
	 */
	private double calculateGraphyCoordinates(int index){
		double yPoint = myBondTrades.getColumnValue(yColumn, index) - yMin;
		double yResult = (yPoint * yGraphValuesRange) / yValuesRange;
		double y = (height - yResult) - axisGap;
		return y;
	}
	
	/*TODO comment and refractor
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * Paint component
	 * 
	 * 
	 */
	public void paintComponent(Graphics g){
		 super.paintComponent(g);
		 
		if (myBondTrades != null) { //only paint when a bondtrade is loaded into the graph
			calculateDimensions();	
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.BLACK); 
			drawAxis(g2); //draw axis/boxed area, hatched lines, and labels
			
			
			//Loop for each point
			for (int index = 0; index < myBondTrades.getRowCounter(); index++){
				double x = calculateGraphxCoordinates(index);
				double y = calculateGraphyCoordinates(index);	
				
				Ellipse2D.Double e = new Ellipse2D.Double(x-(pointDimensions/2), y-(pointDimensions/2), pointDimensions, pointDimensions); //offset to x and y coordinates of shape to make the plot look prettier
				g2.draw(e);
				g2.fill(e);
				
				shapes.add(e);
				addData(e,myBondTrades.getColumnValue(xColumn, index), myBondTrades.getColumnValue(yColumn, index));	
			}
		}
		
	}
	
	//TODO comment
	private void addData(Ellipse2D.Double e, double rawX, double rawY) {		
		dataPointInfo = new ArrayList<Double>();
		
		dataPointInfo.add(e.x);
		dataPointInfo.add(e.y);
		dataPointInfo.add(rawX);
		dataPointInfo.add(rawY);
		AlldataPointInfo.add(dataPointInfo);
	}
	
	
	//TODO comment and maybe refractor
	public int locateClickedShape(double xValueShape, double yValueShape){
		int index = -1;
		//find x and y raw values by locating matches in ArrayList of Array list (3rd and 4th value)
		for (int i = 0; i < AlldataPointInfo.size(); i++) {
			if (AlldataPointInfo.get(i).get(0) == xValueShape && AlldataPointInfo.get(i).get(1) == yValueShape) {
				//found x and y, search for index in Bond trade
				for (int indexBond =0; indexBond < myBondTrades.getRowCounter(); indexBond++) {
					if (AlldataPointInfo.get(i).get(2) == myBondTrades.getColumnValue(xColumn, indexBond) 
							&& AlldataPointInfo.get(i).get(3) == myBondTrades.getColumnValue(yColumn, indexBond)) {		
						index = indexBond;	
					}	
				}
				break;
			}
		}
		return index;
	}
	
	//TODO comment
	//Getter for array list of plot shapes, used 
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
