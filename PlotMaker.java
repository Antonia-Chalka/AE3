import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class PlotMaker extends JComponent{
	
	private BondTrade myBondTrade;
	private int xColumn, yColumn;
	
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
	
	public void paintComponent(Graphics g){
		
		if (myBondTrade != null) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.CYAN);
			
			int width = this.getWidth();
			int height = this.getHeight();
			double xMax = myBondTrade.getMaxValue(xColumn);
			double xMin = myBondTrade.getMinValue(xColumn);
			
			double yMax = myBondTrade.getMaxValue(yColumn);
			double yMin = myBondTrade.getMinValue(yColumn);
			
			double xDifference = xMax - xMin;
			double yDifference = yMax - yMin;
			
			//0,1,2 
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
