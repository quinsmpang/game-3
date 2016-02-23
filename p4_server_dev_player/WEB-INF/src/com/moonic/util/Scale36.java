package com.moonic.util;

import java.util.ArrayList;

public class Scale36 {
	public static char[] eles = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
		'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
	
	/**
	 * 将10进制转换为36进制
	 */
	public static String scale10To36(long number, long offnumber, int formatlen, int randomlen){
		number += offnumber;
		ArrayList<Integer> charlist = new ArrayList<Integer>();
		long max = 1;
		do {
			charlist.add((int)(number/max%36));
			max *= 36;
		} while(number >= max);
		while(charlist.size() < formatlen){
			charlist.add(0);
		}
		StringBuffer sb = new StringBuffer();
		for(int i = charlist.size()-1; i >= 0; i--){
			sb.append(eles[charlist.get(i)]);
		}
		for(int i = 0; i < randomlen; i++){
			sb.append(eles[MyTools.getRandom(0, eles.length-1)]);
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 将36进制转换为10进制
	 */
	public static long scale36To10(String number, long offnumber, int randomlen){
		if(randomlen > 0){
			number = number.substring(0, number.length()-randomlen);
		}
		number = number.toUpperCase();
		char[] numbers = number.toCharArray();
		long return_number = 0;
		for(int i = 0; i < numbers.length; i++){
			for(int j = 0; j < eles.length; j++){
				if(numbers[i] == eles[j]){
					return_number += j*Math.pow(36, numbers.length-1-i);
					break;
				}
			}
		}
		return_number -= offnumber;
		//System.out.println(return_number);
		return return_number;
	}
	
	public static void main(String[] args){
		String str = scale10To36(10000, 1454352854, 6, 2);
		System.out.println(str);
		long number = scale36To10(str, 1454352854, 2);
		System.out.println(number);
		//System.out.println(MyTools.getRandom(60466176, 2000000000));
	}
}
