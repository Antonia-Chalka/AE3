import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class PlotMaker extends JComponent{
	
	private BondTrade myBondTrade;
	private int xColumn, yColumn;
	private int axisGap, height, width, hatchedLineOffset, numofhatches, yOffsetforletters,xOffsetforletters;
	
	 
	public PlotMaker(){
		xColumn = 0;
		yColumn = 0;
	}
	
	public void loadData(BondTrade myBondTrade) {
		this.myBondTrade = myBondTrade;
		repaint();
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
		axisGap = 35;
		hatchedLineOffset = 5;
		numofhatches =10;
		yOffsetforletters = 5;
		xOffsetforletters = 25;
	}
	
	public void paintComponent(Graphics g){
		
		if (myBondTrade != null) {
			calculateDimensions();
			
			double xMax = myBondTrade.getMaxValue(xColumn);
			double xMin = myBondTrade.getMinValue(xColumn);
			
			double yMax = myBondTrade.getMaxValue(yColumn);
			double yMin = myBondTrade.getMinValue(yColumn);
			double xLabelOffset = xMax/10;
			double yLabelOffset = yMax/10;
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.BLACK);
			
			//Draw axis (line only)
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
		        	 xLabel = xLabelOffset * i;
		         }
				g2.drawString(String.format("%.1f", xLabel), x1-xOffsetforletters, y0 + yOffsetforletters);
		      }

		      // and for x axis
		    
		      for (int i = 0; i <= numofhatches; i++) {
		         int x0 = axisGap + ((width - (axisGap * 2)) / numofhatches)*i ;
		         int y0 = height - axisGap;
		         int y1 = y0 + hatchedLineOffset;
		         g2.drawLine(x0, y0, x0, y1);
		         double yLabel;
		         if (i==0) {
		        	 yLabel = yMin;
		         } else {
		        	 yLabel = yLabelOffset * i;
		         }
				g2.drawString(String.format("%.1f", yLabel), x0 - 5, y1+15);
		      }
			
			
			double xDifference = xMax - xMin;
			double yDifference = yMax - yMin;
			
			//Loop for each point
			for (int index = 0; index < myBondTrade.getRowCounter()-1; index++){
				Double xPoint = myBondTrade.getColumnValue(xColumn, index);
				Double yPoint = myBondTrade.getColumnValue(yColumn, index);
				
	
				double xOffset = xPoint - xMin;
				double yOffset = yPoint - yMin;
	
			
				double xResult = xOffset / xDifference;
				double yResult = yOffset / yDifference;
	
				// find the proportion between min/max
	
				double x = xResult * width;
				double y = yResult * height;
				
				
				
				Ellipse2D.Double e = new Ellipse2D.Double(y,x, 5, 5);
				
				g2.draw(e);
				g2.fill(e);	
			}
		}
		
		
		
		
		
	}
	
	
}
