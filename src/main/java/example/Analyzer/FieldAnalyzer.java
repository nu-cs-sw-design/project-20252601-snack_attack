package example.Analyzer;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FieldAnalyzer implements Analyzer<ClassNode> {

	@Override
	public void analyze(ClassNode classNode) {
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
}