package com.fix_me;

import lombok.Getter;
import lombok.Setter;

public class MessageHandler {
	private final int routerSenderIDTag = 49;
	private final int marketTag = 56;
	private final int sideTag = 54;
	private final int symbolTag = 55;
	private final int priceTag = 44;
	private final int quantityTag = 38;
	private final int checksumTag = 10;
	private final int statusTag = 39;

	private @Getter @Setter String routerSenderID;
	private @Getter @Setter String market;
	private @Getter @Setter String side;
	private @Getter @Setter String symbol;
	private @Getter @Setter float price = 0;
	private @Getter @Setter int quantity = 0;
	private @Getter String checksum;
	private @Getter @Setter String status;

	private @Getter String message;

	public MessageHandler(String message) {
		this.message = message;
		this.analyzeMsg();
	}

	private void analyzeMsg() {
		String[] splitLine = message.split("\\|");
		for (String keyValue : splitLine) {
			String[] splitTags = keyValue.split("=");
			switch (splitTags[0]) {
				case "49":
					routerSenderID = splitTags[0];
					break;
				case "56":
					market = splitTags[1];
					break;
				case "54":
					side = splitTags[1];
					break;
				case "55":
					symbol = splitTags[1];
					break;
				case "44":
					price = Float.parseFloat(splitTags[1]);
					break;
				case "39":
					status = splitTags[1];
					break;
				case "38":
					quantity = Integer.parseInt(splitTags[1]);
					break;
				case "10":
					checksum = splitTags[1];
					break;
				default:
			}
		}
	}

	public String CalculateChecksum(String message) {
		String checksumMsg = message;
		checksumMsg = checksumMsg.replace('|', '\u0001');

		int sum = 0;
		int char_val = 0;

		for (int i = 0; i < checksumMsg.length(); i++) {
			char_val = checksumMsg.charAt(i);
			sum = sum + char_val;
		}

		checksum = String.valueOf(sum % 256);
		for (int i = 3; i > checksum.length(); i--) {
			checksum = "0" + checksum;
		}
		return checksum;
	}
}