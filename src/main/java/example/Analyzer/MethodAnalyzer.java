package example.Analyzer;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class MethodAnalyzer implements Analyzer<ClassNode> {

	private final InstructionAnalyzer instructionAnalyzer;

	public MethodAnalyzer(InstructionAnalyzer instructionAnalyzer) {
		this.instructionAnalyzer = instructionAnalyzer;
	}

	@Override
	public void analyze(ClassNode classNode) {
		List<MethodNode> methods = (List<MethodNode>) classNode.methods;
		for (MethodNode method : methods) {
			System.out.println("	Method: " + method.name);
			System.out
					.println("	Internal JVM method signature: " + method.desc);

			System.out.println("	Return type: "
					+ Type.getReturnType(method.desc).getClassName());

			System.out.println("	Args: ");
			for (Type argType : Type.getArgumentTypes(method.desc)) {
				System.out.println("		" + argType.getClassName());

				// DONE: what is the argument's *variable* name?
				// if complied with debug info, argument var there, else it doesn't exist
				if (method.localVariables != null) {
					for (LocalVariableNode localVariable : method.localVariables) {
						System.out.println("		" + localVariable.name);
					}
				}
			}

			System.out.println("	public? "
					+ ((method.access & Opcodes.ACC_PUBLIC) != 0));
			System.out.println("	static? "
					+ ((method.access & Opcodes.ACC_STATIC) != 0));
			// How do you tell if something has default access? (ie no access modifiers?)
			System.out.println("    default? "
					+ ((method.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) == 0));

			System.out.println();

			// Lint Check: Excessive Method Length (>50 Instructions)
			if (method.instructions.size() > 50) {
				System.out.println("    excessive method length: " + method.name + " (" + method.instructions.size() + " instructions)");
			}

			// Print the method's instructions
			instructionAnalyzer.analyze(method);

			// DONE: how do I write a lint check to tell if this method has a bad name?

			// lint check: method name must be camel case
			if (!(method.name).matches("[a-z][a-zA-Z0-9]*")) {
				System.out.println("Error: method name is not in camel case");
			}
		}
	}
}