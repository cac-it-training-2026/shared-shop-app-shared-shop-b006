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
	 * 注文IDと商品IDに紐づくレビューが存在するか確認
	 * @param orderId 注文ID
	 * @param itemId 商品ID
	 * @return 存在すればtrue
	 */
	boolean existsByOrderIdAndItemId(Integer orderId, Integer itemId);

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
}
