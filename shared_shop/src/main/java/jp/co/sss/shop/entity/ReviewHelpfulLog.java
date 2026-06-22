package jp.co.sss.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * レビューの「参考になった」ログエンティティクラス
 *
 * @author Jules
 */
@Entity
@Table(name = "review_helpful_logs")
public class ReviewHelpfulLog {
	/**
	 * ログID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_review_helpful_logs_gen")
	@SequenceGenerator(name = "seq_review_helpful_logs_gen", sequenceName = "seq_review_helpful_logs", allocationSize = 1)
	private Integer id;

	/**
	 * レビュー情報
	 */
	@ManyToOne
	@JoinColumn(name = "review_id", referencedColumnName = "id")
	private Review review;

	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	/**
	 * ログIDの取得
	 * @return ログID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * ログIDのセット
	 * @param id ログID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * レビュー情報の取得
	 * @return レビュー情報
	 */
	public Review getReview() {
		return review;
	}

	/**
	 * レビュー情報のセット
	 * @param review レビュー情報
	 */
	public void setReview(Review review) {
		this.review = review;
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
}
