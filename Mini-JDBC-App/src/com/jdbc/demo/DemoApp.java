package com.jdbc.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DemoApp {

	Connection con;
	Scanner input;
	boolean exit;
	
	DemoApp() {
		
		input = new Scanner(System.in);
		exit = false;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "root");
			if(con != null) {
				System.out.println("Connected to Inventory!");
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		while(!exit) {
			
			printMenu();
			
			System.out.print("Please input: ");
			
			String option = input.nextLine();
			
			switch(option) {
			
				case "exit" : exit = true; quitApp();  break;
			
				case "1" : addCategory(); break;
				
				case "2" : addProduct(); break;
				
				case "3" : listProduct(); break;
				
				case "4" : listCategory(); break;
				
				case "5" : listPofC(); break;
				
				case "7" : listLargestDesc(); break;
				
			}
					
		}		
		
	}
	
	public void printMenu() {
		
		System.out.println();
		System.out.println("-------------------------------");
		System.out.println("Input number or type exit to quit");
		System.out.println("-------------------------------");
		System.out.println("1. Add new category");
		System.out.println("2. Add new product");
		System.out.println("3. View product's description details");
		System.out.println("4. Listing of categories");
		System.out.println("5. Listing of all the products of a category");
		System.out.println("6. Display Average number of products among all categories");
		System.out.println("7. Display the product which has largest description");
		System.out.println("8. Delete Category");
		System.out.println("9. Delete Product");
		System.out.println("10. Remove Product from a category");
		System.out.println("11. Display most recent 5 products");
		System.out.println("");

		
	}
	
	public void addCategory() {
		
		String categoryName;
		
		System.out.print("Please enter the name of new category: ");
		
		categoryName = input.nextLine();
		
		PreparedStatement newCategory;
		
		try {
			newCategory = con.prepareStatement("insert into category (categoryName) values (?)");
			newCategory.setString(1, categoryName);
			if(newCategory.execute())
				System.out.println("New category failed.");
			else			
				System.out.println("New category created.");
			newCategory.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void addProduct() {
		
		String productName;
		String productDesc;
		double productPrice;
		int prodcatg;
		
		System.out.print("Please enter the name of new product: ");	
		productName = input.nextLine();
		
		System.out.print("Please enter the description of new product: ");	
		productDesc = input.nextLine();
		
		System.out.print("Please enter the price of new product: ");	
		productPrice = Double.parseDouble(input.nextLine());
		
		System.out.print("Please enter which category of new product belong to: ");	
		prodcatg = Integer.parseInt(input.nextLine());
		
		PreparedStatement newProduct;
		
		try {
			newProduct = con.prepareStatement("insert into product (productname, productdescription, productprice)"
											+ "values (?, ?, ?)");
			newProduct.setString(1, productName);
			newProduct.setString(2, productDesc);
			newProduct.setDouble(3, productPrice);
			if(newProduct.execute())
				System.out.println("New product failed.");
			else			
				System.out.println("New product created.");
			
			newProduct = con.prepareStatement("select max(productid) as max from product");
			ResultSet max = newProduct.executeQuery();
			int maxIndex = 0;
			while(max.next()){
				maxIndex = max.getInt(1);
				System.out.println(maxIndex);
			}
			
			newProduct = con.prepareStatement("insert into prodcatg (pid, cid) values (?, ?)");
			newProduct.setInt(1, maxIndex);
			newProduct.setInt(2, prodcatg);
			newProduct.execute();
			
			newProduct.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void listProduct() {
		try {
			
			Statement listProduct = con.createStatement();
			ResultSet products = listProduct.executeQuery("select * from product");
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				int offset = 15-products.getString(2).length();
				System.out.println(products.getString(1) + "            " + products.getString(2) + 
								new String(new char[offset]).replace("\0", " ") + products.getString(3));
			}
			products.close();
			listProduct.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listCategory() {
		try {
			
			Statement listCategory = con.createStatement();
			ResultSet categories = listCategory.executeQuery("select * from category");
			System.out.println("CategoryID    CategoryName");
			System.out.println("-------------------------------");
			while(categories.next()) {
				System.out.println(categories.getInt(1) + "             " + categories.getString(2));
			}
			categories.close();
			listCategory.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listPofC() {
		int categoryId;
		
		System.out.print("Please enter the ID of Category you want to list: ");	
		categoryId = Integer.parseInt(input.nextLine());

		PreparedStatement productOfC;
		
		try {
			productOfC = con.prepareStatement("select * from product, prodcatg " + 
											  "where productId = pid " + 
											  "having cid = ?");
			productOfC.setInt(1, categoryId);
			
			ResultSet products = productOfC.executeQuery();
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				int offset = 15-products.getString(2).length();
				System.out.println(products.getString(1) + "            " + products.getString(2) + 
								new String(new char[offset]).replace("\0", " ") + products.getString(3));
			}
			
			products.close();
			productOfC.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listLargestDesc() {
			
		try {
			
			Statement listLargestDesc = con.createStatement();
			ResultSet products = listLargestDesc.executeQuery("select * " +
															  "from product " +
															  "where length(productdescription) " + 
															  " in (select max(length(productdescription)) from product);");
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				int offset = 15-products.getString(2).length();
				System.out.println(products.getString(1) + "            " + products.getString(2) + 
								new String(new char[offset]).replace("\0", " ") + products.getString(3));
			}
			products.close();
			listLargestDesc.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void quitApp() {
		
		System.out.println("Thank you for using the App!");
		
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		
		new DemoApp();

	}

}
