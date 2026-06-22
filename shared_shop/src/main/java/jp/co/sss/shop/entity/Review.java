package jp.co.sss.shop.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * レビュー情報のエンティティクラス
 *
 * @author Jules
 */
@Entity
@Table(name = "reviews")
public class Review {
	/**
	 * レビューID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reviews_gen")
	@SequenceGenerator(name = "seq_reviews_gen", sequenceName = "seq_reviews", allocationSize = 1)
	private Integer id;

	/**
	 * 注文情報
	 */
	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private Order order;

	/**
	 * 商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;

	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	/**
	 * 表示名
	 */
	@Column
	private String displayName;

	/**
	 * タイトル
	 */
	@Column
	private String title;

	/**
	 * 本文
	 */
	@Column
	private String body;

	/**
	 * 評価
	 */
	@Column
	private Integer rating;

	/**
	 * 参考になった数
	 */
	@Column
	private Integer helpfulCount;

	/**
	 * 削除フラグ 0:未削除、1:削除済み
	 */
	@Column(insertable = false)
	private Integer deleteFlag;

	/**
	 * 登録日付
	 */
	@Column(insertable = false)
	private Timestamp insertDate;

	/**
	 * レビューIDの取得
	 * @return レビューID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * レビューIDのセット
	 * @param id レビューID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 注文情報の取得
	 * @return 注文情報
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 注文情報のセット
	 * @param order 注文情報
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 商品情報の取得
	 * @return 商品情報
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * 商品情報のセット
	 * @param item 商品情報
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * 会員情報の取得
	 * @return 会員情報
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 会員情報のセット
	 * @param user 会員情報
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 表示名の取得
	 * @return 表示名
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 表示名のセット
	 * @param displayName 表示名
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * タイトルの取得
	 * @return タイトル
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * タイトルのセット
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 本文の取得
	 * @return 本文
	 */
	public String getBody() {
		return body;
	}

	/**
	 * 本文のセット
	 * @param body 本文
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * 評価の取得
	 * @return 評価
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * 評価のセット
	 * @param rating 評価
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * 参考になった数の取得
	 * @return 参考になった数
	 */
	public Integer getHelpfulCount() {
		return helpfulCount;
	}

	/**
	 * 参考になった数のセット
	 * @param helpfulCount 参考になった数
	 */
	public void setHelpfulCount(Integer helpfulCount) {
		this.helpfulCount = helpfulCount;
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

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Timestamp getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(Timestamp insertDate) {
		this.insertDate = insertDate;
	}

}
