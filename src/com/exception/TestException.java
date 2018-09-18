package com.exception;

import java.util.Collections;
import java.util.List;

public class TestException {

	int i;
	
	public static void main(String[] args) {

		List<String> list=TestException.getEmployees();
		
		System.out.println(list.size() + " count");
		for(String s: list){
			
			System.out.println(s + " data");
		}
		
		System.out.println( " final data "+ list);
		
	}
	
	public static List<String> getEmployees() { 
		
	//	  List list = Collections.EMPTY_LIST; 
		List<String> list = null;
		  return list;
		  
	}

}
