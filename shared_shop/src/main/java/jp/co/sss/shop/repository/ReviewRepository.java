package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewsテーブル用リポジトリ
 *
 * @author Jules
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品IDに紐づく未削除のレビュー一覧を取得（登録日降順）
	 * @param itemId 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return レビューリスト
	 */
	List<Review> findByItemIdAndDeleteFlagOrderByInsertDateDesc(Integer itemId, int deleteFlag);

	/**
	 * 注文IDと商品IDに紐づく未削除のレビューが存在するか確認
	 * @param orderId 注文ID
	 * @param itemId 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 存在すればtrue
	 */
	boolean existsByOrderIdAndItemIdAndDeleteFlag(Integer orderId, Integer itemId, int deleteFlag);

	/**
	 * 対象レビューの「参考になった」数を1インクリメントする
	 * @param reviewId レビューID
	 */
	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@Query("UPDATE Review r SET r.helpfulCount = r.helpfulCount + 1 WHERE r.id = :reviewId")
	void incrementHelpfulCount(@Param("reviewId") Integer reviewId);

	/**
	 * 対象レビューの「参考になった」数を1デクリメントする
	 * @param reviewId レビューID
	 */
	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@Query("UPDATE Review r SET r.helpfulCount = r.helpfulCount - 1 WHERE r.id = :reviewId")
	void decrementHelpfulCount(@Param("reviewId") Integer reviewId);

	/**
	 * 商品ごとの平均評価を取得
	 * @param itemId 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 平均評価
	 */
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId AND r.deleteFlag = :deleteFlag")
	Double getAverageRating(@Param("itemId") Integer itemId, @Param("deleteFlag") int deleteFlag);

	/**
	 * 商品ごとのレビュー数を取得
	 * @param itemId 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return レビュー数
	 */
	@Query("SELECT COUNT(r.id) FROM Review r WHERE r.item.id = :itemId AND r.deleteFlag = :deleteFlag")
	Long getReviewCount(@Param("itemId") Integer itemId, @Param("deleteFlag") int deleteFlag);

	/**
	 * 指定された商品IDリストに対するレビュー情報を一括取得する
	 * @param itemIds 商品IDのリスト
	 * @param deleteFlag 削除フラグ
	 * @return Object[]のリスト（[0]: itemId, [1]: 平均評価, [2]: レビュー数）
	 */
	@Query("SELECT r.item.id, AVG(r.rating), COUNT(r.id) FROM Review r WHERE r.item.id IN :itemIds AND r.deleteFlag = :deleteFlag GROUP BY r.item.id")
	List<Object[]> getReviewAggregatesByItemIds(@Param("itemIds") List<Integer> itemIds, @Param("deleteFlag") int deleteFlag);
}
