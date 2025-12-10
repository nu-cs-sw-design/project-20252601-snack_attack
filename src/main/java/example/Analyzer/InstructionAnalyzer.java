package example.Analyzer;

import org.objectweb.asm.tree.*;

public class InstructionAnalyzer implements Analyzer<MethodNode> {

	@Override
	public void analyze(MethodNode methodNode) {
		InsnList instructions = methodNode.instructions;
		for (int i = 0; i < instructions.size(); i++) {

			// We don't know immediately what kind of instruction we have.
			AbstractInsnNode insn = instructions.get(i);

			if (insn instanceof MethodInsnNode) {
				// A method call of some sort; what other useful fields does this object have?
				MethodInsnNode methodCall = (MethodInsnNode) insn;
				System.out.println("		Call method: " + methodCall.owner + " "
						+ methodCall.name);
			} else if (insn instanceof VarInsnNode) {
				// Some kind of variable *LOAD or *STORE operation.
				VarInsnNode varInsn = (VarInsnNode) insn;
				int opCode = varInsn.getOpcode();
				// See VarInsnNode.setOpcode for the list of possible values of
				// opCode. These are from a variable-related subset of Java
				// opcodes.
			}
			// There are others...
			// This list of direct known subclasses may be useful:
			// http://asm.ow2.org/asm50/javadoc/user/org/objectweb/asm/tree/AbstractInsnNode.html
		}
		// DONE: how do I write a lint check to tell if this method has a bad name?

		// lint check: method name must be camel case
		if (!(methodNode.name).matches("[a-z][a-zA-Z0-9]*")) {
			System.out.println("Error: method name is not in camel case");
		}
	}
}