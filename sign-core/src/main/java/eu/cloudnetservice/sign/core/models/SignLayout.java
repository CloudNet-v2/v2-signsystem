package eu.cloudnetservice.sign.core.models;

public final class SignLayout {

	/**
	 * blockIds are not supported in all versions, use {@link SignLayout#blockName} instead
	 */
	@Deprecated
	int blockId;
	private String name;
	private String[] signLayout;
	private String blockName;
	private int subId;

	public SignLayout(String name, String[] signLayout, int blockId, String blockName, int subId) {
		this.name = name;
		this.signLayout = signLayout;
		this.blockId = blockId;
		this.blockName = blockName;
		this.subId = subId;
	}

	public String getName() {
		return name;
	}

	public int getBlockId() {
		return blockId;
	}

	public int getSubId() {
		return subId;
	}

	public String getBlockName() {
		return blockName;
	}

	public String[] getSignLayout() {
		return signLayout;
	}
}
