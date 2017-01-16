package com.jdbc.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
				
			}
					
		}		
		
	}
	
	public void printMenu() {
		
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
