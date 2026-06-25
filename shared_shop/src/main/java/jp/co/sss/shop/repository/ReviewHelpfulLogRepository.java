package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.ReviewHelpfulLog;

/**
 * review_helpful_logsテーブル用リポジトリ
 *
 * @author Jules
 */
@Repository
public interface ReviewHelpfulLogRepository extends JpaRepository<ReviewHelpfulLog, Integer> {

	/**
	 * レビューIDと会員IDに紐づく「参考になった」ログが存在するか確認
	 * @param reviewId レビューID
	 * @param userId 会員ID
	 * @return 存在すればtrue
	 */
	boolean existsByReviewIdAndUserId(Integer reviewId, Integer userId);

	/**
	 * レビューIDと会員IDに紐づく「参考になった」ログを削除
	 * @param reviewId レビューID
	 * @param userId 会員ID
	 */
	@org.springframework.transaction.annotation.Transactional
	void deleteByReviewIdAndUserId(Integer reviewId, Integer userId);

	/**
	 * 会員が「参考になった」を押したレビューIDの一覧を取得
	 * @param userId 会員ID
	 * @return レビューIDリスト
	 */
	@Query("SELECT l.review.id FROM ReviewHelpfulLog l WHERE l.user.id = :userId")
	List<Integer> findReviewIdsByUserId(@Param("userId") Integer userId);
}
