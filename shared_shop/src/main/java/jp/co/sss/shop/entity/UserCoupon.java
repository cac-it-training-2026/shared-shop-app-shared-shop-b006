package jp.co.sss.shop.entity;

import java.sql.Date;

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
 * ユーザー獲得クーポン情報のエンティティクラス
 *
 * @author Jules
 */
@Entity
@Table(name = "user_coupons")
public class UserCoupon {
	/**
	 * クーポンID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_coupons_gen")
	@SequenceGenerator(name = "seq_user_coupons_gen", sequenceName = "seq_user_coupons", allocationSize = 1)
	private Integer id;

	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	/**
	 * 割引率
	 */
	@Column
	private Integer discountRate;

	/**
	 * 使用済みフラグ 0:未使用、1:使用済み
	 */
	@Column
	private Integer isUsed;

	/**
	 * 有効期限
	 */
	@Column
	private Date expiryDate;

	/**
	 * 登録日付
	 */
	@Column(insertable = false)
	private Date insertDate;

	/**
	 * クーポンIDの取得
	 * @return クーポンID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * クーポンIDのセット
	 * @param id クーポンID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 会員エンティティの取得
	 * @return 会員エンティティ
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 会員エンティティのセット
	 * @param user 会員エンティティ
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 割引率の取得
	 * @return 割引率
	 */
	public Integer getDiscountRate() {
		return discountRate;
	}

	/**
	 * 割引率のセット
	 * @param discountRate 割引率
	 */
	public void setDiscountRate(Integer discountRate) {
		this.discountRate = discountRate;
	}

	/**
	 * 使用済みフラグの取得
	 * @return 使用済みフラグ
	 */
	public Integer getIsUsed() {
		return isUsed;
	}

	/**
	 * 使用済みフラグのセット
	 * @param isUsed 使用済みフラグ
	 */
	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}

	/**
	 * 有効期限の取得
	 * @return 有効期限
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * 有効期限のセット
	 * @param expiryDate 有効期限
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Date getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}
}
