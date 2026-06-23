package jp.co.sss.shop.bean;

/**
 * ミステリーボックス専用景品情報クラス
 *
 * @author SystemShared
 */
public class MysteryItemBean {
	/**
	 * 景品ID
	 */
	private Integer id;

	/**
	 * 景品名
	 */
	private String name;

	/**
	 * 景品説明
	 */
	private String description;

	/**
	 * ランク
	 */
	private String rank;

	/**
	 * 景品IDの取得
	 * @return 景品ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * 景品IDのセット
	 * @param id 景品ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 景品名の取得
	 * @return 景品名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 景品名のセット
	 * @param name 景品名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 景品説明の取得
	 * @return 景品説明
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 景品説明のセット
	 * @param description 景品説明
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * ランクの取得
	 * @return ランク
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * ランクのセット
	 * @param rank ランク
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}
}
