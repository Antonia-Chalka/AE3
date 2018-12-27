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
	
	private void calculateDimensions() {
		height = this.getHeight();
		width = this.getWidth();
		axisGap = 40;
		hatchedLineOffset = 5;
		numofhatches =10;
		yOffsetforletters = 5;
		xOffsetforletters = 25;
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
		//Draw line  of axis
		g2.drawLine(axisGap, height - axisGap, axisGap, axisGap);
	    g2.drawLine(axisGap, height - axisGap, width - axisGap, height - axisGap);
	    
	    //Draw hatched lines and text across the two axis
	    //Y axis:
	    for (int i = 0; i <= numofhatches; i++) {
	         int x1 = axisGap-hatchedLineOffset;
	         int y0 = height - (axisGap + ((height - (axisGap * 2)) * i) / numofhatches );
	         g2.drawLine(axisGap, y0, x1, y0);

	         double xLabel;
	         if (i==0) {
	        	 xLabel = xMin;
	         } else {
	        	 double xLabelOffset = xMax/(numofhatches-1);
	        	 xLabel = xLabelOffset * i;
	         }
			g2.drawString(String.format("%.0f", xLabel), x1-xOffsetforletters, y0 + yOffsetforletters);
	      }

	      //X axis:
	      for (int i = 0; i <= numofhatches; i++) {
	         int x0 = axisGap + ((width - (axisGap * 2)) / numofhatches)*i ;
	         int y0 = height - axisGap;
	         int y1 = y0 + hatchedLineOffset;
	         g2.drawLine(x0, y0, x0, y1);
	         double yLabel;
	         if (i==0) {
	        	 yLabel = yMin;
	         } else {
	        	 double yLabelOffset = yMax/(numofhatches-1);
	        	 yLabel = yLabelOffset * i;
	         }
			g2.drawString(String.format("%.0f", yLabel), x0 - 5, y1+15);
	      }
		
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
				Double xPoint = (myBondTrade.getColumnValue(xColumn, index) - xMin);
				Double yPoint = (myBondTrade.getColumnValue(yColumn, index) - yMin);
	
				double xResult = (xPoint*xGraphValuesRange)/xValuesRange;
				double yResult = (yPoint*yGraphValuesRange)/yValuesRange;
				
				double x = xResult + axisGap;
				double y = (height - yResult) - axisGap;
						
				
				Ellipse2D.Double e = new Ellipse2D.Double(x-(pointDimensions/2), y-(pointDimensions/2), pointDimensions, pointDimensions);
				shapes.add(e);

				
				g2.draw(e);
				g2.fill(e);	
			}
		}
		
		
		
		
		
		
	}
	
	
}
