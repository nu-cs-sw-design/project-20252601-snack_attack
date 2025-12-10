package example.Analyzer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassAnalyzer implements Analyzer<ClassNode> {

	@Override
	public void analyze(ClassNode classNode) {
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
}