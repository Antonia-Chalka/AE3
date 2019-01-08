import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class PlotMaker extends JComponent{
	
	private BondTrade myBondTrade;
	private int xColumn, yColumn;
	private int axisGap, height, width, hatchedLineOffset, numofhatches, yOffsetforletters,xOffsetforletters, pointDimensions;
	private double xMax, yMax, xMin, yMin;
	private double xValuesRange , yValuesRange, xGraphValuesRange, yGraphValuesRange;
	private ArrayList<Ellipse2D.Double> shapes;
	
	public PlotMaker(){
		xColumn = 0;
		yColumn = 0;
		shapes = new ArrayList<>();
	}
	
	public void loadData(BondTrade myBondTrade) {
		this.myBondTrade = myBondTrade;
		repaint();
	}

	public ArrayList<Ellipse2D.Double> returnShapes(){
		return shapes;
	}
	
	public void setXColumn(int x) {
		xColumn = x;
		
	}
	
	public void setYColumn(int y) {
		yColumn = y;
	}
	
	public int getXColumn() {
		return xColumn;
	}
	public int getYColumn() {
		return yColumn;
	}
	
	private void calculateDimensions() {
		height = this.getHeight();
		width = this.getWidth();
		axisGap = 55;
		hatchedLineOffset = 5;
		numofhatches =10;
		yOffsetforletters = 5;
		xOffsetforletters = 45;
		pointDimensions = 5;
		xMax = myBondTrade.getMaxValue(xColumn);
		xMin = myBondTrade.getMinValue(xColumn);
	
		yMax = myBondTrade.getMaxValue(yColumn);
		yMin = myBondTrade.getMinValue(yColumn);
		xValuesRange = xMax - xMin;
		yValuesRange = yMax - yMin;
		
		xGraphValuesRange = width - 2 * axisGap;
		yGraphValuesRange = height - 2 * axisGap;
		
	}
	
	
	private void drawAxis(Graphics2D g2) {
		//Draw box in graph area
		g2.drawLine(axisGap, height - axisGap, axisGap, axisGap); //y axis
	    g2.drawLine(axisGap, height - axisGap, width - axisGap, height - axisGap); //x axis
	    g2.drawLine(axisGap, axisGap, width-axisGap, axisGap); 
	    g2.drawLine(width-axisGap, axisGap, width-axisGap, height-axisGap);
	    
	    //Draw hatched lines and text across the two axis
	    //Y axis: (y of hatched line does not change (y0))
	    for (int i = 0; i <= numofhatches; i++) {
	    	
	    	
	        int x1 = axisGap-hatchedLineOffset;
	        int y0 = (int) (height - (axisGap + ((double) (height - (axisGap * 2)) * i) / numofhatches ));
	        
	        g2.drawLine(axisGap, y0, x1, y0);

	        double xLabelOffset = (xMax-xMin)/(numofhatches); //move to class
	        double xLabel = xMin + xLabelOffset * i;
	
			g2.drawString(String.format("%.1f", xLabel), x1-xOffsetforletters, (int) (y0 + yOffsetforletters));
	      }

	      //X axis (x of hatched line does not change (x0))
	    for (int i = 0; i <= numofhatches; i++) {
	    	
	        int x0 = (int) (axisGap + ((double) (width - (axisGap * 2)) / numofhatches)*i);

	        
	        int y0 = height - axisGap;
	        int y1 = y0 + hatchedLineOffset;
	        g2.drawLine(x0, y0,  x0, y1);
	         
	        double yLabelOffset = (yMax-yMin)/(numofhatches); //move to class
	        double yLabel =  yMin + yLabelOffset * i;
	         
			g2.drawString(String.format("%.1f", yLabel), x0 - 5, y1+15);
	      }
		
	}
	
	private double calculateGraphxCoordinates(int index){
		double xPoint = myBondTrade.getColumnValue(xColumn, index) - yMin;
		double xResult = (xPoint * xGraphValuesRange) / xValuesRange;
		double x = xResult + axisGap;
		return x;
	}
	private double calculateGraphyCoordinates(int index){
		double yPoint = myBondTrade.getColumnValue(yColumn, index) - yMin;
		double yResult = (yPoint*yGraphValuesRange) / yValuesRange;
		double y = (height - yResult) - axisGap;
		return y;
	}
	
	public void paintComponent(Graphics g){
		 super.paintComponent(g);
		 
		if (myBondTrade != null) {
			calculateDimensions();
			
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.BLACK);
			drawAxis(g2);
			
			
			//Loop for each point
			// for y and x do  Point/ValuesRange = Result/GraphValuesRange => Result = (Point*GraphValuesRange)/ValuesRange
			for (int index = 0; index < myBondTrade.getRowCounter()-1; index++){
				double x = calculateGraphxCoordinates(index);
				double y = calculateGraphyCoordinates(index);		
				
				Ellipse2D.Double e = new Ellipse2D.Double(x-(pointDimensions/2), y-(pointDimensions/2), pointDimensions, pointDimensions); //offset to x and y coordinates of shape to make the plot look prettier
				shapes.add(e);
	
				g2.draw(e);
				g2.fill(e);	
			}
		}
		
	}
	
	private double calculateRawxValue(double xCoordinate){
		//DO THIS LATER
		//double xPoint = myBondTrade.getColumnValue(xColumn, index) - yMin;
		//double xResult = (xPoint * xGraphValuesRange) / xValuesRange;
		//double x = xResult + axisGap;
		double xResult = height - (xCoordinate + axisGap);
		double xPoint = (xResult * xValuesRange) / xGraphValuesRange;
		double x = xPoint - xMin;
		
		return x;
		
	}
	private double calculateRawyValue(double yCoordinate){
		//DO THIS LATER
		//double yPoint = myBondTrade.getColumnValue(yColumn, index) - yMin;
		//double yResult = (yPoint*yGraphValuesRange) / yValuesRange;
		//double y = (height - yResult) - axisGap;
		double yResult = height - (yCoordinate + axisGap);
		double yPoint = (yResult * yValuesRange) / yGraphValuesRange;
		double y = (yPoint + yMin);
		
		return y;
		
	}
	
	
	public int locateClickedShape(double xValueShape, double yValueShape){
		//correct to actual x and y values that made shape
		double xValuePoint = calculateRawxValue(xValueShape + (pointDimensions/2));
		double yValuePoint = calculateRawyValue(yValueShape + (pointDimensions/2));
		
		int index = 0;
		
		//Search for x Value in BondTrade Values
		for (int i = 0; i < myBondTrade.getRowCounter()-1; i++){
			Double xValue = myBondTrade.getColumnValue(xColumn, i);
			if (xValue==xValuePoint) {
				Double yValue = myBondTrade.getColumnValue(yColumn, i);
				if (yValue==yValuePoint){ //make sure y value also matches
					i = index;
					System.out.println("Found Index" );
				}
			}
		}
		System.out.println("Index: " + index);
		return index;
		
			
		}
		
		
	
}
