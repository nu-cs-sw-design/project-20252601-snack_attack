package example;

import example.Analyzer.*;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class MyFirstLinter {
	/**
	 * Reads in a list of Java Classes and prints fun facts about them.
	 *
	 * For more information, read: https://asm.ow2.io/asm4-guide.pdf
	 *
	 * @param args
	 *            : the names of the classes, separated by spaces. For example:
	 *            java example.MyFirstLinter java.lang.String
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		// DONE: Learn how to create separate Run Configurations so you can run
		// your code on different programs without changing the code each time.

		for (String className : args) {
			// The 3 steps read in a Java class:
			// 1. ASM's ClassReader does the heavy lifting of parsing the compiled Java class.
			ClassReader reader = new ClassReader(className);

			// 2. ClassNode is just a data container for the parsed class
			ClassNode classNode = new ClassNode();

			// 3. Tell the Reader to parse the specified class and store its data in our ClassNode.
			// EXPAND_FRAMES means: I want my code to work. (Always pass this flag.)
			reader.accept(classNode, ClassReader.EXPAND_FRAMES);

			// Now we can navigate the classNode and look for things we are interested in.
			new ClassAnalyzer().analyze(classNode);
			new FieldAnalyzer().analyze(classNode);

			InstructionAnalyzer instructionAnalyzer = new InstructionAnalyzer();
			new MethodAnalyzer(instructionAnalyzer).analyze(classNode);
		}
	}
}