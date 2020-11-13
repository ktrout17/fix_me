package com.fix_me;

import java.io.*;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Market {
	public static final String MARKET = "\u001B[93m";
	public static final String BROKER   = "\u001B[96m";
	public static final String ACCEPT = "\u001B[32m";
	public static final String REJECT = "\u001B[31m";
	public static final String RESET  = "\u001B[0m";

	private static final String serverIp = "127.0.0.1";
	private static final int serverPort = 5001;
	private static BufferedReader in = null;
	private static PrintWriter out = null;
	private static Socket socket = null;
	private static Scanner scan = null;
	private static String responses = null;
	private static String marketId;
	private static ArrayList<Product> products = new ArrayList<Product>();
	private static BrokerMessageHandler brokerMsg = null;

	public static void main(String[] args) {
		initializeConnections();
		getProductlist();

		String msg = null;
		try {
			while (true) {
				msg = in.readLine();
				if (!msg.equals(null)) {
					if (msg.contains("close")) {
						String disconnectingID[] = msg.split(",");
						System.out.println("[MARKET " + marketId + "] Broker " + disconnectingID[1] + " Disconnected.");
					}else{
						String brokerId = getBrokerMsg(msg).getRouterSenderID();
						System.out.println(BROKER + "[MARKET " + marketId + "] Request from Broker " + brokerId + ": " + msg + RESET);
						Sleep(1);
						brokerMsg = getBrokerMsg(msg);
						if (analyseMsg(brokerMsg)) {
							//  Accept msg
							sendResponses("|39=2|");
						} else {
							// Decline msg
							sendResponses("|39=8|");
						}
					}
				}
			}
		} catch (NullPointerException e) {
			System.err.println("Lost connection to server.");
			// e.printStackTrace();
			closeConnections();
		} catch (IOException e) {
			System.err.println("Lost connection to server.");
			// e.printStackTrace();
			closeConnections();
			System.exit(1);
		}
	}

	private static boolean analyseMsg(BrokerMessageHandler brokerMsg) {
		if (checkIfProductExistInProductList(brokerMsg)) {
			return true;
		}
//		System.out.println("Market.analyseMsg()");
		return false;
	}

	private static boolean checkIfProductExistInProductList(BrokerMessageHandler brokerMsg) {
		// checks whether the product in the product list
		for (Product product : products) {
			if (brokerMsg.getSymbol().equalsIgnoreCase(product.getItem())) {
				if (clarifyBuyOrSell(brokerMsg, product)) {
					return true;
				}
			}
		}
//		System.out.println("Market.checkIfProductExistInProductList()");
		return false;
	}

	private static boolean clarifyBuyOrSell(BrokerMessageHandler brokerMsg, Product product) {
		// Market quantity stays 20, so if broker wants to sell,
		// the quantity can't be more than 20, else if,
		// the broker wants to buy, the quantity can't be less the 0

		if (brokerMsg.getSide().equals("1")) {
			// broker wants to buy
			if ((product.getQuantity() - brokerMsg.getQuantity()) >= 0) {
				if (validatePricePoint(1, brokerMsg, product)) {
					return true;
				}
			}
		} else if (brokerMsg.getSide().equals("2")) {
			// broker wants to sell
			if ((product.getQuantity() + brokerMsg.getQuantity() <= 20)) {
				if (validatePricePoint(2, brokerMsg, product)) {
					return true;
				}
			}
		}
//		System.out.println("Market.clarifyBuyOrSell()");
		return false;
	}

	private static boolean validatePricePoint(int side, BrokerMessageHandler brokerMsg, Product product) {
		// Markets price for products are set, so if broker wants to sell,
		// the price needs to be greater or equal to the markets price,
		// else if, the broker wants to buy, the price from the broker
		// needs less than or equal to the markets price.
		if (side == 1) {
			if (brokerMsg.getPrice() >= product.getPrice()) {
				product.setQuantity(product.getQuantity() - brokerMsg.getQuantity());
				if (updateProductList(product)) {
					return true;
				}
			}
		} else if (side == 2) {
			if (brokerMsg.getPrice() <= product.getPrice()) {
				product.setQuantity(product.getQuantity() + brokerMsg.getQuantity());
				if (updateProductList(product)) {
					return true;
				}
			}
		}
//		System.out.println("Market.validatePricePoint()");
		return false;
	}

	private static boolean updateProductList(Product prod) {
		// Updates the arraylist product list for other brokers
		for (Product product : products) {
			if (product.getItem().equalsIgnoreCase(prod.getItem())) {
				product.setQuantity(prod.getQuantity());
				break;
			}
		}
		try {
			// writes arraylist to the file
			BufferedWriter file = new BufferedWriter(new FileWriter("Product_List.txt"));
			for (Product product : products) {
				file.write(product.getItem() + "," +  product.getPrice() + "," + product.getQuantity()+ "\n");
			}
			file.close();
			return true;
		} catch (IOException e) {
			System.err.println("Failed to update Product_list.");
			return false;
			// System.exit(1);
		}
	}

	private static void getProductlist() {
		try {
			File fix_message = new File("Product_List.txt");
			Boolean exists = fix_message.exists();
			if (!exists) {
				System.out.println("File does not exist - Creating file.");
				fix_message.createNewFile();
			}
			Scanner scan = new Scanner(fix_message);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				Product product = new Product(line);
				products.add(product);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			closeConnections();
		} catch (IOException e) {
			System.err.println("Failed to create file.");
			closeConnections();
		} catch (NumberFormatException e) {
			System.err.println("Product file has incorrect format.");
			System.out.println("[ item name,price,quantity ]");
			closeConnections();
		}
		// return null;
	}

	private static void initializeConnections() {
		try {
			socket = new Socket(serverIp, serverPort);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			marketId = in.readLine();
			// marketId
			System.out.println(MARKET + "[MARKET " + marketId + "] connected to Router." + RESET);
		} catch (IOException e) {
			System.err.println("Failed to create connection");
			System.exit(1);
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	private static void Sleep(long time) {
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void sendResponses(String response) {
		String FIXMsg = constructFIXmsg(response);
		out.println(FIXMsg);
		if (response.contains("39=2"))
			System.out.println(ACCEPT + "[MARKET " + marketId + "] order ACCEPTED." + RESET);
		if (response.contains("39=8"))
			System.out.println(REJECT + "[MARKET " + marketId + "] order REJECTED." + RESET);
		System.out.println(MARKET + "[MARKET " + marketId + "] responding with: " + FIXMsg + RESET);
	}

	private static BrokerMessageHandler getBrokerMsg(String request) {

		BrokerMessageHandler check = new BrokerMessageHandler(request);
		return check;
	}

	private static String getChecksum(String response) {
		BrokerMessageHandler check = new BrokerMessageHandler(response);
		String checksum = check.CalculateChecksum(response);
		return checksum;
	}

	private static String constructFIXmsg(String response) {
		String fullResponse = "49=" + marketId + "|56=" + brokerMsg.getRouterSenderID() + response;
		String checksum = getChecksum(fullResponse.toString());
		String FIXMsg = fullResponse + "10=" + checksum + "|";
		return FIXMsg;
	}

	private static void closeConnections() {
		try {
			socket.close();
			System.out.println(MARKET + "[MARKET " + marketId + "] connection Closing..." + RESET);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Failed to close connections");
			System.exit(1);
		}
	}
}
