package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
