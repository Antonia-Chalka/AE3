import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main extends JFrame implements ActionListener{

	private JButton openButton, quitButton;	
	private PlotMaker plotPanel;	
	private JComboBox<String> xBox, yBox;
	private JTextField filenameField, detailField;
	private BondTrade myBondTrade = new BondTrade();
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Main();
	}
	
	public Main() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 500);
		
		setup();
		
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
		mainPanel.add(plotPanel, BorderLayout.CENTER);
				
				
		//South (lower) Panel (plot options)
		xBox = new JComboBox<String>();
		southPanel.add(xBox);
		
		yBox = new JComboBox<String>();
		southPanel.add(yBox);
				
		detailField = new JTextField("Trade Details.");
		detailField.setEditable(false);
		detailField.setHorizontalAlignment(JTextField.CENTER);
		detailField.setSize(250, 50);
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
			ArrayList<String> bondTradeHeaders = myBondTrade.getHeaders();
			for (int i=0; i < bondTradeHeaders.size(); i++)  {
				xBox.addItem(bondTradeHeaders.get(i));
				yBox.addItem(bondTradeHeaders.get(i).toString());
			}

			plotPanel.loadData(myBondTrade);
			
			
		}
	}

}
