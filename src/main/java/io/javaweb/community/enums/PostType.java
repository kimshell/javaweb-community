package io.javaweb.community.enums;

public enum PostType {
	
	DISCUSS("讨论"),
	
	HELP("问答求助"),
	
	LEISURE("休闲灌水"),
	
	NOTICE("社区公告"),
	
	SUGGEST("建议/BUG提交");
	
	private String desc;
	
	private PostType(String desc) {
		this.setDesc(desc);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
