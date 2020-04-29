package ryoryo.ppa.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ClassTransformer implements IClassTransformer, Opcodes
{
//	private static final String ASM_HOOKS = "ryoryo/ppa/asm/ASMHooks";
	private static final String ASM_HOOKS = LoadingPlugin.toSlash(ASMHooks.class.getName());

	private static final String TARGET_CLASS = "net.minecraft.block.BlockPumpkin";

	public ClassTransformer()
	{
		LoadingPlugin.LOGGER.info("Starting Class Transformation");
	}

	/**
	 * PrintWriter pw = null;
	 * try
	 * {
	 * 		pw = new PrintWriter("D:/Desktop/trace.txt");
	 * }
	 * catch(FileNotFoundException e)
	 * {
	 * 		e.printStackTrace();
	 * }
	 * TraceClassVisitor cv = new TraceClassVisitor(writer, pw);
	 * reader.accept(cv, ClassReader.EXPAND_FRAMES);
	 *
	 * これでバイトコードをファイルに出力できる．
	 *
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if(!TARGET_CLASS.equals(transformedName))
			return basicClass;

		LoadingPlugin.LOGGER.info("Found: " + transformedName + "; start transforming------------------");

		//byte配列を読み込み、利用しやすい形にする。
		ClassReader reader = new ClassReader(basicClass);
		//これのvisitを呼ぶことによって情報が溜まっていく。
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		reader.accept(new CustomVisitor(name, writer), ClassReader.EXPAND_FRAMES);
		basicClass = writer.toByteArray();

		LoadingPlugin.LOGGER.info("Finish transforming--------------------------------------");

		return basicClass;
	}

	class CustomVisitor extends ClassVisitor
	{
		private static final String TARGET_METHOD = "canPlaceBlockAt";
		private static final String TARGET_METHOD_DEOBF = "func_176196_c";
		private String owner;

		public CustomVisitor(String owner, ClassVisitor visitor)
		{
			super(ASM4, visitor);
			this.owner = owner;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			if(TARGET_METHOD.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.owner, name, desc))
				|| TARGET_METHOD_DEOBF.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.owner, name, desc))
				|| name.equals(TARGET_METHOD)
				|| name.equals(TARGET_METHOD_DEOBF))
			{
				LoadingPlugin.LOGGER.info("Found: " + name + "; start transforming");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}

	/**
	 * public canPlaceBlockAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z
	 *  L0
	 *   LINENUMBER 145 L0
	 *   ALOAD 1
	 *   ALOAD 2
	 *   INVOKEVIRTUAL net/minecraft/world/World.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;
	 *   INVOKEINTERFACE net/minecraft/block/state/IBlockState.getBlock ()Lnet/minecraft/block/Block;
	 *   ALOAD 1
	 *   ALOAD 2
	 *   INVOKEVIRTUAL net/minecraft/block/Block.isReplaceable (Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Z
	 *   IFEQ L1
	 *   ALOAD 1
	 *   ALOAD 2
	 *   INVOKEVIRTUAL net/minecraft/util/math/BlockPos.down ()Lnet/minecraft/util/math/BlockPos;
	 *   GETSTATIC net/minecraft/util/EnumFacing.UP : Lnet/minecraft/util/EnumFacing;
	 *   INVOKEVIRTUAL net/minecraft/world/World.isSideSolid (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z
	 *   IFEQ L1
	 *   ICONST_1
	 *   GOTO L2
	 *  L1
	 *   FRAME FULL [net/minecraft/block/BlockPumpkin net/minecraft/world/World net/minecraft/util/math/BlockPos] []
	 *   ICONST_0
	 *  L2
	 *   FRAME FULL [net/minecraft/block/BlockPumpkin net/minecraft/world/World net/minecraft/util/math/BlockPos] [I]
	 *   IRETURN
	 *  L3
	 *   LOCALVARIABLE this Lnet/minecraft/block/BlockPumpkin; L0 L3 0
	 *   LOCALVARIABLE worldIn Lnet/minecraft/world/World; L0 L3 1
	 *   LOCALVARIABLE pos Lnet/minecraft/util/math/BlockPos; L0 L3 2
	 *   MAXSTACK = 3
	 *   MAXLOCALS = 3
	 *
	 * これを下の物に書き換える．
	 *
	 * public canPlaceBlockAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z
	 *  L0
	 *   LINENUMBER 32 L0
	 *   ALOAD 1
	 *   ALOAD 2
	 *   INVOKESTATIC ryoryo/polishedstone/asm/PSHooks.pumpkinHook (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z
	 *   IRETURN
	 *  L1
	 *   LOCALVARIABLE this Lryoryo/polishedstone/block/BlockBlackQuartz; L0 L1 0
	 *   LOCALVARIABLE world Lnet/minecraft/world/World; L0 L1 1
	 *   LOCALVARIABLE pos Lnet/minecraft/util/math/BlockPos; L0 L1 2
	 *   MAXSTACK = 2
	 *   MAXLOCALS = 3
	 *
	 * @author Toyota
	 *
	 */
	class CustomMethodVisitor extends MethodVisitor
	{
		private final MethodVisitor visitor;
		public CustomMethodVisitor(int api, MethodVisitor mv)
		{
			super(api, null);//今回はメソッド内の処理まるまる置き換えなので，nullを渡す．
			this.visitor = mv;
		}

		@Override
		public void visitCode()
		{
			LoadingPlugin.LOGGER.info("Replacing...");
			visitor.visitCode();
			visitor.visitVarInsn(ALOAD, 1);
			visitor.visitVarInsn(ALOAD, 2);
			visitor.visitMethodInsn(INVOKESTATIC,
									ASM_HOOKS,
									"pumpkinHook",
									Type.getMethodDescriptor(Type.BOOLEAN_TYPE,
															Type.getObjectType("net/minecraft/world/World"),
															Type.getObjectType("net/minecraft/util/math/BlockPos")),
									false);
			visitor.visitInsn(IRETURN);
			visitor.visitMaxs(2, 3);
			visitor.visitEnd();
		}
	}
}