package hr.fer.oprpp1.custom.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectStackTest {
	
	@Test
	public void testStack() {
		ObjectStack<Integer> stack = new ObjectStack<Integer>();
		stack.push(1);
		stack.push(2);
		stack.push(3);
	
		Assertions.assertEquals(3, stack.pop());
		Assertions.assertEquals(2, stack.peek());
		stack.pop();
		stack.pop();
		Assertions.assertTrue(stack.isEmpty());
	}

}
