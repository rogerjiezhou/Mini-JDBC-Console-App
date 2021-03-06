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
	
	DemoApp() {
		
		input = new Scanner(System.in);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "root");
			if(con != null) {
				System.out.println("Connected to Inventory!");
				
			}
		} catch(Exception e) {
			e.printStackTrace();
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
			}
			
			newProduct = con.prepareStatement("insert into prodcatg (pid, cid) values (?, ?)");
			newProduct.setInt(1, maxIndex);
			newProduct.setInt(2, prodcatg);
			newProduct.execute();
			
			newProduct.close();
		} catch(Exception e) {
			System.out.println("The category does not exist.");
		}
		
	}
	
	public void listProduct() {
		try {
			
			Statement listProduct = con.createStatement();
			ResultSet products = listProduct.executeQuery("select * from product");
			System.out.println("----------------------------------------------");
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				System.out.println(products.getString(1) + 
						  new String(new char[13-products.getString(1).length()]).replace("\0", " ") +							
						  products.getString(2) + 
						  new String(new char[15-products.getString(2).length()]).replace("\0", " ") + products.getString(3));
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
			System.out.println("-------------------------------");
			System.out.println("CategoryID    CategoryName");
			System.out.println("-------------------------------");
			while(categories.next()) {
				System.out.println(categories.getInt(1) + 
							new String(new char[14-categories.getString(1).length()]).replace("\0", " ") +	
							categories.getString(2));
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
			System.out.println("----------------------------------------------");
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				System.out.println(products.getString(1) + 
						  new String(new char[13-products.getString(1).length()]).replace("\0", " ") +							
						  products.getString(2) + 
						  new String(new char[15-products.getString(2).length()]).replace("\0", " ") + products.getString(3));
			}
			
			products.close();
			productOfC.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void avgOfProduct() {
		
		PreparedStatement numOfProducts;
		PreparedStatement numOfCategories;
		
		try {
			numOfProducts = con.prepareStatement("select count(distinct productId) from product");
			numOfCategories = con.prepareStatement("select count(distinct categoryId) from category");
			
			ResultSet products = numOfProducts.executeQuery();
			ResultSet categories = numOfCategories.executeQuery();
			
			while(!products.next() || !categories.next());
			System.out.println("Average number of products among all categories: " + products.getInt(1)/categories.getInt(1));
			
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
			System.out.println("----------------------------------------------");
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				System.out.println(products.getString(1) + 
						  new String(new char[13-products.getString(1).length()]).replace("\0", " ") +							
						  products.getString(2) + 
						  new String(new char[15-products.getString(2).length()]).replace("\0", " ") + products.getString(3));
			}
			products.close();
			listLargestDesc.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteCategory() {
		
		int categoryId;
		
		System.out.print("Please enter the ID of category you want to delete: ");	
		categoryId = Integer.parseInt(input.nextLine());
		
		try {
			
			Statement deleteCategory = con.createStatement();
			
			deleteCategory.execute("delete from prodcatg where cid = " + categoryId);
			deleteCategory.execute("delete from category where categoryId = " + categoryId);
			
			deleteCategory.close();
			System.out.println("Deleting category " + categoryId);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteProduct() {
		int productId;
		
		System.out.print("Please enter the ID of product you want to delete: ");	
		productId = Integer.parseInt(input.nextLine());
		
		try {
			
			Statement deleteCategory = con.createStatement();
			
			deleteCategory.execute("delete from prodcatg where pid = " + productId);
			deleteCategory.execute("delete from product where productId = " + productId);
			
			deleteCategory.close();
			System.out.println("Deleting product " + productId);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeProduct() {
		
		int productId;
		int categoryId;
		
		System.out.print("Please enter the ID of category which the product belong to: ");	
		categoryId = Integer.parseInt(input.nextLine());
		
		System.out.print("Please enter the ID of product you want to delete: ");	
		productId = Integer.parseInt(input.nextLine());
		
		
		
		try {
			
			Statement deleteCategory = con.createStatement();
			
			deleteCategory.execute("delete from prodcatg where pid = " + productId + " and cid = " + categoryId);
			
			deleteCategory.close();
			System.out.println("Deleting product " + productId + " from category " + categoryId);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void recentFiveProduct() {
		
		try {
			
			Statement recentFiveProduct = con.createStatement();
			ResultSet products = recentFiveProduct.executeQuery("select * " +
															  "from product " +
															  "order by productId desc limit 5");														  
			System.out.println("----------------------------------------------");
			System.out.println("ProductID    ProductName    ProductDescription");
			System.out.println("----------------------------------------------");
			while(products.next()) {
				System.out.println(products.getString(1) + 
								  new String(new char[13-products.getString(1).length()]).replace("\0", " ") +							
								  products.getString(2) + 
								  new String(new char[15-products.getString(2).length()]).replace("\0", " ") + products.getString(3));
			}
			products.close();
			recentFiveProduct.close();
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
		
		DemoApp app = new DemoApp();
		boolean exit = false;
		
		Scanner input = new Scanner(System.in);
		
		while(!exit) {
			
			app.printMenu();
			
			System.out.print("Please input: ");
			
			String option = input.nextLine();
			
			switch(option) {
			
				case "exit" : exit = true; app.quitApp();  break;
			
				case "1" : app.addCategory(); break;
				
				case "2" : app.addProduct(); break;
				
				case "3" : app.listProduct(); break;
				
				case "4" : app.listCategory(); break;
				
				case "5" : app.listPofC(); break;
				
				case "6" : app.avgOfProduct(); break;
				
				case "7" : app.listLargestDesc(); break;
				
				case "8" : app.deleteCategory(); break;
				
				case "9" : app.deleteProduct(); break;
				
				case "10" : app.removeProduct(); break;
				
				case "11" : app.recentFiveProduct(); break;
				
				default : System.out.println("Invalid input, please try again."); break;
				
			}
					
		}		

	}

}
