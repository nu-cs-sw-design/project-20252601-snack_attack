package example.Analyzer;

public interface Analyzer<NodeType> {
	void analyze(NodeType target);
}