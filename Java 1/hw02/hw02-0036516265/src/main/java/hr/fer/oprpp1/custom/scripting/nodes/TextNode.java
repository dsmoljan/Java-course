package hr.fer.oprpp1.custom.scripting.nodes;

/**
 * A node representing a piece of textual data.
 * @author dorian
 *
 */
public class TextNode extends Node{
	
	private String text;
	
	public TextNode(String text) { //paziti, text node ima samo konstruktor, ne i setter -> dakle, kad ga se jednom napravi, to je to
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	@Override
	public String toString() {
		String res = this.text;
		res = res.replace("\\", "\\\\");
		res = res.replace("{", "\\{");
		
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TextNode)) {
			return false;
		}
		
		return this.text.equals(((TextNode)obj).getText());
	}

}
