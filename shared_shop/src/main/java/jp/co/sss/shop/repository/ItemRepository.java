package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
			@Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者,商品詳細機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);

	// ここから下は一般会員用の「新着順」および「売れ筋順」商品一覧機能用のメソッド

	/**
	 * 未削除の商品を登録日が新しい順に取得（新着順・全件）
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i WHERE i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findByDeleteFlagOrderByInsertDateDesc(@Param("deleteFlag") int deleteFlag);

	/**
	 * 特定のカテゴリかつ未削除の商品を登録日が新しい順に取得（新着順・カテゴリ絞り込み）
	 * @param categoryId カテゴリID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ的リスト
	 */
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(@Param("categoryId") int categoryId,
			@Param("deleteFlag") int deleteFlag);

	/**
	 * 未削除の商品を注文回数が多い顺に取得（売れ筋順・全件）
	 * ※OracleデータベースのGROUP BY制約を回避するため、ORDER BY内に相関サブクエリとCOUNT関数を記述
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i WHERE i.deleteFlag = :deleteFlag " +
			"ORDER BY (SELECT COUNT(oi.id) FROM OrderItem oi WHERE oi.item.id = i.id) DESC, i.id DESC")
	List<Item> findByDeleteFlagOrderBySalesDesc(@Param("deleteFlag") int deleteFlag);

	/**
	 * 特定のカテゴリ内で未削除の商品を注文回数が多い顺に取得（売れ筋順・カテゴリ絞り込み）
	 * ※OracleデータベースのGROUP BY制約を回避するため、ORDER BY内に相関サブクエリとCOUNT関数を記述
	 * @param categoryId カテゴリID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i WHERE i.category.id = :categoryId AND i.deleteFlag = :deleteFlag " +
			"ORDER BY (SELECT COUNT(oi.id) FROM OrderItem oi WHERE oi.item.id = i.id) DESC, i.id DESC")
	List<Item> findByCategoryIdAndDeleteFlagOrderBySalesDesc(@Param("categoryId") int categoryId,
			@Param("deleteFlag") int deleteFlag);

	/**
	 * 商品名（あいまい検索）と削除フラグを条件に検索
	 * 商品名、または商品名（カナ）にキーワードが含まれるものを対象とする
	 * @param name 商品名（キーワード）
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティのリスト
	 */
	@Query("SELECT i FROM Item i WHERE (i.name LIKE %:name% OR i.nameKana LIKE %:name%) AND i.deleteFlag = :deleteFlag ORDER BY i.insertDate DESC, i.id DESC")
	List<Item> findByNameContainingAndDeleteFlagOrderByInsertDateDesc(@Param("name") String name,
			@Param("deleteFlag") int deleteFlag);
}