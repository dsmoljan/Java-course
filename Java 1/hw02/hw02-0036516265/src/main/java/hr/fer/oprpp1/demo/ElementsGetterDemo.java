package hr.fer.oprpp1.demo;

import java.util.ConcurrentModificationException;


import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;
import hr.fer.oprpp1.custom.collections.Collection;
import hr.fer.oprpp1.custom.collections.ElementsGetter;
import hr.fer.oprpp1.custom.collections.LinkedListIndexedCollection;

public class ElementsGetterDemo {
	
	//in addition to this demo, class ElementsGetter also has junit test in the appropriate package
	public static void main(String[] args) {
		
		System.out.println("------------------FIRST DEMO-----------------------"); //equals to subtask 1
		Collection col = new ArrayIndexedCollection(); // npr. new ArrayIndexedCollection();
		col.add("Ivo");
		col.add("Ana");
		col.add("Jasna");
		ElementsGetter getter = col.createElementsGetter();
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Jedan element: " + getter.getNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Jedan element: " + getter.getNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Jedan element: " + getter.getNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		System.out.println("Ima nepredanih elemenata: " + getter.hasNextElement());
		
		System.out.println();
		System.out.println("------------------SECOND DEMO-----------------------"); //for subtask 2
		System.out.println();
		
		Collection col1 = new ArrayIndexedCollection();
		Collection col2 = new ArrayIndexedCollection();
		col1.add("Ivo");
		col1.add("Ana");
		col1.add("Jasna");
		col2.add("Jasmina");
		col2.add("Štefanija");
		col2.add("Karmela");
		ElementsGetter getter1 = col1.createElementsGetter();
		ElementsGetter getter2 = col1.createElementsGetter();
		ElementsGetter getter3 = col2.createElementsGetter();
		System.out.println("Jedan element: " + getter1.getNextElement());
		System.out.println("Jedan element: " + getter1.getNextElement());
		System.out.println("Jedan element: " + getter2.getNextElement());
		System.out.println("Jedan element: " + getter3.getNextElement());
		System.out.println("Jedan element: " + getter3.getNextElement());
		
		System.out.println();
		System.out.println("------------------THIRD DEMO-----------------------"); //for subtask 3
		System.out.println();
		
		System.out.println("---Testing ArrayIndexedCollection elements getter---");
		
		col = new ArrayIndexedCollection();
		col.add("Ivo");
		col.add("Ana");
		col.add("Jasna");
		getter = col.createElementsGetter();
		System.out.println("Jedan element: " + getter.getNextElement());
		System.out.println("Jedan element: " + getter.getNextElement());
		col.clear();
		try {
			System.out.println("Jedan element: " + getter.getNextElement());
		} catch(ConcurrentModificationException e) {
			System.out.println("Caught ConcurrentModificationException");
		};
		
		System.out.println("---Testing LinkedListIndexedCollection elements getter---");
		//testing illeagal modifications of the array
		col = new LinkedListIndexedCollection();
		col.add("Ivo");
		col.add("Ana");
		col.add("Jasna");

		getter = col.createElementsGetter();
		
		System.out.println("Jedan element: " + getter.getNextElement());
		System.out.println("Jedan element: " + getter.getNextElement());

		col.clear();
		
		try {
			System.out.println("Jedan element: " + getter.getNextElement());
		} catch(ConcurrentModificationException e) {
			System.out.println("Caught ConcurrentModificationException");
		};
		
		System.out.println();
		System.out.println("------------------FOURTH DEMO-----------------------"); //for subtask 3
		System.out.println();
		
		col = new ArrayIndexedCollection();
		col.add("Ivo");
		col.add("Ana");
		col.add("Jasna");
		
		getter = col.createElementsGetter();
		getter.getNextElement();
		
		getter.processRemaining(System.out::println);
		

		
		
		
		}
	
	
		
	
	
	
		

}
