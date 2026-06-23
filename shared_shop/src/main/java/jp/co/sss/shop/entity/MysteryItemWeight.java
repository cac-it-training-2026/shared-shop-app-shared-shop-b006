package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * グレード別ランク出現ウェイト情報のエンティティクラス
 *
 * @author SystemShared
 */
@Entity
@Table(name = "mystery_item_weights")
public class MysteryItemWeight {
	/**
	 * ウェイトID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * ランク
	 */
	@Column
	private String rank;

	/**
	 * グレード（商品価格）
	 */
	@Column
	private Integer grade;

	/**
	 * ウェイト（出現率）
	 */
	@Column
	private Integer weight;

	/**
	 * コンストラクタ
	 */
	public MysteryItemWeight() {
	}

	/**
	 * ウェイトIDの取得
	 * @return ウェイトID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * ウェイトIDのセット
	 * @param id ウェイトID
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * グレードの取得
	 * @return グレード
	 */
	public Integer getGrade() {
		return grade;
	}

	/**
	 * グレードのセット
	 * @param grade グレード
	 */
	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	/**
	 * ウェイトの取得
	 * @return ウェイト
	 */
	public Integer getWeight() {
		return weight;
	}

	/**
	 * ウェイトのセット
	 * @param weight ウェイト
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
