package com.teamabnormals.blueprint.common.remolder.util;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.LabelNode;

/**
 * A {@link Label} extension designed to track whether it was jumped to in a molding buffer.
 *
 * @author SmellyModder (Luke Tonon)
 */
public class DiscretionaryLabel extends Label {
	public boolean jumped;

	public static DiscretionaryLabel label() {
		DiscretionaryLabel label = new DiscretionaryLabel();
		label.info = new LabelNode(label);
		return label;
	}

	public void visitIfJumped(MethodVisitor visitor) {
		if (this.jumped) {
			visitor.visitLabel(this);
		}
	}

	public void jump(MethodVisitor visitor, int opcode) {
		this.jumped = true;
		visitor.visitJumpInsn(opcode, this);
	}

	public static void tryToMark(Label label) {
		if (label instanceof DiscretionaryLabel discretionaryLabel)
			discretionaryLabel.jumped = true;
	}
}
