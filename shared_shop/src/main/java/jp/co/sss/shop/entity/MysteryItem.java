package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * ミステリーボックス専用景品情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "mystery_items")
public class MysteryItem {
	/**
	 * 景品ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mystery_items_gen")
	@SequenceGenerator(name = "seq_mystery_items_gen", sequenceName = "seq_mystery_items", allocationSize = 1)
	private Integer id;

	/**
	 * 景品名
	 */
	@Column
	private String name;

	/**
	 * 景品説明
	 */
	@Column
	private String description;

	/**
	 * ランク
	 */
	@Column
	private String rank;

	/**
	 * 削除フラグ
	 */
	@Column(insertable = false)
	private Integer deleteFlag;

	/**
	 * コンストラクタ
	 */
	public MysteryItem() {
	}

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

	/**
	 * 削除フラグの取得
	 * @return 削除フラグ
	 */
	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * 削除フラグのセット
	 * @param deleteFlag 削除フラグ
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
}
