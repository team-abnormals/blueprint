var ASMAPI = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");

var MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");
var IincInsnNode = Java.type("org.objectweb.asm.tree.IincInsnNode");

var GET_INIT = ASMAPI.mapMethod("func_231160_c_");

function log(s) {
    print("[Abnormals Core] " + s);
}

function patch(method, name, patchFunction) {
    if (method.name !== name)
        return false;

    log("Patching method " + name + " (" + method.name + ")");
    patchFunction(method.instructions);
    return true;
}

function initializeCoreMod() {
    return {
        "CustomizeSkinScreen": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.gui.screen.CustomizeSkinScreen"
            },
            "transformer": function (classNode) {
                var methods = classNode.methods;

                for (var i in methods) {
                    if (patch(methods[i], GET_INIT, patchCreateTitle)) {
                        break;
                    }
                }
                return classNode;
            }
        }
    };
}

function patchCreateTitle(instructions) {
    var count = 0
    for (var i = 0; i < instructions.size(); i++) {
        var insn = instructions.get(i);
        if (insn.getOpcode() === Opcodes.POP && count < 2) {
            count++
            continue
        }

        if (count > 1) {
            if (count < 3) {
                count++
                continue
            }
            instructions.insertBefore(insn, new IincInsnNode(1, 1));
            instructions.insertBefore(insn, new VarInsnNode(Opcodes.ALOAD, 0));
            instructions.insertBefore(insn, new VarInsnNode(Opcodes.ILOAD, 1));
            instructions.insertBefore(insn, new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/minecraftabnormals/abnormals_core/core/util/ACHooks",
                "addSlabfishButton",
                "(Lnet/minecraft/client/gui/screen/CustomizeSkinScreen;I)V",
                false
            ));
            break
        }
    }
}