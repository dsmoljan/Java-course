package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

public class TreeWriter {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Očekivao sam točno 1 argument!");
			return;
		}
		
		String fileName = args[0];
		String docBody = readExample(fileName);
		
		//čim stvorimo ovaj objekt, dokument nam se isparsira, te se jave potencijalne greške
		//i sad praktički cijelu strukturu dokumenta imamo u p.getDocumentNode()
		//i onda obilazimo taj isparsirani dokument, NE trebamo ponovno pisati implementaciju dokumenta
		//visitor obilazi isparsirani dokument
		SmartScriptParser p = new SmartScriptParser(docBody);
		
		WriterVisitor visitor = new WriterVisitor();
		p.getDocumentNode().accept(visitor);
		//sad bi WriterVisitor trebao obići dokument i ispisati ga
	}
	
	private static class WriterVisitor implements INodeVisitor{

		@Override
		public void visitTextNode(TextNode node) {
			String res = node.getText();
			res = res.replace("\\", "\\\\");
			res = res.replace("{", "\\{");
			
			System.out.print(res);
		}


		@Override
		public void visitForLoopNode(ForLoopNode node) {
			String res = "{$ FOR ";
			
			res += node.getVariable().asText() + " " + node.getStartExpression().asText() + " " + node.getEndExpression().asText() + " " + node.getStepExpression().asText();
			res += "$}";
			
			System.out.print(res);
			
			
			for (int i = 0; i < node.numberOfChildren(); i++) {
				Node n = node.getChild(i);
				n.accept(this);
			}
			
			res = "{$END$}";
			
			System.out.print(res);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			String res = "{$= ";
			
			for (Element e : node.getElements()) {
				res += " " + e.asText();
			}
			res += "$}";
			
			System.out.print(res);
			
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for (int i = 0; i < node.numberOfChildren(); i++) {
				node.getChild(i).accept(this);
			}
		}
		
	}
	
	
	private static String readExample(String name) {
		try(InputStream is = TreeWriter.class.getClassLoader().getResourceAsStream("primjeri/" + name + ".txt")) {
			if(is==null) throw new RuntimeException("Datoteka primjer/" + name + ".txt je nedostupna.");
			byte[] data = is.readAllBytes();
			String text = new String(data, StandardCharsets.UTF_8);
			return text;
		} catch(IOException ex) {
			throw new RuntimeException("Greška pri čitanju datoteke.", ex);
		}
	}

}
