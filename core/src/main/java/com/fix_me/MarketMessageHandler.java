package com.fix_me;

public class MarketMessageHandler {
    String message;

    int quantity;
    String market;
    float price;
    String routerSenderID;
    int status;
    String symbol;
    int side;
    int checksum;



    public MarketMessageHandler(String message){
        this.message = message;
        parseMessage();
    }

    public void parseMessage(){
        String[] MsgSections= message.split("\\|");
        for (String keyValue: MsgSections) {
            String[] MsgSection = keyValue.split("=");
            if (MsgSection.length == 2){
                switch (MsgSection[0]) {
                    case "49":
                        this.routerSenderID = MsgSection[1];
                        break;
                    case "56":
                        this.market = MsgSection[1];
                        break;
                    case "54":
                        this.side = Integer.parseInt(MsgSection[1]);
                        break;
                    case "55":
                        this.symbol = MsgSection[1];
                        break;
                    case "44":
                        this.price = Float.parseFloat(MsgSection[1]);
                        break;
                    case "39":
                        this.status = Integer.parseInt(MsgSection[1]);
                        break;
                    case "38":
                        this.quantity = Integer.parseInt(MsgSection[1]);
                        break;
                    case "10":
                        this.checksum = Integer.parseInt(MsgSection[1]);
                        break;
                    default:
                }
            }
        }
    }


    public boolean validateChecksum(){
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

            if (checksum == 0)
                parseMessage();
            System.out.println("fix message checksum " + checksum);

            if (checksum == (sum % 256))
                return true;
            else
                return false;
        }
        return false;
    }
}
