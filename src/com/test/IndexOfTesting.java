package com.test;

import java.util.ArrayList;
import java.util.List;

public class IndexOfTesting {

	public static void main(String[] args) {
	
		
		List<String> factory = new ArrayList<String>();
		
		factory.add("all");
		/*	factory.add("vinod");
		factory.add("srikanth");
		factory.add("all");
		*/
		
	
		if(factory.indexOf("all")==0){
			
			System.out.println("if");
		}
		else{
			
			System.out.println("else");
		}

	}

}
