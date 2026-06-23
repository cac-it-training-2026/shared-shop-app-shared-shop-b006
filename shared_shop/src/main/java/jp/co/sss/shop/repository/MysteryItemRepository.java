package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.MysteryItem;

/**
 * mystery_itemsテーブル用リポジトリ
 *
 * @author SystemShared
 */
@Repository
public interface MysteryItemRepository extends JpaRepository<MysteryItem, Integer> {
	/**
	 * ランクと削除フラグを条件に景品候補を取得
	 * @param rank ランク
	 * @param deleteFlag 削除フラグ
	 * @return 景品エンティティのリスト
	 */
	List<MysteryItem> findByRankAndDeleteFlag(String rank, int deleteFlag);
}
