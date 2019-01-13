import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


//get trade details to work
//make your code look nocer

public class Main extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton openButton, quitButton;	
	private PlotMaker plotPanel;	
	private JComboBox<String> xBox, yBox;
	private JTextField filenameField;
	private JTextArea detailField;
	BondTrade myBondTrade = new BondTrade();
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Main();
	}
	
	public Main() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(600, 600));
		setup();
		
		pack();
		this.setVisible(true);
	}
	
	private void setup(){
		//Set up panels
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel southPanel = new JPanel();
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
				
		//Set up north panel (file options and quit button)
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
		
				
		//Middle Panel (CHANGE)
		plotPanel = new PlotMaker();
		plotPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				checkMouse(e);
				System.out.println("Clicked!");
			}
			
		});
		mainPanel.add(plotPanel, BorderLayout.CENTER);
				
				
		//South (lower) Panel (plot options)
		xBox = new JComboBox<String>();
		xBox.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent arg0) {
						if (xBox.getSelectedIndex() != -1) {
							plotPanel.setXColumn(xBox.getSelectedIndex());
							plotPanel.repaint();
							}
								
					}
				});
		southPanel.add(xBox);
		
		
		yBox = new JComboBox<String>();
		yBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (yBox.getSelectedIndex() != -1) {
					plotPanel.setYColumn(yBox.getSelectedIndex());
					plotPanel.repaint();
					}
			}
		});
		southPanel.add(yBox);
		
		detailField = new JTextArea("Trade Details.");
		detailField.setEditable(false);
		detailField.setPreferredSize(new Dimension (250, 70));
		detailField.setColumns(4);
		southPanel.add(detailField);
		
		
		//At the end
		this.add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// Save where action originated from
		Object source = e.getSource();
			
		if (source == openButton){ //if 'Open' Button was pressed
			System.out.println("Open Button"); 
			resetFileData();
			try {
				
				processFile();
			} catch (FileNotFoundException e1) {
				System.out.print("Error while processing file - file not found");
				e1.printStackTrace();
			}
				
		} else if (source == quitButton){  //if 'Quit' Button was pressed
			System.out.println("Quit Button"); 
			System.exit(0);
		}	
	}
	
	private void processFile() throws FileNotFoundException {
		JFileChooser inputfileChooser = new JFileChooser();
		inputfileChooser.setCurrentDirectory(new File("."));
		inputfileChooser.setFileFilter(new FileNameExtensionFilter("Bond Trade (.csv)","csv"));
		
		if (inputfileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			
			File inputFile = inputfileChooser.getSelectedFile();
			filenameField.setText("File Name: " + inputFile.getName());
			
			Scanner fileScanner = new Scanner(inputFile);
			while (fileScanner.hasNextLine()) {
				myBondTrade.addData(fileScanner.nextLine());	
			}
			fileScanner.close();
			
			//get x and y column and add them
			for (String header: myBondTrade.getHeaders())  {
				xBox.addItem(header);
				yBox.addItem(header);
			}

			plotPanel.loadData(myBondTrade);
			
		}
	}
	
	private void resetFileData(){
		myBondTrade = new BondTrade();
		xBox.removeAllItems();
		yBox.removeAllItems();
		
	}
	
	public void checkMouse(MouseEvent e) {
		System.out.println("Checking clicks...");
		Point mousePoint = e.getPoint();
		int index = 0;
		ArrayList<Ellipse2D.Double> shapes = plotPanel.returnShapes();
		for (Ellipse2D.Double shape : shapes) {
			if (shape.contains(e.getPoint())) {
				System.out.println("mouse click: " + e.getPoint());
				System.out.println("shape.x: " + shape.x);
				System.out.println("shape.y: " + shape.y);
				
				index = plotPanel.locateClickedShape(shape.x, shape.y);
				if (index !=-1){

					System.out.println("Found click!");
					detailField.setText( myBondTrade.getBondDetails(index)); 
				}
				break;
			}
		}
	}

}
