/*  Name:  Matthew Weinert
	Course: CNT 4714 – Spring 2020
	Assignment title: Project 1 – Event-driven Enterprise Simulation
	Date: Sunday January 26, 2020
 */

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DunderMifflin extends JFrame {

	JPanel panel1 = new JPanel();
	FlowLayout layout = new FlowLayout();
	JPanel panel2 = new JPanel();
	JLabel numberOfItems, bookID, quantityOfItem, itemInfo, orderSubtotal;
	JTextField numTF, idTF, quantityTF, itemTF, subtotalTF;
	JButton processItem, confirmItem, viewOrder, finishOrder, newOrder, exit;
	ArrayList<String> inv = new ArrayList<String>();
	String[] stuff = null;
	String[] order = null;
	DecimalFormat df = new DecimalFormat("#.00");
	List<String> list = new ArrayList<String>();
	List<String> finalList = new ArrayList<String>();
	boolean bookExists = false;
	int orderNumber = 1;
	int totalNumberOfItems = 0;
	int infoNum = 0;
	int percentOff = 0;
	String string = null;
	String lastString = null;
	StringBuilder totalList = new StringBuilder();
	StringBuilder finalTotalList = new StringBuilder();
	double totalDiscounted = 0;
	double total = 0;
	Calendar cal = Calendar.getInstance();

	public DunderMifflin() {

		panel1.setLayout(new GridLayout(6, 6));
		panel2.setLayout(layout);

		numberOfItems = new JLabel("Number of items in this order: ");
		numTF = new JTextField(20);
		panel1.add(numberOfItems);
		panel1.add(numTF);

		bookID = new JLabel("Enter Book ID for item #" + orderNumber + ": ");
		idTF = new JTextField(20);
		panel1.add(bookID);
		panel1.add(idTF);

		quantityOfItem = new JLabel("Enter quantity for item #" + orderNumber + ": ");
		quantityTF = new JTextField(20);
		panel1.add(quantityOfItem);
		panel1.add(quantityTF);

		itemInfo = new JLabel("Item #" + (orderNumber - infoNum++) + " info: ");
		itemTF = new JTextField(20);
		panel1.add(itemInfo);
		panel1.add(itemTF);

		orderSubtotal = new JLabel("Order subtotal for " + totalNumberOfItems + " item(s): ");
		subtotalTF = new JTextField(20);
		panel1.add(orderSubtotal);
		panel1.add(subtotalTF);

		processItem = new JButton("Process Item #" + orderNumber);
		confirmItem = new JButton("Confirm Item #" + orderNumber);
		viewOrder = new JButton("View Order");
		finishOrder = new JButton("Finish Order");
		newOrder = new JButton("New Order");
		exit = new JButton("Exit");

		panel2.add(processItem);
		panel2.add(confirmItem);
		panel2.add(viewOrder);
		panel2.add(finishOrder);
		panel2.add(newOrder);
		panel2.add(exit);
		add(panel1, "North");
		add(panel2, "South");

		setVisible(true);
		this.setSize(800, 250);

		scanInvFile();
		
		numTF.setEnabled(true);
		idTF.setEnabled(true);
		quantityTF.setEnabled(true);
		itemTF.setEnabled(false);
		subtotalTF.setEnabled(false);

		finishOrder.setEnabled(false);
		confirmItem.setEnabled(false);
		viewOrder.setEnabled(false);
		
		processItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				searchInv(idTF.getText());

				numTF.setEnabled(false);
				processItem.setEnabled(false);
				viewOrder.setEnabled(false);
				finishOrder.setEnabled(false);
				confirmItem.setEnabled(true);
				
				if (idTF.getText().equals(stuff[0])) {
					bookExists = true;
				}
			}
		});

		confirmItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (bookExists && !(quantityTF.getText().isBlank())) {
					JOptionPane.showMessageDialog(panel2, "Item #" + orderNumber++ + " accepted.");
					bookExists = false;
					processItem.setEnabled(true);
					viewOrder.setEnabled(true);
					finishOrder.setEnabled(true);
					bookID.setText("Enter Book ID for item #" + orderNumber + ": ");
					quantityOfItem.setText("Enter quantity for item #" + orderNumber + ": ");
					itemInfo.setText("Item #" + (orderNumber - infoNum) + " info: ");
					processItem.setText("Process Item #" + orderNumber);
					confirmItem.setText("Confirm Item #" + orderNumber);
					subtotalTF.setText("$" + df.format(total));
					numTF.setText(String.valueOf(totalNumberOfItems));
					orderSubtotal.setText("Order subtotal for " + totalNumberOfItems + " item(s): ");
					idTF.setText("");
					quantityTF.setText("");
					panel1.validate();
					panel1.repaint();
				} else {
					JOptionPane.showMessageDialog(panel2, "Book ID " + idTF.getText() + " not in file.");
					processItem.setEnabled(true);
				}
			}
		});

		viewOrder.addActionListener(new ActionListener() {
				
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				appendList();
				
				JOptionPane.showMessageDialog(panel2, totalList);

			}
		});

		finishOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				appendList();
				JOptionPane.showMessageDialog(panel2, "Date: " + cal.getTime() + "\n \n Number of line items: " + totalNumberOfItems 
						+ "\n \n Item# / ID / Title / Price / Qty / Disc % / Subtotal: \n \n" + totalList + "\n \n Order subtotal: " + subtotalTF.getText() 
						+ "\n \n Tax Rate: 6% \n \n" + "Tax amount: $" + df.format(total*0.06) + "\n \n Order Total: $" + df.format(total + (total * 0.06)) + "\n \n Thanks for shopping at Dunder Mifflin!");
				finishFile(false);
			}
		});
		
		newOrder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				orderNumber = 1;
				totalNumberOfItems = 0;
				infoNum = 0;
				percentOff = 0;
				totalDiscounted = 0;
				total = 0;
				list.clear();
				finalList.clear();
				finalTotalList.setLength(0);
				totalList.setLength(0);
				numTF.setEnabled(true);
				idTF.setEnabled(true);
				quantityTF.setEnabled(true);
				itemTF.setEnabled(false);
				numTF.setEnabled(true);
				subtotalTF.setEnabled(false);
				processItem.setEnabled(true);
				confirmItem.setEnabled(false);
				viewOrder.setEnabled(false);
				finishOrder.setEnabled(false);
				exit.setEnabled(true);
				bookID.setText("Enter Book ID for item #" + orderNumber + ": ");
				quantityOfItem.setText("Enter quantity for item #" + orderNumber + ": ");
				itemInfo.setText("Item #" + (orderNumber - infoNum) + " info: ");
				processItem.setText("Process Item #" + orderNumber);
				confirmItem.setText("Confirm Item #" + orderNumber);
				orderSubtotal.setText("Order subtotal for " + totalNumberOfItems + " item(s): ");
				subtotalTF.setText("");
				itemTF.setText("");
				numTF.setText("");
				idTF.setText("");
				quantityTF.setText("");
				finishFile(true);
				panel1.validate();
				panel1.repaint();
			}
		});

		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				DunderMifflin.this.processWindowEvent(new WindowEvent(DunderMifflin.this, WindowEvent.WINDOW_CLOSING));
				finishFile(true);
				System.exit(0);
			}
		});
	}

	public static void main(String args[]) {
		DunderMifflin GUI = new DunderMifflin();
	}

	public void scanInvFile() {

		try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {

			String line;

			while ((line = br.readLine()) != null) {
				inv.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchInv(String bookID) {

		int i = 0;
		
		while (i < inv.size()) {

			stuff = inv.get(i++).split(",");

			if (stuff[0].compareTo(bookID) == 0) {
				
				addTotal(stuff[2]);

				itemTF.setText(stuff[0] + stuff[1] + stuff[2] + " %" + percentOff + " " + totalDiscounted);
				
				string = itemTF.getText();
															
				java.util.Date now = cal.getTime();
				
				java.sql.Timestamp currentTime = new java.sql.Timestamp(now.getTime());
				
				String ts = new SimpleDateFormat("yyyyMMddHHmm").format(currentTime);
				
				String orderAmt = "";
				
				orderAmt = quantityTF.getText();
				
				list.add(string);
							
				finalTotalList.append(ts).append(", ").append(stuff[0] + ", "+ stuff[1] + ", " + stuff[2] + ", " + orderAmt + ", "+ percentOff + ", " + totalDiscounted + ", ")
					.append(cal.getTime()).append("\n");			
				
				break;
			}			
		}
	}

	public void addTotal(String price) {
		totalNumberOfItems += Integer.parseInt(quantityTF.getText());

		if (Integer.parseInt(quantityTF.getText()) > 0) {
			if (Integer.parseInt(quantityTF.getText()) < 5) {
				totalDiscounted = (Double.parseDouble(price) * Integer.parseInt(quantityTF.getText()));
				total += totalDiscounted;
				percentOff = 0;
				return;
			} else if (Integer.parseInt(quantityTF.getText()) < 10) {
				totalDiscounted = (Double.parseDouble(price) * Integer.parseInt(quantityTF.getText())
						- ((Double.parseDouble(price) * Integer.parseInt(quantityTF.getText()) * 0.10)));
				total += totalDiscounted;
				percentOff = 10;
				return;
			} else {
				totalDiscounted = (Double.parseDouble(price) * Integer.parseInt(quantityTF.getText())
						- ((Double.parseDouble(price) * Integer.parseInt(quantityTF.getText()) * 0.15)));
				total += totalDiscounted;
				percentOff = 15;
				return;
			}
		} else {
			total += (Double.parseDouble(price) * Integer.parseInt(quantityTF.getText()));
		}
	}

	public void totalItems() {
		
	}
	
	public void finishFile(boolean Del) {
		
		try {
			
			File file = new File("transactions.txt");
			
			if(Del) {
				file.delete();
				return;
			}
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			PrintWriter writer = new PrintWriter(file);
			
			writer.write(finalTotalList.toString());

			writer.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void appendList() {
				
		for(int z = 0; z < list.size(); z++) {
			totalList.append(z+1).append(". ").append(list.get(z)).append("\n");
		}
	}
}
