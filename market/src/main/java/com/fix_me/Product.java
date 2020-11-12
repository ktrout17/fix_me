package com.fix_me;

import lombok.Getter;
import lombok.Setter;

public class Product {
	@Getter @Setter
	private String item = null;
	@Getter @Setter
	private float price = 0;
	@Getter @Setter
	private int quantity = 0;
	
	public Product(String item) throws NumberFormatException {
			String product[] = item.split(",");
			setItem(product[0]);
			setPrice(Float.parseFloat(product[1]));
			setQuantity(Integer.parseInt(product[2]));
	}
}
