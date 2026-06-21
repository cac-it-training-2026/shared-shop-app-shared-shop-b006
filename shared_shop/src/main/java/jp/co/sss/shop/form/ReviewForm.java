package jp.co.sss.shop.form;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * レビュー投稿用フォーム
 *
 * @author Jules
 */
public class ReviewForm implements Serializable {

	/**
	 * 注文ID
	 */
	private Integer orderId;

	/**
	 * 商品ID
	 */
	private Integer itemId;

	/**
	 * 表示名
	 */
	@NotBlank
	@Size(max = 100)
	private String displayName;

	/**
	 * 匿名フラグ
	 */
	private Boolean isAnonymous;

	/**
	 * タイトル
	 */
	@NotBlank
	@Size(max = 200)
	private String title;

	/**
	 * 本文
	 */
	@Size(max = 2000)
	private String body;

	/**
	 * 評価
	 */
	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;

	/**
	 * デフォルトコンストラクタ
	 */
	public ReviewForm() {
	}

	/**
	 * 注文IDの取得
	 * @return 注文ID
	 */
	public Integer getOrderId() {
		return orderId;
	}

	/**
	 * 注文IDのセット
	 * @param orderId 注文ID
	 */
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	/**
	 * 商品IDの取得
	 * @return 商品ID
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * 商品IDのセット
	 * @param itemId 商品ID
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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
	 * 匿名フラグの取得
	 * @return 匿名フラグ
	 */
	public Boolean getIsAnonymous() {
		return isAnonymous;
	}

	/**
	 * 匿名フラグのセット
	 * @param isAnonymous 匿名フラグ
	 */
	public void setIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}
}
