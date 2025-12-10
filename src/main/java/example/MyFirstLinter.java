package example;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

// FIXME: this code has TERRIBLE DESIGN all around
public class MyFirstLinter {
	
	String[] fieldForAnalysisByThisProgram = new String[1];
	
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
			printClass(classNode);

			printFields(classNode);
			
			printMethods(classNode);
		}
	}

	private static void printClass(ClassNode classNode) {
		System.out.println("Class's Internal JVM name: " + classNode.name);
		System.out.println("User-friendly name: "
				+ Type.getObjectType(classNode.name).getClassName());
		System.out.println("public? "
				+ ((classNode.access & Opcodes.ACC_PUBLIC) != 0));
		System.out.println("Extends: " + classNode.superName);
		System.out.println("Implements: " + classNode.interfaces);
		// DONE: how do I write a lint check to tell if this class has a bad name?

        String internalClassName = (Type.getObjectType(classNode.name).getClassName()).toString();
        String[] classNameArray = internalClassName.split("\\.");
        String className = classNameArray[classNameArray.length - 1];

        // lint check: name should be in PascalCase
        if (!(className).matches("([A-Z][a-z0-9]*)+")) {
            System.out.println("Error: class name is not in PascalCase");
        }

        // lint check: name should be < 50 characters
        if (className.length() > 50) {
            System.out.println("Error: class name is too long");
        }

		// Lint Check: Class Cannot Be Publicly Constructed
		boolean hasPublicConstructor = false;

		for (MethodNode methodNode : classNode.methods) {
			if (methodNode.name.equals("<init>"))
				if ((methodNode.access & Opcodes.ACC_PUBLIC) != 0)
					hasPublicConstructor = true;
		}

		if (!hasPublicConstructor)
			System.out.println("    no public constructor : ");
	}

	private static void printFields(ClassNode classNode) {
		// Print all fields (note the cast; ASM doesn't store generic data with its Lists)
		List<FieldNode> fields = (List<FieldNode>) classNode.fields;
		for (FieldNode field : fields) {
			System.out.println("	Field: " + field.name);
			System.out.println("	Internal JVM type: " + field.desc);
			System.out.println("	User-friendly type: "
					+ Type.getObjectType(field.desc).getClassName());
			// Query the access modifiers with the ACC_* constants.

			System.out.println("	public? "
					+ ((field.access & Opcodes.ACC_PUBLIC) != 0));
			// DONE: how do you tell if something has package-private access? (ie no access modifiers?)
			System.out.println("    protected? "
					+ ((field.access & Opcodes.ACC_PROTECTED) != 0));
			System.out.println("    private? "
					+ ((field.access & Opcodes.ACC_PRIVATE) != 0));
			System.out.println("    package-private? "
					+ ((field.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) == 0));

			// DONE: how do I write a lint check to tell if this field has a bad name?
			if (!field.name.matches("[a-z][a-zA-Z0-9]*")) {
				System.out.println("    bad name: " + field.name);
			}

			System.out.println();
		}
	}

	private static void printMethods(ClassNode classNode) {
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
			}

			System.out.println("	public? "
					+ ((method.access & Opcodes.ACC_PUBLIC) != 0));
			System.out.println("	static? "
					+ ((method.access & Opcodes.ACC_STATIC) != 0));
			// How do you tell if something has default access? (ie no access modifiers?)

			System.out.println();

			// Lint Check: Excessive Method Length (>50 Instructions)
			if (method.instructions.size() > 50) {
				System.out.println("    excessive method length: " + method.name + " (" + method.instructions.size() + " instructions)");
			}

			// Print the method's instructions
			printInstructions(method);
		}
	}

	private static void printInstructions(MethodNode methodNode) {
		InsnList instructions = methodNode.instructions;
		for (int i = 0; i < instructions.size(); i++) {

			// We don't know immediately what kind of instruction we have.
			AbstractInsnNode insn = instructions.get(i);

			// FIXME: Is instanceof the best way to deal with the instruction's type?
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
