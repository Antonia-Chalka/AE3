import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial") //suppressed warning as it is not necessary for this exercise
public class AE3 extends JFrame implements ActionListener{

	// Class variables contain all GUI components (buttons, panels, combo boxes etc) and all the BondTrades (BondTrade)
	private JPanel mainPanel, northPanel, southPanel;
	private JButton openButton, quitButton;	
	private PlotMaker plotPanel;	
	private JComboBox<String> xBox, yBox;
	private JTextField filenameField;
	private JTextArea detailField;
	private JFileChooser inputfileChooser;
	private BondTradeData myBondTrades = new BondTradeData();
	
	//Main method
	public static void main(String[] args) {
		new AE3();
	}
	
	//Standard general set up setting for the GUI
	public AE3() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600, 600));
		
		mainPanel = new JPanel(new BorderLayout());
		setupSouthPanel();
		setupPlotPanel();
		setupNorthPanel();
		this.add(mainPanel);
		
		pack();
		this.setVisible(true);
	}
	
	//Set up the north (upper) panel, which includes the open button, quit button and filename field, their locations and appropriate listeners
	private void setupNorthPanel(){
		northPanel = new JPanel(new BorderLayout());
		mainPanel.add(northPanel, BorderLayout.NORTH);
				
		openButton = new JButton("Open");
		openButton.addActionListener(this);
		northPanel.add(openButton, BorderLayout.WEST);
				
		quitButton = new JButton("Quit");
		quitButton.addActionListener(this);
		northPanel.add(quitButton, BorderLayout.EAST);
				
		filenameField = new JTextField("No file has been selected.");
		filenameField.setEditable(false);
		filenameField.setHorizontalAlignment(JTextField.CENTER);
		northPanel.add(filenameField, BorderLayout.CENTER);		
	}

	//Set up the plot panel and mouse listener
	private void setupPlotPanel() {
		plotPanel = new PlotMaker();
		mainPanel.add(plotPanel, BorderLayout.CENTER);
		plotPanel.addMouseListener(new MouseAdapter() {
			//When user clicks on the graph, check if click corresponds to a graph point (via checkMouseClick(e)) and respond appropriately
			public void mouseClicked(MouseEvent e) {
				checkMouseClick(e);
			}
		});	
	}
	
	//Set up combo boxes and trade details field, their locations and appropriate listeners
	private void setupSouthPanel() {
		southPanel = new JPanel();
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		xBox = new JComboBox<String>();
		southPanel.add(xBox);		
		xBox.addItemListener(new ItemListener() {
					//if user changes combobox item (indicated by xBox.getSelectedIndex() != -1), set the graph's x axis to the selected column and redraw
					@Override
					public void itemStateChanged(ItemEvent arg0) {
						if (xBox.getSelectedIndex() != -1) { 
							plotPanel.setXColumn(xBox.getSelectedIndex());
							plotPanel.repaint();
							}		
					}
				});
				
		yBox = new JComboBox<String>();
		southPanel.add(yBox);
		yBox.addItemListener(new ItemListener() {
			//if user changes combobox item (indicated by yBox.getSelectedIndex() != -1), set the graph's y axis to the selected column and redraw
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (yBox.getSelectedIndex() != -1) {
					plotPanel.setYColumn(yBox.getSelectedIndex());
					plotPanel.repaint();
					}
			}
		});
			
		detailField = new JTextArea("Choose a trade to see the details.");
		detailField.setEditable(false);
		detailField.setColumns(4);
		southPanel.add(detailField);		
	}
	
	//This method checks the source of the actionlistener of the components and responds accordingly
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == openButton){ //if 'Open' Button was pressed, open a file chooser via the processFile() method
			//Set up file chooser
			inputfileChooser = new JFileChooser();
			inputfileChooser.setCurrentDirectory(new File(".")); //Set directory of file chooser to current working directory
			inputfileChooser.setFileFilter(new FileNameExtensionFilter("Bond Trade (.csv)","csv")); //Display only .csv files, and with an appropriate format name
			
			if (inputfileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { //when the user chooses a file	
				processFile();	
			}	
		} else if (e.getSource() == quitButton){  //if 'Quit' Button was pressed, close the program
			System.exit(0);
		}	
	}
	
	/*This method is called when the open button of the filechooser is pressed. It: 
	 * 1. clears any previous file data from the panels
	 * 2. Sets up the top-most text field with the file's name
	 * 3. Adds the bond trade information of the file to a BondTrade object
	 * 4. Sets up the headers for the combo boxes
	 * 5. Sets up the plot panel
	 */
	private void processFile(){	
		//Reset previous file data (remove the combo boxes headers and sets the trade detail field to a generic message)
		myBondTrades = new BondTradeData();
		xBox.removeAllItems();
		yBox.removeAllItems();
		detailField.setText("Choose a trade to see the details.");
			
		File inputFile = inputfileChooser.getSelectedFile(); //Store selected file
			
		filenameField.setText("File Name: " + inputFile.getName()); //Display file name
			
		//Add the bond trade information of the file to a BondTrade object
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(inputFile);
			while (fileScanner.hasNextLine()) {
				myBondTrades.addData(fileScanner.nextLine());
			}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error while processing file - file not found.");
			e.printStackTrace();
		}
		
		//Sets up the headers for the combo boxes
		for (String header: myBondTrades.getHeaders())  {
			xBox.addItem(header);
			yBox.addItem(header);	
		}
		plotPanel.loadData(myBondTrades); //load bond trade data into plot panel to create scatterplot
		yBox.setSelectedIndex(1); //set y Combo box to display the 2nd column of data (x combo box displays 1st column by default)	
	}
	
	/*TODO add comments
	 * This method is triggered when the user performs a mouse lick on the plot area.
	 * It cycles through each shape (i.e. dot) on the graph.
	 * If it finds that the position of the mouseclick is within a graph dot,
	 * 
	 * 
	 */
	public void checkMouseClick(MouseEvent e) {
		for (Ellipse2D.Double shape : plotPanel.getPlotShapes()) { //cycle for each shape
			if (shape.contains(e.getPoint())) { //if the position of the mouseclick is within a dot on the graph	
				int index = plotPanel.locateClickedShapeIndex(shape.x, shape.y); //this method will return -1 if mouse click does not correspond to a shape
				if (index !=-1){
					detailField.setText( myBondTrades.getBondDetails(index)); 
					break;
				}	
			}
		}
	}
}
