package com.fix_me;

import lombok.Getter;
import lombok.Setter;

public class MarketMessageHandler {
	String message;
	@Getter @Setter String routerSenderID;
	@Getter @Setter String routerRecieverId;
	@Getter @Setter int status;
	@Getter @Setter int checksum;

	public MarketMessageHandler(String message) {
		this.message = message;
		parseMessage();
	}

	public void parseMessage() {
		String[] MsgSections = message.split("\\|");
		for (String keyValue : MsgSections) {
			String[] MsgSection = keyValue.split("=");
			if (MsgSection.length == 2) {
				switch (MsgSection[0]) {
					case "49":
						this.routerSenderID = MsgSection[1];
						break;
					case "56":
						this.routerRecieverId = MsgSection[1];
						break;
					case "39":
						this.status = Integer.parseInt(MsgSection[1]);
						break;
					case "10":
						this.checksum = Integer.parseInt(MsgSection[1]);
						break;
					default:
				}
			}
		}
	}

	public boolean validateChecksum() {
		String[] splitMessage = message.split("\\|");

		if (splitMessage.length > 3) {
			// add 1 for end pipe
			int len_checksum = splitMessage[splitMessage.length - 1].length() + 1;

			String chkmessage = message.substring(0, message.length() - len_checksum);
			chkmessage = chkmessage.replace('|', '\u0001');

			int sum = 0;
			int char_val = 0;

			for (int i = 0; i < chkmessage.length(); i++) {
				char_val = chkmessage.charAt(i);
				sum = sum + char_val;
			}

			if (checksum == 0) {
				System.out.println(message);
				parseMessage();
			}
			// System.out.println("fix message checksum " + checksum);

			if (checksum == (sum % 256))
				return true;
			else
				return false;
		}
		return false;
	}
}
