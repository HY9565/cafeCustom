package com.team;

// OrderItem 클래스 추가
class OrderItem {
    String name;
    int price;
    int quantity;
    int num;

    OrderItem(String name, int price, int quantity,int num) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.num=num;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
    
    
}