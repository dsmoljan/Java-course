package hr.fer.zemris.java.custom.scripting.nodes;


//e sad kad imamo ovo sučelje
//i dodamo metode accept(INodeVisitor visitor) u svaku od nodova
//možemo pisati različite vrste visitora koji vrše različite funkcionalnosti
//to je ideja
public interface INodeVisitor {
	public void visitTextNode(TextNode node);
	public void visitForLoopNode(ForLoopNode node);
	public void visitEchoNode(EchoNode node);
	public void visitDocumentNode(DocumentNode node);
}
